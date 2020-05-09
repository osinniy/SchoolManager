package com.osinniy.school.firebase.firestore;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.dz.DZMapper;
import com.osinniy.school.obj.imp.ImpMapper;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.utils.Schedulers;
import com.osinniy.school.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirestoreDao implements Dao {

    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

    private boolean isExceptionHandled;


    @Override
    public void load(WeakReference<GetItemListener> listenerRef) {
        getDZQuery().get()
                .addOnSuccessListener(Schedulers.getIo(), snapshots -> {
                    List<DZ> dzList = parseDZs(snapshots.getDocuments());
                    GetItemListener listener = listenerRef.get();
                    if (listener != null) listener.onDZLoaded(dzList);
                })
                .addOnFailureListener(Schedulers.getIo(), e -> {
                    if (isExceptionHandled) return;
                    GetItemListener listener = listenerRef.get();
                    if (listener != null) listener.onItemsLoadFailed(e);
                    isExceptionHandled = true;
                });

        getImportantQuery().get()
                .addOnSuccessListener(Schedulers.getIo(), snapshots -> {
                    List<Important> impList = parseImportants(snapshots.getDocuments());
                    GetItemListener listener = listenerRef.get();
                    if (listener != null) listener.onImportantLoaded(impList);
                })
                .addOnFailureListener(Schedulers.getIo(), e -> {
                    if (isExceptionHandled) return;
                    GetItemListener listener = listenerRef.get();
                    if (listener != null) listener.onItemsLoadFailed(e);
                    isExceptionHandled = true;
                });
    }


    private List<DZ> parseDZs(List<DocumentSnapshot> documents) {
        List<DZ> dzList = new ArrayList<>(documents.size());
        for (DocumentSnapshot doc : documents)
            dzList.add(DZMapper.restoreInstance(doc));
        return dzList;
    }


    private List<Important> parseImportants(List<DocumentSnapshot> documents) {
        List<Important> impList = new ArrayList<>(documents.size());
        for (DocumentSnapshot doc : documents)
            impList.add(ImpMapper.restoreInstance(doc));
        return impList;
    }


    private Query getImportantQuery() {
        return fs.collection(UserOptions.getCurrent().getGroupId())
                .document(Docs.DOC_DATA)
                .collection(Docs.COL_IMPORTANT);
//                .whereGreaterThan(Docs.CREATION_DATE, new Timestamp(getWeekAgoDate()));
    }


    private Query getDZQuery() {
        return fs.collection(UserOptions.getCurrent().getGroupId())
                .document(Docs.DOC_DATA)
                .collection(Docs.COL_DZ);
    }


    private Date getWeekAgoDate() {
        long weekMillis = 1000 * 60 * 60 * 24 * 7;
        return new Date(new Date().getTime() - weekMillis);
    }


    @Override
    public Task<Void> addDZ(@NonNull DZ dz) {
        String groupId = UserOptions.getCurrent().getGroupId();
        return fs.collection(groupId).document(Docs.DOC_DATA).collection(Docs.COL_DZ)
                .document(dz.getId()).set(DZMapper.createMap(dz)).addOnFailureListener(e -> {
                    Util.logException("Failed to add new 'DZ' obj", e);
                });
    }


    @Override
    public Task<Void> deleteDZ(@NonNull DZ dz) {
        String groupId = UserOptions.getCurrent().getGroupId();
        return fs.collection(groupId).document(Docs.DOC_DATA).collection(Docs.COL_DZ)
                .document(dz.getId()).delete().addOnFailureListener(e -> {
                    Util.logException("Homework deletion failed", e);
                });
    }


    @Override
    public Task<Void> addImportant(@NonNull Important imp) {
        String groupId = UserOptions.getCurrent().getGroupId();
        return fs.collection(groupId).document(Docs.DOC_DATA).collection(Docs.COL_IMPORTANT)
                .document(imp.getId()).set(ImpMapper.createMap(imp)).addOnFailureListener(e -> {
                    Util.logException("Failed to add new 'Important' obj", e);
                });
    }


    @Override
    public Task<Void> deleteImportant(@NonNull Important imp) {
        String groupId = UserOptions.getCurrent().getGroupId();
        return fs.collection(groupId).document(Docs.DOC_DATA).collection(Docs.COL_IMPORTANT)
                .document(imp.getId()).delete().addOnFailureListener(e -> {
                    Util.logException("Important deletion failed", e);
                });
    }

}
