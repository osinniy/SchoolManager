package com.osinniy.dz.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.osinniy.dz.R;
import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.obj.imp.Important;
import com.osinniy.dz.obj.timetable.Timetable;
import com.osinniy.dz.ui.UIManager;
import com.osinniy.dz.ui.splash.SplashGroupActivity;
import com.osinniy.dz.ui.userdata.UserActivity;
import com.osinniy.dz.util.listeners.OnItemClickListener;
import com.osinniy.dz.util.listeners.OnUIChangeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardFragment extends Fragment
        implements DashboardPresenter.Listener, OnItemClickListener, OnUIChangeListener {

    public static final String TAG_METHOD_CALL = "METHOD CALL";

    private Context c;

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
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        UIManager ui = new UIManager(c, v, this);

        ui.setRecyclerView(R.id.dashboard_recycler, adapter);

        refresher = ui.setSwipeRefresh(R.id.swipe_refresh);

        CircleImageView profilePhoto = v.findViewById(R.id.profile_photo);
//        FIXME photo didn't work
        profilePhoto.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        profilePhoto.setOnClickListener(view -> startActivity(new Intent(c, UserActivity.class)));

        return v;
    }


    @Override
    public void onRefresh() {
        Timetable.refresh();
        presenter.loadDZ();
    }


    @Override
    public void onDZLoaded(List<DZ> itemsList) {
        Log.d(TAG_METHOD_CALL, "Method < onDZLoaded > in DashboardFragment called");
        refresher.setRefreshing(false);
//        adapter.submitList(itemsList);

        startActivity(new Intent(c, SplashGroupActivity.class));
    }


    @Override
    public void onItemImportantClicked(Important item) {}


    @Override
    public void onItemDZClicked(DZ item) {}


    @Override
    public void onDetach() {
        super.onDetach();
        c = null;
    }

}
