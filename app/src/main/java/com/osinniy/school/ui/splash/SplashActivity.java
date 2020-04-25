package com.osinniy.school.ui.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.osinniy.school.BuildConfig;
import com.osinniy.school.R;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.firebase.user.UserDao;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.ui.admin.AdminActivity;
import com.osinniy.school.ui.user.MainActivity;
import com.osinniy.school.utils.Schedulers;
import com.osinniy.school.utils.Util;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "Auth";
    private static final String ERROR_CODE = "firebaseUiExceptionErrorCode";
    private static final String PREF_NUMBER_OF_LAUNCHES = "numberOfLaunches";

    private static final int RC_SIGN_IN = 777;

    private final UserDao userDao = Factory.getInstance().getUserDao();

    private Runnable exitRunnable = () -> {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) prepare();
        else startAuth();
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Schedulers.getHandler().post(exitRunnable);
    }


    private void startAuth() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.mipmap.ic_launcher)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                        .build(),
                RC_SIGN_IN
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
                userDao.reloadOptions(() -> {
                    userDao.getOptions().edit()
                            .setPhotoUri(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                            .setUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                            .apply(this);
                });
                startActivity(new Intent(this, GroupsActivity.class));
            }
            else {
                if (response == null) {
                    // User pressed back button
                    Util.showToast(this, R.string.toast_sign_in_cancelled);
                }
                else if (response.getError() != null) {
                    int errorCode = response.getError().getErrorCode();
                    final FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
                    crashlytics.setCustomKey(ERROR_CODE, errorCode);

                    switch (errorCode) {
                        case ErrorCodes.NO_NETWORK:
                            Util.showToast(this, R.string.toast_no_internet_connection);
                            break;

                        case ErrorCodes.PROVIDER_ERROR: {
                            String providerName = response.getProviderType();
                            if (GoogleAuthProvider.PROVIDER_ID.equals(providerName))
                                Util.showToast(this, R.string.toast_google_error);
                            else if (FacebookAuthProvider.PROVIDER_ID.equals(providerName))
                                Util.showToast(this, R.string.toast_facebook_error);
                            else if (EmailAuthProvider.PROVIDER_ID.equals(providerName))
                                Util.showToast(this, R.string.toast_email_error);
                            crashlytics.recordException(response.getError());
                            break;
                        }

                        case ErrorCodes.DEVELOPER_ERROR:
                            Util.showToast(this, R.string.toast_developer_error);
                            crashlytics.recordException(response.getError());
                            break;

                        case ErrorCodes.UNKNOWN_ERROR:
                            Util.showToast(this, R.string.toast_unknown_error);
                            Log.e(TAG, "Sign-in error: ", response.getError());
                            crashlytics.recordException(response.getError());
                            break;

                        case ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT:
                        case ErrorCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_ERROR:
                        case ErrorCodes.EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR:
                        case ErrorCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR:
                        case ErrorCodes.EMAIL_LINK_WRONG_DEVICE_ERROR:
                        case ErrorCodes.EMAIL_MISMATCH_ERROR:
                        case ErrorCodes.ERROR_GENERIC_IDP_RECOVERABLE_ERROR:
                        case ErrorCodes.ERROR_USER_DISABLED:
                        case ErrorCodes.INVALID_EMAIL_LINK_ERROR:
                        case ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED:
                            Util.showToast(this, R.string.toast_unknown_error);
                            crashlytics.recordException(response.getError());
                            break;
                    }
                }
            }
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void prepare() {
        userDao.setOptions(UserOptions.fromShared(this));

        if (userDao.getOptions().getGroupId() != null)
            Factory.getInstance().getGroupDao().reloadGroupData();

//        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
//        crashlytics.setCrashlyticsCollectionEnabled(false);
//        SharedPreferences pref = getSharedPreferences(getString(R.string.pref_main), Context.MODE_PRIVATE);
//        int numOfLaunches = pref.getInt(PREF_NUMBER_OF_LAUNCHES, 0);
//        if (numOfLaunches % 5 == 0) {
//            crashlytics.checkForUnsentReports()
//                    .addOnSuccessListener(aBoolean -> {
//                        if (aBoolean != null) {
//                            if (aBoolean) crashlytics.sendUnsentReports();
//                        }
//                    });
//        }
//        pref.edit().putInt(PREF_NUMBER_OF_LAUNCHES, ++numOfLaunches).apply();

        startNextActivity(userDao.getOptions());
    }


    private void startNextActivity(UserOptions options) {
        if (options.getGroupId() != null) {
            if (options.isAdmin())
                startActivity(new Intent(this, AdminActivity.class));
            else
                startActivity(new Intent(this, MainActivity.class));
        }
        else startActivity(new Intent(this, GroupsActivity.class));

        finish();
    }

}
