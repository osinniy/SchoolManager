package com.osinniy.school.ui.splash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.analytics.FirebaseAnalytics;
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
import com.osinniy.school.ui.MainActivity;
import com.osinniy.school.utils.Schedulers;
import com.osinniy.school.utils.Util;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "Auth";
    private static final String ERROR_CODE = "fui_exception_error_code";

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
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
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
                        .setLogo(R.drawable.app_icon_foreground_big)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                        .build(),
                RC_SIGN_IN
        );
    }

    @Override
    @SuppressLint("SwitchIntDef")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
                userDao.reloadOptions(() -> {
                    userDao.getOptions().edit()
                            .setPhotoUri(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                            .setUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                            .apply(this);
                    userDao.addUser();
                    startNextActivity(userDao.getOptions());
                    finish();
                }, () -> {
                    Util.showToast(this, R.string.toast_sth_went_wrong_re_authenticate);
                    AuthUI.getInstance().signOut(this);
                    finish();
                });
                if (!BuildConfig.DEBUG) {
                    Bundle params = new Bundle(1);
                    params.putString(FirebaseAnalytics.Param.METHOD, response.getProviderType());
                    FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.LOGIN, params);
                }
            }
            else {
                if (response == null) {
//                  User pressed back button
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
                    }
                }
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void prepare() {
        userDao.setOptions(UserOptions.fromShared(this));

//        if (GroupManager.isUserInGroup())
//            Schedulers.getHandler().post(() -> Factory.getInstance().getGroupDao().reloadGroupData());

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


    public void startNextActivity(UserOptions options) {
        if (!options.getGroupId().equals("null"))
            startActivity(new Intent(this, MainActivity.class));
        else
            startActivity(new Intent(this, GroupsActivity.class));

        finish();
    }

}
