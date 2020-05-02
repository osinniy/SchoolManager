package com.osinniy.school.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.osinniy.school.firebase.Factory;
import com.osinniy.school.firebase.firestore.Dao;
import com.osinniy.school.firebase.firestore.FirestoreDao;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.utils.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.List;

public class DashboardPresenter extends BasePresenter<DashboardPresenter.Listener>
        implements Dao.GetItemListener {

    private final FirestoreDao dao = Factory.getInstance().getFirestoreDao();


    DashboardPresenter(@NonNull Listener listener) {
        super(listener);
    }


    void loadItems() {
        dao.load(new WeakReference<>(this));
    }


    @Override
    public void onDZLoaded(List<DZ> dzList) {
        postOnMainThread(listener -> listener.onDZLoaded(dzList));
    }


    @Override
    public void onImportantLoaded(List<Important> importantList) {
        postOnMainThread(listener -> listener.onImportantLoaded(importantList));
    }


    @Override
    public void onItemsLoadFailed(Exception e) {
        postOnMainThread(listener -> listener.onItemsLoadFailed(e));
    }


    public interface Listener extends LifecycleOwner {
        void onDZLoaded(List<DZ> dzList);
        void onImportantLoaded(List<Important> importantList);
        void onItemsLoadFailed(Exception e);
    }

}
