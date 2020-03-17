package com.osinniy.dz.database.firedz;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.osinniy.dz.obj.dz.DZ;

import java.lang.ref.WeakReference;
import java.util.List;

public interface DZDao {

    void addInfoAboutNewUser(FirebaseUser newUser);

    void getDZ(WeakReference<GetDZListener> listener);

    ListenerRegistration listenDZ(WeakReference<GetDZListener> listener);

    void addDZ(DZ dz);

    void deleteDZ(DZ dz);

    interface GetDZListener {

        void onDZLoaded(List<DZ> dzList);

        void onDZLoadFailed();

    }

}
