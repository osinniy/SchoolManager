package com.osinniy.dz.ui.recycler;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.firebase.firestore.ListenerRegistration;
import com.osinniy.dz.database.firedz.DZDao;
import com.osinniy.dz.database.firedz.DZDaoFactory;
import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.util.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.List;

public class DZPresenter extends BasePresenter<DZPresenter.Listener> implements DZDao.GetDZListener {

    private final DZDao dao = DZDaoFactory.getInstance().getDao();

    private ListenerRegistration registration;


    public DZPresenter(@NonNull Listener listener) {
        super(listener);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void loadDZ() {
        registration = dao.listenNotes(new WeakReference<>(this));
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopLoadDZ() {
        registration = dao.listenNotes(new WeakReference<>(this));
    }


    @Override
    public void onDZLoaded(List<DZ> dzList) {
        postOnMainThread(listener -> listener.onDZLoaded(dzList));
    }


    @Override
    public void onDZLoadFailed() {

    }


    public interface Listener extends LifecycleOwner {
        void onDZLoaded(List<DZ> dzList);
    }

}
