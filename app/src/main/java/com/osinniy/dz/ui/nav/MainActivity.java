package com.osinniy.dz.ui.nav;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.osinniy.dz.R;
import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.obj.timetable.Timetable;
import com.osinniy.dz.ui.recycler.DZAdapter;
import com.osinniy.dz.ui.recycler.DZPresenter;
import com.osinniy.dz.util.OnItemClickListener;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements DZPresenter.Listener, OnItemClickListener<DZ> {

    private SwipeRefreshLayout refresh;
    private DZAdapter adapter;
    private DZPresenter presenter = new DZPresenter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        TODO change to main_activity
        setContentView(R.layout.fragment_dashboard);

//        TODO repair this method
//        initNavView();
        initRecyclerView();
        initRefreshLayout();

        Timetable.refresh();
        presenter.loadDZ();
    }


    @Override
    public void onItemClicked(DZ item) {

    }


    @Override
    public void onDZLoaded(List<DZ> dzList) {
        refresh.setRefreshing(false);
        adapter.submitList(dzList);
    }



    private void initNavView() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.dz_recycler);
        adapter = new DZAdapter(getLayoutInflater(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void initRefreshLayout() {
        refresh = findViewById(R.id.swipe_refresh);
        refresh.setRefreshing(true);
        refresh.setOnRefreshListener(presenter::loadDZ);
    }


    public MainActivity() {}

    public Context get() {
        return this;
    }
}
