package com.osinniy.school.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.osinniy.school.R;
import com.osinniy.school.firebase.groups.GroupManager;
import com.osinniy.school.ui.splash.GroupsActivity;

public class UserActivity extends AppCompatActivity {

//    sign out should be completed by calling AuthUI.getInstance().signOut(this)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);
    }


    public void onExitGroupButtonClick(View v) {
        GroupManager.exitGroup(this);
        startActivity(new Intent(this, GroupsActivity.class));
        finish();
    }

}
