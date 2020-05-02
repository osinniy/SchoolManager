package com.osinniy.school.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.osinniy.school.BuildConfig;
import com.osinniy.school.R;
import com.osinniy.school.app.ActivityStack;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.firebase.groups.GroupManager;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.ui.splash.GroupsActivity;
import com.osinniy.school.ui.splash.SplashActivity;
import com.osinniy.school.utils.Status;
import com.osinniy.school.utils.Util;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        ActivityStack.getInstance().add(this);

        Factory.getInstance().getGroupDao().getMetadata((data, e) -> {
            if (data != null)
                if (data.contains(Docs.NAME) && data.contains(Docs.CODE))
                    ((TextView) findViewById(R.id.profile_text_group)).setText(
                            String.format("%s (%s)", data.getString(Docs.NAME), data.getString(Docs.CODE))
                    );
        });

        ((TextView) findViewById(R.id.profile_username_text)).setText(UserOptions.getCurrent().getUsername());
        ((ImageView) findViewById(R.id.profile_photo)).setImageURI(UserOptions.getCurrent().getPhotoUri());
        ((TextView) findViewById(R.id.profile_text_user_id)).setText(FirebaseAuth.getInstance().getUid());
    }


    public void onEditUsernameButtonClick(View v) {
        if (Status.checkInternet(this, v)) return;

        EditText editText = findViewById(R.id.profile_username_edit);
        TextView usernameText = findViewById(R.id.profile_username_text);
        ImageButton editButt = findViewById(R.id.profile_edit_button);

        final String currentUsername = usernameText.getText().toString();
        usernameText.setVisibility(View.INVISIBLE);

        editText.setVisibility(View.VISIBLE);
        editText.setEnabled(true);
        editText.setHint(currentUsername);

        editButt.setImageDrawable(getDrawable(R.drawable.ic_done));
        editButt.setOnClickListener(view -> {
            final String newUsername = editText.getText().toString().equals("") ? currentUsername : editText.getText().toString();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUsername)
                        .build()
            );
            UserOptions.getCurrent().edit().setUsername(newUsername).apply(this);

            editText.getText().clear();
            editText.setVisibility(View.INVISIBLE);
            editText.setEnabled(false);
            usernameText.setVisibility(View.VISIBLE);
            usernameText.setText(newUsername);

            editButt.setImageDrawable(getDrawable(R.drawable.ic_edit));
            editButt.setOnClickListener(view1 -> onEditUsernameButtonClick(editButt));
        });

    }


    public void onDeleteGroupButtonClick(View v) {
        if (Status.checkInternet(this, v)) return;

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_delete_group_data)
                .setMessage(R.string.dialog_msg_delete_group_data)
                .setPositiveButton(R.string.dialog_butt_delete, (dialog, which) -> {
                    Factory.getInstance().getGroupDao().deleteAllGroupData();
                    UserOptions.getCurrent().writeToShared(this);
                    startActivity(new Intent(this, GroupsActivity.class));
                    ActivityStack.getInstance().finishAll();
                })
                .setNegativeButton(R.string.dialog_butt_cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    public void onLeaveGroupButtonClick(View v) {
        if (Status.checkInternet(this, v)) return;

        final String code = UserOptions.getCurrent().getGroupId().substring(6);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_leave_group)
                .setMessage(getString(R.string.dialog_msg_leave_group).concat(" ").concat(code))
                .setPositiveButton(R.string.dialog_butt_leave, (dialog, which) -> {
                    GroupManager.exitGroup();
                    UserOptions.getCurrent().writeToShared(this);
                    startActivity(new Intent(this, GroupsActivity.class));
                    ActivityStack.getInstance().finishAll();
                })
                .setNegativeButton(R.string.dialog_butt_cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    public void onDeleteAccountButtonClick(View v) {
        if (Status.checkInternet(this, v)) return;

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_delete_account)
                .setMessage(UserOptions.getCurrent().isAdmin() ? R.string.dialog_msg_delete_account_admin : R.string.dialog_msg_delete_account)
                .setPositiveButton(R.string.dialog_butt_delete, (dialog, which) -> {
                    AuthUI.getInstance().delete(this).addOnSuccessListener(aVoid -> {
                        if (UserOptions.getCurrent().isAdmin())
                            Factory.getInstance().getGroupDao().deleteAllGroupData();
                        UserOptions.getCurrent().writeToShared(this);
                        startActivity(new Intent(this, SplashActivity.class));
                        Util.showToast(this, R.string.toast_account_deleted);
                        if (!BuildConfig.DEBUG)
                            FirebaseAnalytics.getInstance(this).logEvent("account_deleting", null);
                        ActivityStack.getInstance().finishAll();
                    })
                    .addOnFailureListener(e -> {
                        Util.showToast(this, R.string.toast_sth_went_wrong_restart_app);
                        Util.logException("Account deleting was failed", e);
                    });
                })
                .setNegativeButton(R.string.dialog_butt_cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    public void onSignOutButtonClick(View v) {
        if (Status.checkInternet(this, v)) return;

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_sign_out)
                .setMessage(null)
                .setPositiveButton(R.string.dialog_butt_sign_out, (dialog, which) -> {
                    AuthUI.getInstance().signOut(this).addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(this, SplashActivity.class));
                        if (!BuildConfig.DEBUG)
                            FirebaseAnalytics.getInstance(this).logEvent("sign_out", null);
                        ActivityStack.getInstance().finishAll();
                    })
                    .addOnFailureListener(e -> {
                        Util.showToast(this, R.string.toast_sth_went_wrong_restart_app);
                        Util.logException("Sign out was failed", e);
                    });
                })
                .setNegativeButton(R.string.dialog_butt_cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().remove(this);
    }

}
