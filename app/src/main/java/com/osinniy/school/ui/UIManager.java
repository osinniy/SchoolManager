package com.osinniy.school.ui;

import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.osinniy.school.utils.listeners.OnUIChangeListener;

public class UIManager {

    private Context c;
    private View v;
    private OnUIChangeListener listener;


    public UIManager(Context c, View v, OnUIChangeListener UIChangeListener) {
        this.c = c;
        this.v = v;
        this.listener = UIChangeListener;
    }


    public SwipeRefreshLayout setSwipeRefresh(@IdRes int id, boolean mayRefreshNow) {
        SwipeRefreshLayout swipeRefresh = v.findViewById(id);
        swipeRefresh.setRefreshing(mayRefreshNow);
        swipeRefresh.setOnRefreshListener(() -> listener.onRefresh());
        return swipeRefresh;
    }


    public RecyclerView setRecyclerView(@IdRes int id, RecyclerView.Adapter adapter) {
        RecyclerView recyclerView = v.findViewById(id);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(c, RecyclerView.VERTICAL, false));
        return recyclerView;
    }

}
