package com.osinniy.dz.ui;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.osinniy.dz.R;
import com.osinniy.dz.util.listeners.OnUIChangeListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UIManager {

    private View v;

    private OnUIChangeListener listener;


    public UIManager(View v, OnUIChangeListener listener) {
        this.v = v;
        this.listener = listener;

        init();
    }


    private void init() {
        SwipeRefreshLayout swipeRefresh =  v.findViewById(R.id.swipe_refresh);
        swipeRefresh.setRefreshing(true);
        swipeRefresh.setOnRefreshListener(() -> {
                    new Thread(() -> listener.onRefresh()).start();
                });
    }


    public RecyclerView setRecyclerView(Context c, RecyclerView.Adapter adapter) {
        RecyclerView recyclerView = v.findViewById(R.id.dashboard_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(c, RecyclerView.VERTICAL, false));
        return recyclerView;
    }


    public CircleImageView setProfilePhoto() {
        CircleImageView profilePhoto = v.findViewById(R.id.profile_photo);
        profilePhoto.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        return profilePhoto;
    }

}
