package com.osinniy.school.background;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import javax.annotation.Nonnull;

public class FireBackgroundService extends IntentService {

    public static final String ACTION_DATABASE_CHECKER = "DatabaseChecker";


    public FireBackgroundService() {
        super("FireBackgroundService");
    }


    public static void doInBackground(Context c, @Nonnull String action) {
        switch (action) {
            case ACTION_DATABASE_CHECKER: {
                Intent i = new Intent(c, FireBackgroundService.class);
                i.setAction(ACTION_DATABASE_CHECKER);
                c.startService(i);
            } break;
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case ACTION_DATABASE_CHECKER: {
                    handleActionDatabaseChecker();
                } break;
            }
        }
    }


//    TODO add background service for check changes in firebase
//    FirebaseApp.getInstance().addBackgroundStateChangeListener();
    private void handleActionDatabaseChecker() {

    }

}
