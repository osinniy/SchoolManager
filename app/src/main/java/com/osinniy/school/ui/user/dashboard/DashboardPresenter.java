package com.osinniy.school.ui.user.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.firestore.ListenerRegistration;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.firebase.firestore.Dao;
import com.osinniy.school.firebase.firestore.FirestoreDao;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.utils.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.List;

public class DashboardPresenter extends BasePresenter<DashboardPresenter.Listener> implements Dao.GetItemListener {

    private final FirestoreDao dao = Factory.getInstance().getFirebaseDao();

    private ListenerRegistration registration;


    DashboardPresenter(@NonNull Listener listener) {
        super(listener);
    }


    void loadItems() {
        registration = dao.listen(new WeakReference<>(this));
    }


    void stopLoadItems() {
        registration = dao.listen(new WeakReference<>(this));
    }


    @Override
    public void onItemsLoaded(List<Bindable> itemsList) {
        postOnMainThread(listener -> listener.onItemsLoaded(itemsList));
    }


    @Override
    public void onItemsLoadFailed() {
        postOnMainThread(Listener::onItemsLoadFailed);
    }


    public interface Listener extends LifecycleOwner {
        void onItemsLoaded(List<Bindable> itemsList);
        void onItemsLoadFailed();
    }

}
