package com.osinniy.dz.database.firestore;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.osinniy.dz.obj.dz.DZ;

import java.lang.ref.WeakReference;
import java.util.List;

public interface Dao {

    void addInfoAboutNewUser(FirebaseUser newUser, boolean[] additional);

    void getDZ(WeakReference<GetDZListener> listener);

//    TODO FIX listenDZ() called 2 times during navigation
    ListenerRegistration listenDZ(WeakReference<GetDZListener> listener);

    void addDZ(DZ dz);

    void deleteDZ(DZ dz);

    interface GetDZListener {

        void onDZLoaded(List<DZ> dzList);

        void onDZLoadFailed();

    }

}
