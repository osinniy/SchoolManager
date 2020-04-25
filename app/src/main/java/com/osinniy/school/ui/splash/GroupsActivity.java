package com.osinniy.school.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.osinniy.school.R;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.firebase.groups.GroupManager;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.ui.user.MainActivity;
import com.osinniy.school.utils.Schedulers;
import com.osinniy.school.utils.Status;

import java.util.ArrayList;

public class GroupsActivity extends AppCompatActivity {

    private static final String BUNDLE_CODE = "code";

    @SuppressWarnings("unchecked")
    private Runnable downloadGroupCodesRunnable = () -> {
        Status.isGroupCodesDownloaded = false;
        FirebaseFirestore.getInstance().document(Docs.REF_GROUP_CODES).get(Source.SERVER)
                .addOnSuccessListener(snapshot -> {
                    Factory.getInstance().getGroupDao().groupCodes
                            = (ArrayList<String>) snapshot.get(Docs.GROUP_CODES_ARRAY);
                    Status.isGroupCodesDownloaded = true;
                })
                .addOnFailureListener(e -> {
                    Log.e(Docs.TAG_FIRESTORE_READ, "Cannot download codes from server: ", e);
                });
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_groups);

        if (savedInstanceState != null) {
            String code = savedInstanceState.getString(BUNDLE_CODE);
            if (code != null)
                ((EditText) findViewById(R.id.edit_group_code)).setText(code);
        }

        Schedulers.getHandler().post(downloadGroupCodesRunnable);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        final String code = ((EditText) findViewById(R.id.edit_group_code)).getText().toString();
        outState.putString(BUNDLE_CODE, code);
        super.onSaveInstanceState(outState);
    }


    public void onJoinButtonClick(View v) {
        if (Status.checkUnavailableAction(this, v)) return;

        final String code = ((EditText) findViewById(R.id.edit_group_code)).getText().toString();
        if (code.length() != 6 || code.contains(" "))
            Snackbar.make(v, R.string.snackbar_enter_valid_code, Snackbar.LENGTH_LONG).show();
        else {
            if (GroupManager.tryToEnter(code)) {
                UserOptions.getCurrent().writeToShared(this);
                startMainActivity();
            }
            else Snackbar.make(v, R.string.snackbar_group_not_found, Snackbar.LENGTH_LONG).show();
        }
    }


    public void onCreateNewGroupButtonClick(View v) {
        startActivity(new Intent(this, CreateNewGroupActivity.class));
    }


    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
