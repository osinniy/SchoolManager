package com.osinniy.school.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.osinniy.school.R;
import com.osinniy.school.app.ActivityStack;

public class MainActivity extends AppCompatActivity {

    private boolean firstActivityStart;

//    private final BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (firstActivityStart) {
//                firstActivityStart = false;
//                return;
//            }
//
//            View view = findViewById(R.id.root_fragment_main);
//            if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false))
//                Snackbar.make(view, R.string.snackbar_offline_mode, Snackbar.LENGTH_SHORT).show();
//            else {
//                Snackbar.make(view, R.string.snackbar_back_online, Snackbar.LENGTH_SHORT).show();
////                DashboardFragment fragment =
////                        FragmentManager.findFragment(findViewById(R.id.dash_recycler));
////                fragment.onRefresh();
////                fragment.refresher.setRefreshing(true);
//            }
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        firstActivityStart = savedInstanceState == null;

        ActivityStack.getInstance().add(this);

        BottomNavigationView navView = findViewById(R.id.nav_view_main);
        NavController navController = Navigation.findNavController(this, R.id.root_fragment_main);
        NavigationUI.setupWithNavController(navView, navController);

//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(connectionReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().remove(this);
//        unregisterReceiver(connectionReceiver);
    }

}
