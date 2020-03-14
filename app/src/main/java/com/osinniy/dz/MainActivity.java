package com.osinniy.dz;

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
import com.osinniy.dz.obj.DZ;
import com.osinniy.dz.ui.dashboard.DZAdapter;
import com.osinniy.dz.ui.dashboard.DZPresenter;
import com.osinniy.dz.util.OnItemClickListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DZPresenter.Listener, OnItemClickListener<DZ> {

    private SwipeRefreshLayout refresh;
    private DZAdapter adapter;
    private DZPresenter presenter = new DZPresenter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initNavView();
        initRecyclerView();
        initRefreshLayout();

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

}
