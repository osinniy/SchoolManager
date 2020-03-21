package com.osinniy.dz.database.firestore;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.osinniy.dz.database.FireDocs;
import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.obj.dz.DZMapper;
import com.osinniy.dz.util.Schedulers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.osinniy.dz.ui.dashboard.DashboardFragment.TAG_METHOD_CALL;

public class FirebaseDao implements Dao {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public void getDZ(WeakReference<GetDZListener> listenerRef) {
        Task<QuerySnapshot> snapshotTask = getUserQuery().get();

        snapshotTask.addOnSuccessListener(Schedulers.getIo(), queryDocumentSnapshots -> {
            List<DZ> dzList = parseDZ(queryDocumentSnapshots.getDocuments());
            GetDZListener listener = listenerRef.get();
            if (listener != null) listener.onDZLoaded(dzList);
        });

        snapshotTask.addOnFailureListener(e -> {
            GetDZListener listener = listenerRef.get();
            if (listener != null) listener.onDZLoadFailed();
        });
    }


//    TODO shouldn't called in main thread
    @Override
    public ListenerRegistration listenDZ(WeakReference<GetDZListener> listenerRef) {
        Log.d(TAG_METHOD_CALL, "Method < listenDZ > in FirebaseDao called");
        return getUserQuery()
                .addSnapshotListener(Schedulers.getIo(), (queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots != null) {
                        List<DZ> itemsList = parseDZ(queryDocumentSnapshots.getDocuments());
                        GetDZListener listener = listenerRef.get();
                        if (listener != null) listener.onDZLoaded(itemsList);
                    }
                    else if (e != null) {
                        GetDZListener listener = listenerRef.get();
                        if (listener != null) listener.onDZLoadFailed();
                    }
                });
    }


    private List<DZ> parseDZ(List<DocumentSnapshot> documents) {
        List<DZ> dZItems = new ArrayList<>(documents.size());
        for (DocumentSnapshot snapshot : documents)
            dZItems.add(DZMapper.restoreInstance(snapshot));
        return dZItems;
    }


    private Query getUserQuery() {
        return firestore.collection(FireDocs.COL_DZ)
                .whereEqualTo(FireDocs.UID, Objects.requireNonNull(user).getUid());
    }


//    TODO rewrite to optimize for groups
    @Override
    public void addInfoAboutNewUser(FirebaseUser newUser, boolean[] additional) {
        Map<String, Object> user = new HashMap<>();

        user.put(FireDocs.EMAIL, newUser.getEmail());
        user.put(FireDocs.USERNAME, newUser.getDisplayName());
        user.put(FireDocs.UID, newUser.getUid());

        if (additional[0]) {
            firestore.collection(FireDocs.COL_ADMINS).add(user)
                    .addOnFailureListener(e -> {
                        Log.e(FireDocs.TAG_FIRESTORE_WRITE, "Failed to write to collection < "
                                + FireDocs.COL_ADMINS + " > :", e);
                    });
        } else {
            firestore.collection(FireDocs.COL_USERS).add(user)
                    .addOnFailureListener(e -> {
                        Log.e(FireDocs.TAG_FIRESTORE_WRITE, "Failed to write to collection < "
                                + FireDocs.COL_USERS + " > :", e);
                    });
        }
    }


    @Override
    public void addDZ(DZ dz) {
        firestore.collection(FireDocs.COL_DZ).document().set(DZMapper.createMap(dz));
    }


    @Override
    public void deleteDZ(DZ dz) {
        firestore.collection(FireDocs.COL_DZ).document(dz.getId()).delete();
    }

}
