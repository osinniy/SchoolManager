package com.osinniy.school.ui.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.osinniy.school.R;
import com.osinniy.school.app.ActivityStack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActivityStack.getInstance().add(this);

        BottomNavigationView navView = findViewById(R.id.nav_view_main);
        NavController navController = Navigation.findNavController(this, R.id.root_fragment_main);
        NavigationUI.setupWithNavController(navView, navController);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().remove(this);
    }

}
