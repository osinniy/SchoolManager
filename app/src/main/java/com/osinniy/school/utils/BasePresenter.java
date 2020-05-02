package com.osinniy.school.utils;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Future;

public abstract class BasePresenter<Listener extends LifecycleOwner> implements LifecycleObserver {

    @NonNull
    private final WeakReference<Listener> listener;

    private final Set<Future> tasks = Collections.newSetFromMap(new WeakHashMap<>());


    public BasePresenter(@NonNull Listener listener) {
        this.listener = new WeakReference<>(listener);
        listener.getLifecycle().addObserver(this);
    }


    protected void addFuture(Future<?> future) {
        tasks.add(future);
    }


    protected void postOnMainThread(ListenerRunnable<Listener> listenerRunnable) {
        Schedulers.getHandler().post(() -> {
            Listener listener = this.listener.get();
            if (listener != null) listenerRunnable.executeWithListener(listener);
        });
    }


    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        for (Future<?> future : tasks) future.cancel(true);
    }


    public interface ListenerRunnable<Listener> {
        void executeWithListener(Listener listener);
    }

}
