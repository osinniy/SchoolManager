package com.osinniy.dz.database;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.osinniy.dz.obj.DZ;

import java.lang.ref.WeakReference;
import java.util.List;

public interface DZDao {

    void getDZ(WeakReference<GetDZListener> listener);

    ListenerRegistration listenNotes(WeakReference<GetDZListener> listener);

    void addInfoAboutNewUser(FirebaseUser newUser);

    void addDZ(DZ dz);

    void deleteDZ(DZ dz);

    interface GetDZListener {

        void onDZLoaded(List<DZ> dzList);

        void onDZLoadFailed();

    }

}
