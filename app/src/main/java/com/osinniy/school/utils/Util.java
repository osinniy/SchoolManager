package com.osinniy.school.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.osinniy.school.BuildConfig;
import com.osinniy.school.firebase.Factory;

import java.util.Random;

public final class Util {

    public static void showToast(Context context, @StringRes int stringResId) {
        Toast.makeText(context, stringResId, Toast.LENGTH_LONG).show();
    }


    public static String generateID() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678901234567890"
                .toCharArray();
        StringBuilder builder = new StringBuilder(20);
        Random random = new Random();

        for (int i = 0; i < 20; i++) builder.append(chars[random.nextInt(chars.length)]);

        return builder.toString();
    }


    public static String generateGroupCode() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789123456789"
                .toCharArray();
        StringBuilder builder = new StringBuilder(6);
        Random random = new Random();

        for (int i = 0; i < 6; i++) builder.append(chars[random.nextInt(chars.length)]);
        String generated = builder.toString();

        if (Factory.getInstance().getGroupDao().checkCode(generated)) return generateGroupCode();
        else return generated;
    }


    public static void logException(String msg, Exception e) {
        if (BuildConfig.DEBUG) Log.e("School Manager", msg, e);
        else {
            FirebaseCrashlytics.getInstance().log(msg);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

}
