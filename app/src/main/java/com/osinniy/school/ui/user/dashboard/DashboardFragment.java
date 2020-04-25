package com.osinniy.school.ui.user.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.osinniy.school.R;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.obj.timetable.Timetable;
import com.osinniy.school.ui.UIManager;
import com.osinniy.school.ui.user.UserActivity;
import com.osinniy.school.utils.Status;
import com.osinniy.school.utils.listeners.OnItemClickListener;
import com.osinniy.school.utils.listeners.OnUIChangeListener;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardFragment extends Fragment
        implements DashboardPresenter.Listener, OnItemClickListener, OnUIChangeListener {

    public static final String TAG_METHOD_CALL = "METHOD CALL";

    private Context c;
    private View v;

    private SwipeRefreshLayout refresher;
    private DashboardAdapter adapter;
    private DashboardPresenter presenter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        c = context;
        adapter = new DashboardAdapter(getLayoutInflater(), this);
        presenter = new DashboardPresenter(this);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        UIManager ui = new UIManager(c, v, this);

        ui.setRecyclerView(R.id.dash_recycler, adapter);

        refresher = ui.setSwipeRefresh(R.id.dash_swipe_refresh);

        CircleImageView profilePhoto = v.findViewById(R.id.profile_photo);
//        FIXME photo didn't work
        profilePhoto.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        profilePhoto.setOnClickListener(view -> startActivity(new Intent(c, UserActivity.class)));

        return v;
    }


    @Override
    public void onRefresh() {
        if (Status.checkUnavailableAction(c, v)) return;

        new Loader(this).execute();
    }


    @Override
    public void onItemsLoaded(List<Bindable> itemsList) {
        Log.d(TAG_METHOD_CALL, "Method < onItemsLoaded > in DashboardFragment called");
        adapter.submitList(itemsList);
        refresher.setRefreshing(false);
    }


    @Override
    public void onItemsLoadFailed() {}


    @Override
    public void onItemImportantClicked(Important item) {}


    @Override
    public void onItemDZClicked(DZ item) {}


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
