package com.osinniy.dz.ui.nav;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.osinniy.dz.R;
import com.osinniy.dz.database.DaoFactory;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.root_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        DaoFactory.getInstance().getUserDao().isAdmin();

//        if (savedInstanceState == null) navigator.beginNav();

    }

}
