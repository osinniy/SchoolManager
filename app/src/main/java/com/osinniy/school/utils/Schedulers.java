package com.osinniy.school.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class Schedulers {

    private static volatile ScheduledThreadPoolExecutor IO;
    private static volatile Handler handler;

    public static ScheduledThreadPoolExecutor getIo() {
        if (IO == null) {
            synchronized (Schedulers.class) {
                if (IO == null)
                    IO = new ScheduledThreadPoolExecutor(
                        Runtime.getRuntime().availableProcessors()
                );
            }
        }
        return IO;
    }

    public static Handler getHandler() {
        if (handler == null) {
            synchronized (Schedulers.class) {
                if (handler == null) handler = new Handler(Looper.getMainLooper());
            }
        }
        return handler;
    }

}
