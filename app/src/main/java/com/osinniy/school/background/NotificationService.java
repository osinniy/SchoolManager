package com.osinniy.school.background;

import android.app.IntentService;
import android.content.Intent;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }


    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static final String ACTION_FOO = "com.osinniy.school.android.action.FOO";
    public static final String ACTION_BAZ = "com.osinniy.school.android.action.BAZ";

    public static final String EXTRA_PARAM1 = "com.osinniy.school.android.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.osinniy.school.android.extra.PARAM2";

}
