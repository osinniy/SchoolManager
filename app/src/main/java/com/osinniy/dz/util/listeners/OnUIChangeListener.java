package com.osinniy.dz.util.listeners;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

public interface OnUIChangeListener {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onRefresh();

}
