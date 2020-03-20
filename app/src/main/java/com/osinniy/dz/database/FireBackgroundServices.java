package com.osinniy.dz.database;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import javax.annotation.Nonnull;

public class FireBackgroundServices extends IntentService {

    public static final String ACTION_DATABASE_CHECKER = "DatabaseChecker";


    public FireBackgroundServices() {
        super("FireBackgroundService");
    }


    public static void doInBackground(Context c, @Nonnull String action) {
        switch (action) {
            case ACTION_DATABASE_CHECKER: {
                Intent i = new Intent(c, FireBackgroundServices.class);
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
    private void handleActionDatabaseChecker() {

    }

}
