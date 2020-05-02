package com.osinniy.school.ui.admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.osinniy.school.R;
import com.osinniy.school.app.ActivityStack;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);

        ActivityStack.getInstance().add(this);

        BottomNavigationView navView = findViewById(R.id.nav_view_admin);
        NavController navController = Navigation.findNavController(this, R.id.root_fragment_admin);
        NavigationUI.setupWithNavController(navView, navController);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().remove(this);
    }

}
