package com.osinniy.dz.ui;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.osinniy.dz.util.listeners.OnUIChangeListener;

public class UIManager {

    private Context c;
    private View v;
    private OnUIChangeListener listener;


    public UIManager(Context c, View v, OnUIChangeListener UIChangeListener) {
        this.c = c;
        this.v = v;
        this.listener = UIChangeListener;
    }


    public SwipeRefreshLayout setSwipeRefresh(int id) {
        SwipeRefreshLayout swipeRefresh =  v.findViewById(id);
        swipeRefresh.setRefreshing(true);
        swipeRefresh.setOnRefreshListener(() -> new Thread(() -> listener.onRefresh()).start());
        return swipeRefresh;
    }


    public RecyclerView setRecyclerView(int id, RecyclerView.Adapter adapter) {
        RecyclerView recyclerView = v.findViewById(id);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(c, RecyclerView.VERTICAL, false));
        return recyclerView;
    }

}
