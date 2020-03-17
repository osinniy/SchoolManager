package com.osinniy.dz.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

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


    DZPresenter(@NonNull Listener listener) {
        super(listener);
    }


    void loadDZ() {
        registration = dao.listenDZ(new WeakReference<>(this));
    }


    void stopLoadDZ() {
        registration = dao.listenDZ(new WeakReference<>(this));
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
