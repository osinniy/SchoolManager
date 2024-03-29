package com.osinniy.school.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.osinniy.school.R;

import java.io.IOException;
import java.util.Objects;

public final class Status {

    public static final String IS_ONLINE = "online";

    public static boolean isOnline;
    public static boolean isGroupCodesDownloaded;
    public static boolean isOptionsDownloaded;


    public static boolean isOnline(Context c) {
        ConnectivityManager cm = Objects.requireNonNull(
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE)
        );
        NetworkInfo network = cm.getActiveNetworkInfo();
        isOnline = (network != null && network.isConnectedOrConnecting());
        return isOnline;
    }


    public static boolean checkInternet(Context c) {
        if (!Status.isOnline(c)) {
            Toast.makeText(c, R.string.toast_no_internet_connection, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }


    public static boolean switchMode(Context c, View v) {
        if (!Status.isOnline(c))
            Snackbar.make(v, R.string.snackbar_offline_mode, Snackbar.LENGTH_SHORT).show();
        return isOnline;
    }


    /**
     * @deprecated call {@code isOnline()} takes too long, use {@code isOnline(Context c)} instead
     */
    @Deprecated
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            isOnline = (exitValue == 0);
            return isOnline;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

}
