package com.osinniy.school.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.osinniy.school.BuildConfig;
import com.osinniy.school.R;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.obj.Bindables;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.obj.timetable.Timetable;
import com.osinniy.school.ui.ProfileActivity;
import com.osinniy.school.ui.admin.NewDZActivity;
import com.osinniy.school.ui.admin.NewImportantActivity;
import com.osinniy.school.ui.admin.TimetableActivity;
import com.osinniy.school.ui.splash.GroupsActivity;
import com.osinniy.school.utils.Schedulers;
import com.osinniy.school.utils.Status;
import com.osinniy.school.utils.Util;
import com.osinniy.school.utils.listeners.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardFragment extends Fragment
        implements DashboardPresenter.Listener, OnItemClickListener {

    public static final String TIMETABLE_TRACE_EXTRA = "com.osinniy.school.TIMETABLE_TRACE_EXTRA";

    private static final int REQUEST_NEW_DZ = 0;
    private static final int REQUEST_NEW_IMP = 1;
    private static final int REQUEST_EDIT_TIMETABLE = -1;

    private Context c;
    private View v;

    private SwipeRefreshLayout refresher;
    private DashboardAdapter adapter;
    private DashboardPresenter presenter;

    private List<Bindable> itemsList = new ArrayList<>();
    private boolean[] loadingState = new boolean[2];


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        c = context;
        adapter = new DashboardAdapter(getActivity(), this);
        presenter = new DashboardPresenter(this);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Factory.getInstance().getUserDao().setOptions(UserOptions.fromShared(c));

        boolean admin = UserOptions.getCurrent().isAdmin();

        v = inflater.inflate(admin ?
                R.layout.fragment_dashboard_admin :
                R.layout.fragment_dashboard,
                container, false);

        SharedPreferences pref = c.getSharedPreferences(getString(R.string.pref_main), Context.MODE_PRIVATE);
        if (pref.contains(GroupsActivity.GROUP_NAME_PREF))
            ((TextView) v.findViewById(R.id.dash_group_name_on_app_bar)).setText(
                    pref.getString(GroupsActivity.GROUP_NAME_PREF, null)
            );
        else
            Factory.getInstance().getGroupDao().getMetadata((data, e) -> {
                if (data != null)
                    ((TextView) v.findViewById(R.id.dash_group_name_on_app_bar)).setText(data.getString(Docs.NAME));
            });

        if (admin) {
            FloatingActionButton fabAddDZ = v.findViewById(R.id.dash_fab_add_dz);
            FloatingActionButton fabAddImportant = v.findViewById(R.id.dash_fab_add_important);
            FloatingActionButton fabTimetable = v.findViewById(R.id.dash_fab_timetable);
            fabAddDZ.setOnClickListener(this::onNewDZButtonClick);
            fabAddImportant.setOnClickListener(this::onNewImportantButtonClick);
            fabTimetable.setOnClickListener(this::onEditTimetableButtonClick);
        }

        RecyclerView recyclerView = v.findViewById(R.id.dash_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        recyclerView.setHasFixedSize(false);

        refresher = v.findViewById(R.id.dash_swipe_refresh);
        refresher.setRefreshing(Status.isOnline(c));
        refresher.setOnRefreshListener(this::onRefresh);

        CircleImageView profilePhoto = v.findViewById(R.id.profile_photo);
        profilePhoto.setOnClickListener(view -> startActivity(new Intent(c, ProfileActivity.class)));
        Uri photoUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        if (photoUri != null) {
            new Picasso.Builder(c)
                    .listener((picasso, uri, exception) -> {
                        Util.logException("Profile photo downloading failed, url = ".concat(uri.toString()), exception);
                    })
                    .build()
                    .load(photoUri)
                    .error(R.drawable.ic_user)
                    .into(profilePhoto);
        } else
            profilePhoto.setImageResource(R.drawable.ic_user);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            ImageButton darkModeSwitch = v.findViewById(R.id.dark_mode_switch);
            darkModeSwitch.setEnabled(true);
            darkModeSwitch.setVisibility(View.VISIBLE);
            darkModeSwitch.setOnClickListener(this::onDarkModeButtonClick);
        }

        if (!Status.switchMode(c, v)) {
            Schedulers.getHandler().postDelayed(() -> {
                Snackbar.make(v, R.string.snackbar_old_data, Snackbar.LENGTH_SHORT).show();
            }, 2000);
        }

        Timetable.refresh().addOnSuccessListener(snapshot -> presenter.loadItems());

        return v;
    }


    private void onRefresh() {
        if (!Status.isOnline(c)) {
            Snackbar.make(v, R.string.toast_no_internet_connection, Snackbar.LENGTH_SHORT).show();
            refresher.setRefreshing(false);
            return;
        }

        Timetable.refresh().addOnSuccessListener(snapshot -> {
            itemsList.clear();
            presenter.loadItems();
        });
    }


    @Override
    public void onDZLoaded(List<DZ> dzList) {
        if (dzList.size() > 0) {
            DZ yesterday = Bindables.findTomorrowDZ(dzList);
            if (yesterday != null) itemsList.add(yesterday);
        }

        loadingState[0] = true;

        if (loadingState[1]) {
            loadingState[0] = false;
            loadingState[1] = false;
            Collections.sort(itemsList, Bindables.BINDABLE_COMPARATOR);
            adapter.submitList(itemsList);
            adapter.notifyDataSetChanged();
            refresher.setRefreshing(false);
        }
    }


    @Override
    public void onImportantLoaded(List<Important> importantList) {
        Collections.sort(importantList, Bindables.BINDABLE_COMPARATOR);
        if (importantList.size() > 0) itemsList.add(importantList.get(0));
        if (importantList.size() > 1) itemsList.add(importantList.get(1));
        if (importantList.size() > 2) itemsList.add(importantList.get(2));

        loadingState[1] = true;

        if (loadingState[0]) {
            loadingState[0] = false;
            loadingState[1] = false;



            Collections.sort(itemsList, Bindables.BINDABLE_COMPARATOR);
            adapter.submitList(itemsList);
            adapter.notifyDataSetChanged();
            refresher.setRefreshing(false);
        }
    }


    @Override
    public void onItemsLoadFailed(Exception e) {
        Util.logException("Items loading failed", e);
    }


    @Override
    public void onItemImportantClicked(Important item) {
    }


    @Override
    public void onItemDZClicked(DZ item) {
    }


    private void onDarkModeButtonClick(View v) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        Activity activity = getActivity();
        if (activity != null) activity.recreate();
    }


    private void onNewDZButtonClick(View view) {
        startActivityForResult(new Intent(c, NewDZActivity.class), REQUEST_NEW_DZ);
    }


    private void onNewImportantButtonClick(View view) {
        startActivityForResult(new Intent(c, NewImportantActivity.class), REQUEST_NEW_IMP);
    }


    private void onEditTimetableButtonClick(View v) {
        Trace timetableActivityTrace = FirebasePerformance.startTrace("timetable_activity_start");
        startActivityForResult(
                new Intent(c, TimetableActivity.class).putExtra(
                        TIMETABLE_TRACE_EXTRA, timetableActivityTrace
                ), REQUEST_EDIT_TIMETABLE
        );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_NEW_IMP) {
                if (data != null) {
                    List<Bindable> currentList = new ArrayList<>(adapter.getCurrentList());
                    currentList.add(data.getParcelableExtra(NewImportantActivity.IMPORTANT_EXTRA));
                    Collections.sort(currentList, Bindables.BINDABLE_COMPARATOR);

                    Important lastItem = Bindables.findLastImportant(currentList);
                    if (lastItem != null) {
                        currentList.remove(lastItem);
                        Factory.getInstance().getFirestoreDao().deleteImportant(lastItem)
                                .addOnSuccessListener(aVoid -> {
                                    Snackbar.make(v, R.string.snackbar_deleted, Snackbar.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Util.showToast(c, R.string.toast_sth_went_wrong_restart_app);
                                });
                    }

                    adapter.submitList(currentList);
                    adapter.notifyDataSetChanged();

                    if (!BuildConfig.DEBUG)
                        FirebaseAnalytics.getInstance(c).logEvent("important_added", null);
                }
            }
            if (requestCode == REQUEST_EDIT_TIMETABLE) {
//                TODO: Snackbar not shows
                Snackbar.make(v, R.string.snackbar_timetable_saved, Snackbar.LENGTH_SHORT).show();

                if (!BuildConfig.DEBUG && data != null) {
                    Bundle params = new Bundle(6);
                    params.putString("for_the_first_time", String.valueOf(!data.getBooleanExtra(
                            TimetableActivity.TIMETABLE_ADDED_EXTRA, false
                    )));
                    params.putLong("monday", Timetable.getMonday().size());
                    params.putLong("tuesday", Timetable.getTuesday().size());
                    params.putLong("wednesday", Timetable.getWednesday().size());
                    params.putLong("thursday", Timetable.getThursday().size());
                    params.putLong("friday", Timetable.getFriday().size());
                    FirebaseAnalytics.getInstance(c).logEvent("timetable_change", params);
                }
            }
            if (requestCode == REQUEST_NEW_DZ) {
                if (data != null) {
//                    List<Bindable> currentList = new ArrayList<>(adapter.getCurrentList());
//                    currentList.add(data.getParcelableExtra(NewDZActivity.DZ_EXTRA));
//                    Collections.sort(currentList, Bindables.BINDABLE_COMPARATOR);
//
//                    DZ lastItem = Bindables.findLastDZ(currentList);
//                    if (lastItem != null) currentList.remove(lastItem);
//                    adapter.submitList(currentList);

                    if (!BuildConfig.DEBUG)
                        FirebaseAnalytics.getInstance(c).logEvent("homework_added", null);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v = null;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        c = null;
    }

}
