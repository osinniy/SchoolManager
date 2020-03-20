package com.osinniy.dz.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.osinniy.dz.R;
import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.obj.timetable.Timetable;
import com.osinniy.dz.ui.UIManager;
import com.osinniy.dz.ui.userdata.UserActivity;
import com.osinniy.dz.util.listeners.OnItemClickListener;
import com.osinniy.dz.util.listeners.OnUIChangeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardFragment extends Fragment
        implements DZPresenter.Listener, OnItemClickListener<DZ>, OnUIChangeListener {

    public static final String TAG_METHOD_CALL = "METHOD CALL";

    private Context c;

    private SwipeRefreshLayout refresher;
    private DZAdapter adapter;
    private DZPresenter presenter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        c = context;
        adapter = new DZAdapter(getLayoutInflater(), this);
        presenter = new DZPresenter(this);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        refresher = v.findViewById(R.id.swipe_refresh);

        UIManager ui = new UIManager(v, this);

        ui.setRecyclerView(c, adapter);

        Uri testUri = Uri.parse("https://pbs.twimg.com/profile_images/720684837363953664/F6Ks_osM.jpg");

        CircleImageView profilePhoto = v.findViewById(R.id.profile_photo);
        profilePhoto.setImageURI(testUri);
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
        adapter.submitList(itemsList);
    }


    @Override
    public void onItemClicked(DZ item) {

    }


    @Override
    public void onDetach() {
        super.onDetach();
        c = null;
    }

}
