package com.osinniy.dz.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Schedulers {

    private static final ScheduledThreadPoolExecutor IO = new ScheduledThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors()
    );

    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static ScheduledThreadPoolExecutor getIo() {
        return IO;
    }

    public static Handler getHandler() {
        return handler;
    }
}
