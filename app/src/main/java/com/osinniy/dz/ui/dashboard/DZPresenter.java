package com.osinniy.dz.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.firebase.firestore.ListenerRegistration;
import com.osinniy.dz.database.Dao;
import com.osinniy.dz.database.DaoFactory;
import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.util.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.List;

public class DZPresenter extends BasePresenter<DZPresenter.Listener> implements Dao.GetDZListener {

    private final Dao dao = DaoFactory.getInstance().getDao();

    private ListenerRegistration registration;


    DZPresenter(@NonNull Listener listener) {
        super(listener);
    }


    void loadDZ() {
        registration = dao.listenDZ(new WeakReference<>(this));
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stopLoadDZ() {
        registration = dao.listenDZ(new WeakReference<>(this));
    }


    @Override
    public void onDZLoaded(List<DZ> itemsList) {
        postOnMainThread(listener -> listener.onDZLoaded(itemsList));
    }


    @Override
    public void onDZLoadFailed() {

    }


    public interface Listener extends LifecycleOwner {
        void onDZLoaded(List<DZ> itemsList);
    }

}
