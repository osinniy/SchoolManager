package com.osinniy.school.firebase.firestore;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;

import java.lang.ref.WeakReference;
import java.util.List;

public interface Dao {

    @Deprecated
    void addInfoAboutNewUser(FirebaseUser newUser, boolean[] additional);

    void getItems(WeakReference<GetItemListener> listener);

//    FIXME listenDZ() called 2 times during navigation
    ListenerRegistration listen(WeakReference<GetItemListener> listener);

    void addDZ(@NonNull DZ dz);

    void deleteDZ(@NonNull DZ dz);

    void addImportant(@NonNull Important imp);

    void deleteImportant(@NonNull Important imp);

    interface GetItemListener {

        void onItemsLoaded(List<Bindable> bindableList);

        void onItemsLoadFailed();

    }

}
