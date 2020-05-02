package com.osinniy.school.app;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Deque;

public class ActivityStack {

    private static ActivityStack activityStack = new ActivityStack();

    private Deque<WeakReference<Activity>> stack = new ArrayDeque<>();


    public static ActivityStack getInstance() {
        return activityStack;
    }


    public Deque<Activity> getAll() {
        Deque<Activity> strongReferences = new ArrayDeque<>(stack.size());
        for (WeakReference<Activity> weakActivity : stack) {
            Activity strongActivity = weakActivity.get();
            if (strongActivity != null) strongReferences.add(strongActivity);
        }
        return strongReferences;
    }


    public void add(Activity a) {
        stack.add(new WeakReference<>(a));
    }


    public void remove(Activity a) {
        for (WeakReference<Activity> weakActivity : stack) {
            Activity strongActivity = weakActivity.get();
            if (strongActivity != null) {
                if (a.equals(strongActivity)) stack.remove(weakActivity);
            }
        }
    }


    public void finishAll() {
        for (WeakReference<Activity> weakActivity : stack) {
            Activity strongActivity = weakActivity.get();
            if (strongActivity != null) {
                strongActivity.finish();
                stack.remove(weakActivity);
            }
        }
    }

}
