package com.osinniy.school.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.osinniy.school.firebase.Factory;

import java.util.Random;

public final class Util {

    public static void showToast(Context context, @StringRes int stringResId) {
        Toast.makeText(context, stringResId, Toast.LENGTH_LONG).show();
    }


    @Deprecated
    public static String generateID() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678901234567890"
                .toCharArray();
        StringBuilder builder = new StringBuilder(20);
        Random random = new Random();

        for (int i = 0; i < 20; i++) builder.append(chars[random.nextInt(chars.length)]);

        return builder.toString();
    }


    public static String generateGroupCode() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678901234567890"
                .toCharArray();
        StringBuilder builder = new StringBuilder(6);
        Random random = new Random();

        for (int i = 0; i < 6; i++) builder.append(chars[random.nextInt(chars.length)]);
        String generated = builder.toString();

        if (Factory.getInstance().getGroupDao().checkCode(generated)) return generateGroupCode();
        else return generated;
    }

}
