package com.osinniy.dz.util;

import android.content.Context;
import android.content.Intent;

import com.osinniy.dz.MainActivity;

public class Tools {

//    collections
    public static final String COLLECTION_SCHOOL_SCHEDULE = "school_schedule";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_DZ = "dz_store";

//    info about user
    public static final String EMAIL = "email";
    public static final String NAME  = "name";
    public static final String UID   = "uid";

//    info about dz obj
    public static final String HOMEWORK      = "homework";
    public static final String TIME          = "time";
    public static final String ID            = "id";
    public static final String IS_CHANGED    = "isChanged";

//    days of the week
    public static final String MONDAY    = "monday";
    public static final String TUESDAY   = "tuesday";
    public static final String WEDNESDAY = "wednesday";
    public static final String THURSDAY  = "thursday";
    public static final String FRIDAY    = "friday";


    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

}
