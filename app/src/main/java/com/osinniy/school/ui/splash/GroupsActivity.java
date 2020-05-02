package com.osinniy.school.ui.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.osinniy.school.BuildConfig;
import com.osinniy.school.R;
import com.osinniy.school.app.ActivityStack;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.firebase.groups.GroupManager;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.ui.admin.AdminActivity;
import com.osinniy.school.ui.user.MainActivity;
import com.osinniy.school.utils.Schedulers;
import com.osinniy.school.utils.Status;
import com.osinniy.school.utils.Util;

import java.util.ArrayList;

public class GroupsActivity extends AppCompatActivity {

    Toolbar appBar;

    @SuppressWarnings("unchecked")
    private Runnable downloadGroupCodesRunnable = () -> {
        Status.isGroupCodesDownloaded = false;
        FirebaseFirestore.getInstance()
                .document(Docs.REF_GROUP_CODES)
                .get(Source.SERVER)
                .addOnSuccessListener(snapshot -> {
                    Factory.getInstance().getGroupDao().groupCodes
                            = (ArrayList<String>) snapshot.get(Docs.GROUP_CODES_ARRAY);
                    Status.isGroupCodesDownloaded = true;
                })
                .addOnFailureListener(e -> {
                    if (Status.isOnline(this))
                        Util.logException("Cannot download codes from server", e);
                });
    };

    private BroadcastReceiver downloadCodesReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Status.isGroupCodesDownloaded) return;
            if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) return;

            downloadGroupCodesRunnable.run();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_groups);

        ActivityStack.getInstance().add(this);

        appBar = findViewById(R.id.groups_toolbar);
        appBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_join) {
                onJoinButtonClick(appBar);
                return true;
            }
            return false;
        });

        ((EditText) findViewById(R.id.edit_group_code)).addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                appBar.getMenu().findItem(R.id.menu_join).setEnabled(s.toString().length() == 6);
                ((TextInputLayout) findViewById(R.id.edit_layout_group_code)).setErrorEnabled(false);
            }
        });

        Schedulers.getHandler().post(downloadGroupCodesRunnable);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(downloadCodesReceiver, filter);
    }


    public void onJoinButtonClick(View v) {
        if (Status.checkInternet(this, v)) return;

        if (!Status.isGroupCodesDownloaded) {
            Util.showToast(this, R.string.toast_sth_went_wrong_restart_app);
            return;
        }

        Runnable successfulEnterGroupRunnable = () -> {
            UserOptions.getCurrent().writeToShared(this);

            if (UserOptions.getCurrent().isAdmin())
                startActivity(new Intent(this, AdminActivity.class));
            else
                startActivity(new Intent(this, MainActivity.class));
            finish();
        };

        String code = ((TextInputEditText) findViewById(R.id.edit_group_code)).getText().toString();
        if (GroupManager.tryToEnter(code, successfulEnterGroupRunnable)) {
            if (!BuildConfig.DEBUG) {
                Bundle params = new Bundle(1);
                params.putString(FirebaseAnalytics.Param.GROUP_ID, UserOptions.getCurrent().getGroupId());
                FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.JOIN_GROUP, params);
            }
        }
        else ((TextInputLayout) findViewById(R.id.edit_layout_group_code)).setError(getString(R.string.error_group_not_found));
    }


    public void onCreateNewGroupButtonClick(View v) {
        startActivity(new Intent(this, CreateNewGroupActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().remove(this);
        unregisterReceiver(downloadCodesReceiver);
    }

}
