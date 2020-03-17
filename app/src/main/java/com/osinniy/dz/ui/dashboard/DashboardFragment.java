package com.osinniy.dz.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.osinniy.dz.R;
import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.obj.timetable.Timetable;
import com.osinniy.dz.util.listeners.OnItemClickListener;
import com.osinniy.dz.util.listeners.OnUIChangeListener;

import java.util.List;

public class DashboardFragment extends Fragment
        implements DZPresenter.Listener, OnItemClickListener<DZ>, OnUIChangeListener {

    private Context c;

    private View v;

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
        return v = inflater.inflate(R.layout.fragment_dashboard, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        refresher = v.findViewById(R.id.swipe_refresh);
        refresher.setRefreshing(true);
        refresher.setOnRefreshListener(this::onRefresh);

        RecyclerView recycler = v.findViewById(R.id.dz_recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(c, RecyclerView.VERTICAL, false));
    }


    @Override
    public void onRefresh() {
        if (!refresher.isRefreshing()) refresher.setRefreshing(true);
        Timetable.refresh();
        presenter.loadDZ();
    }


//    TODO method is not called
    @Override
    public void onDZLoaded(List<DZ> dzList) {
        refresher.setRefreshing(false);
        adapter.submitList(dzList);
    }


    @Override
    public void onItemClicked(DZ item) {

    }


    @Override
    public void onStop() {
        super.onStop();
        presenter.stopLoadDZ();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        c = null;
        v = null;
    }

}
