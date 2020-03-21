package com.osinniy.dz.ui.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.osinniy.dz.R;
import com.osinniy.dz.database.DaoFactory;
import com.osinniy.dz.util.Schedulers;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 777;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Handler main = Schedulers.getHandler();


    private Runnable exitRunnable = () -> {
        if (auth.getCurrentUser() != null) startSecondStepActivity();
        else startAuth();
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        main.post(exitRunnable);
    }


    private void startAuth() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder()
                        .setScopes(Arrays.asList(
                                Scopes.PROFILE,
                                Scopes.EMAIL,
                                Scopes.OPEN_ID,
                                Scopes.PLUS_ME,
                                Scopes.APP_STATE))
                        .build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(),
                RC_SIGN_IN
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {

            if (resultCode == Activity.RESULT_OK) {
                DaoFactory.getInstance().getDao()
//                                                              TODO shouldn't be always true
                        .addInfoAboutNewUser(auth.getCurrentUser(), new boolean[] {true});
                startSecondStepActivity();
            }
            else finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void startSecondStepActivity() {
        startActivity(new Intent(this, SplashGroupActivity.class));
        finish();
    }

}
