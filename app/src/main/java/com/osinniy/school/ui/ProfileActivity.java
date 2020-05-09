package com.osinniy.school.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
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
import com.squareup.picasso.Picasso;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        ActivityStack.getInstance().add(this);

        ImageView profilePhoto = findViewById(R.id.profile_photo);
        Uri photoUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        if (photoUri != null) {
            new Picasso.Builder(this)
                    .listener((picasso, uri, exception) -> Util.logException("Profile photo downloading failed", exception))
                    .build()
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .error(R.drawable.ic_user)
                    .into(profilePhoto);
        } else
            profilePhoto.setImageResource(R.drawable.ic_user);

        SharedPreferences pref = getSharedPreferences(getString(R.string.pref_main), Context.MODE_PRIVATE);
        TextView groupTextView = findViewById(R.id.profile_text_group);
        groupTextView.setText(pref.getString(GroupsActivity.GROUP_NAME_PREF, ""));

        Factory.getInstance().getGroupDao().getMetadata((data, e) -> {
            if (data != null) {
                if (data.contains(Docs.NAME) && data.contains(Docs.CODE)) {
                    groupTextView.setText(
                            String.format("%s (%s)", data.getString(Docs.NAME), data.getString(Docs.CODE))
                    );
                    groupTextView.setTextSize(data.getString(Docs.NAME).length() < 17 ? 18.0F : 16.0F);
                }
            }
        });

        if (!UserOptions.getCurrent().isAdmin()) {
            ImageButton deleteGroupButt = findViewById(R.id.profile_delete_group_button);
            deleteGroupButt.setVisibility(View.GONE);
            deleteGroupButt.setEnabled(false);
        }

        ((TextView) findViewById(R.id.profile_username_text)).setText(UserOptions.getCurrent().getUsername());
        ((TextView) findViewById(R.id.profile_text_user_id)).setText(FirebaseAuth.getInstance().getUid());

        findViewById(R.id.licenses_button).setOnClickListener(v -> {
            OssLicensesMenuActivity.setActivityTitle("LicencesActivity");
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Factory.getInstance().getUserDao().setOptions(UserOptions.fromShared(this));
    }


    public void onEditUsernameButtonClick(View v) {
        if (!Status.isOnline(this)) {
            Snackbar.make(v, R.string.toast_no_internet_connection, Snackbar.LENGTH_SHORT).show();
            return;
        }

        EditText editText = findViewById(R.id.profile_username_edit);
        TextView usernameText = findViewById(R.id.profile_username_text);
        ImageButton editButt = findViewById(R.id.profile_edit_button);

        final String currentUsername = usernameText.getText().toString();
        usernameText.setVisibility(View.INVISIBLE);

        editText.setVisibility(View.VISIBLE);
        editText.setEnabled(true);
        editText.setHint(currentUsername);

        editButt.setImageDrawable(getDrawable(R.drawable.ic_done_32dp));
        editButt.setOnClickListener(view -> {
            final String newUsername =
                    editText.getText().toString().equals("") ? currentUsername : editText.getText().toString();
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

            editButt.setImageDrawable(getDrawable(R.drawable.ic_edit_silver));
            editButt.setOnClickListener(view1 -> onEditUsernameButtonClick(editButt));

            if (!newUsername.equals(currentUsername) && !BuildConfig.DEBUG) {
                Bundle params = new Bundle(1);
                params.putLong("length", newUsername.length());
                FirebaseAnalytics.getInstance(this).logEvent("username_changed", null);
            }
        });

    }


    public void onDeleteGroupButtonClick(View v) {
        if (!Status.isOnline(this)) {
            Snackbar.make(v, R.string.toast_no_internet_connection, Snackbar.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_delete_group_data)
                .setMessage(R.string.dialog_msg_delete_group_data)
                .setPositiveButton(R.string.dialog_butt_delete, (dialog, which) -> {
                    if (!BuildConfig.DEBUG) {
                        Factory.getInstance().getGroupDao().getMetadata((data, e) -> {
                            if (data != null) {
                                Bundle params = new Bundle(1);
                                long difference = new Date().getTime() -
                                        data.getTimestamp(Docs.CREATION_DATE).toDate().getTime();
                                long days = difference / 1000 / 60 / 60 / 24;
                                params.putLong("days", days);
                                FirebaseAnalytics.getInstance(this).logEvent("group_delete", params);
                            }
                        });
                    }

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
        if (!Status.isOnline(this)) {
            Snackbar.make(v, R.string.toast_no_internet_connection, Snackbar.LENGTH_SHORT).show();
            return;
        }

        final String code = UserOptions.getCurrent().getGroupId().substring(6);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_leave_group)
                .setMessage(getString(R.string.dialog_msg_leave_group).concat(" ").concat(code))
                .setPositiveButton(R.string.dialog_butt_leave, (dialog, which) -> {
                    GroupManager.exitGroup();
                    UserOptions.getCurrent().writeToShared(this);
                    startActivity(new Intent(this, GroupsActivity.class));

                    if (!BuildConfig.DEBUG)
                        FirebaseAnalytics.getInstance(this).logEvent("group_leave", null);

                    ActivityStack.getInstance().finishAll();
                })
                .setNegativeButton(R.string.dialog_butt_cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    public void onDeleteAccountButtonClick(View v) {
        if (!Status.isOnline(this)) {
            Snackbar.make(v, R.string.toast_no_internet_connection, Snackbar.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_delete_account)
                .setMessage(UserOptions.getCurrent().isAdmin() ? R.string.dialog_msg_delete_account_admin : R.string.dialog_msg_delete_account)
                .setPositiveButton(R.string.dialog_butt_delete, (dialog, which) -> {
                    if (UserOptions.getCurrent().isAdmin())
                        Factory.getInstance().getGroupDao().deleteAllGroupData();
                    Factory.getInstance().getUserDao().deleteUser();

                    AuthUI.getInstance().delete(this).addOnSuccessListener(aVoid -> {
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
        if (!Status.isOnline(this)) {
            Snackbar.make(v, R.string.toast_no_internet_connection, Snackbar.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_sign_out)
                .setMessage(R.string.dialog_text_sign_out)
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
