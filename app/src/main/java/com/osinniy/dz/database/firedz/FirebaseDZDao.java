package com.osinniy.dz.database.firedz;

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

class FirebaseDZDao implements DZDao {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public void getDZ(WeakReference<GetDZListener> listenerRef) {
        Task<QuerySnapshot> snapshotTask = getUserDZQuery().get();

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


    @Override
    public ListenerRegistration listenNotes(WeakReference<GetDZListener> listenerRef) {
        return getUserDZQuery()
                .addSnapshotListener(Schedulers.getIo(), (queryDocumentSnapshots, e) -> {

                    if (queryDocumentSnapshots != null) {
                        List<DZ> dzList = parseDZ(queryDocumentSnapshots.getDocuments());
                        GetDZListener listener = listenerRef.get();
                        if (listener != null) listener.onDZLoaded(dzList);
                    }
                    else if (e != null) {
                        GetDZListener listener = listenerRef.get();
                        if (listener != null) listener.onDZLoadFailed();
                    }

                });
    }


    private List<DZ> parseDZ(List<DocumentSnapshot> documents) {
        List<DZ> notes = new ArrayList<>(documents.size());
        for (DocumentSnapshot snapshot : documents)
            notes.add(DZMapper.restoreInstanceFromDocument(snapshot));
        return notes;
    }


    private Query getUserDZQuery() {
        return firestore.collection(FireDocs.COL_DZ)
                .whereEqualTo(FireDocs.UID, Objects.requireNonNull(user).getUid());
    }


    public void addInfoAboutNewUser(FirebaseUser newUser) {
        new Thread(() -> {
            Map<String, String> user = new HashMap<>();

            user.put(FireDocs.EMAIL, newUser.getEmail());
            user.put(FireDocs.NAME, newUser.getDisplayName());
            user.put(FireDocs.UID, newUser.getUid());

            firestore.collection(FireDocs.COL_USERS).add(user);
        });

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
