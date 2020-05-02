package com.osinniy.school.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.osinniy.school.R;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.obj.timetable.Timetable;
import com.osinniy.school.ui.ProfileActivity;
import com.osinniy.school.ui.UIManager;
import com.osinniy.school.utils.Status;
import com.osinniy.school.utils.Util;
import com.osinniy.school.utils.listeners.OnItemClickListener;
import com.osinniy.school.utils.listeners.OnUIChangeListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardFragment extends Fragment
        implements DashboardPresenter.Listener, OnItemClickListener, OnUIChangeListener {

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
        adapter = new DashboardAdapter(getLayoutInflater(), this, c);
        presenter = new DashboardPresenter(this);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        boolean admin = UserOptions.getCurrent().isAdmin();

        v = inflater.inflate(admin ?
                R.layout.fragment_dashboard_admin :
                R.layout.fragment_dashboard,
                container, false);

        Factory.getInstance().getGroupDao().getMetadata((data, e) -> {
            if (data != null)
                ((Toolbar) v.findViewById(R.id.dash_toolbar)).setTitle(data.getString(Docs.NAME));
            else if (e != null)
                if (Status.isOnline(c))
                    Util.logException("Group metadata load failed", e);
        });

        if (admin) {
            FloatingActionButton fabAddDZ = v.findViewById(R.id.dashboard_fab_add_dz);
            FloatingActionButton fabAddImportant = v.findViewById(R.id.dashboard_fab_add_important);
            fabAddDZ.setOnClickListener(this::onNewDZButtonClick);
            fabAddImportant.setOnClickListener(this::onNewImportantButtonClick);
        }

        UIManager ui = new UIManager(c, v, this);

        ui.setRecyclerView(R.id.dash_recycler, adapter);

        refresher = ui.setSwipeRefresh(R.id.dash_swipe_refresh, Status.isOnline(c));

        CircleImageView profilePhoto = v.findViewById(R.id.profile_photo);
//        FIXME photo didn't work
        profilePhoto.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        profilePhoto.setOnClickListener(view -> startActivity(new Intent(c, ProfileActivity.class)));

        Status.switchMode(c, v);

        return v;
    }


    @Override
    public void onRefresh() {
        if (Status.checkInternet(c, v)) return;

        new Loader(this).execute();
    }


    @Override
    public void onDZLoaded(List<DZ> dzList) {
        itemsList.add(dzList.get(0));
        loadingState[0] = true;

        if (loadingState[1]) adapter.submitList(itemsList);
    }


    @Override
    public void onImportantLoaded(List<Important> importantList) {
        itemsList.add(importantList.get(0));
        loadingState[1] = true;

        if (loadingState[0]) adapter.submitList(itemsList);
    }


    @Override
    public void onItemsLoadFailed(Exception e) {
        Util.logException("Items loading failed: ", e);
    }


    @Override
    public void onItemImportantClicked(Important item) {}


    @Override
    public void onItemDZClicked(DZ item) {}


    private void onNewDZButtonClick(View view) {}


    private void onNewImportantButtonClick(View view) {}


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


    private static class Loader extends AsyncTask<Void, Void, Void> {

        WeakReference<DashboardFragment> fragment;

        Loader(DashboardFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            DashboardFragment fragment = this.fragment.get();
            if (fragment == null) return;

            fragment.refresher.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DashboardFragment fragment = this.fragment.get();
            if (fragment != null) {
                Timetable.refresh();
                fragment.presenter.loadItems();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DashboardFragment fragment = this.fragment.get();
            if (fragment == null) return;

            fragment.refresher.setRefreshing(false);
        }

    }

}
