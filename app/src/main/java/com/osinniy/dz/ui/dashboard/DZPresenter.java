package com.osinniy.dz.ui.dashboard;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.firebase.firestore.ListenerRegistration;
import com.osinniy.dz.database.DaoFactory;
import com.osinniy.dz.database.firestore.Dao;
import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.util.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.osinniy.dz.ui.dashboard.DashboardFragment.TAG_METHOD_CALL;

public class DZPresenter extends BasePresenter<DZPresenter.Listener> implements Dao.GetDZListener {

    private final Dao dao = DaoFactory.getInstance().getDao();

    private ListenerRegistration registration;


    DZPresenter(@NonNull Listener listener) {
        super(listener);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void loadDZ() {
        registration = dao.listenDZ(new WeakReference<>(this));
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void stopLoadDZ() {
        registration = dao.listenDZ(new WeakReference<>(this));
    }


    @Override
    public void onDZLoaded(List<DZ> itemsList) {
        postOnMainThread(listener -> listener.onDZLoaded(itemsList));
    }


    @Override
    public void onDZLoadFailed() {
        Log.d(TAG_METHOD_CALL, "Method < onDZLoadFailed > in DZPresenter called");
    }


    public interface Listener extends LifecycleOwner {
        void onDZLoaded(List<DZ> itemsList);
    }

}
