package com.osinniy.school.firebase.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.user.UserDao;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.dz.DZMapper;
import com.osinniy.school.obj.imp.ImpMapper;
import com.osinniy.school.obj.imp.Important;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.utils.Schedulers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.osinniy.school.ui.user.dashboard.DashboardFragment.TAG_METHOD_CALL;

public class FirestoreDao implements Dao {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public void getItems(WeakReference<GetItemListener> listenerRef) {
        getUserQuery()
                .get()
                .addOnSuccessListener(Schedulers.getIo(), snapshots -> {
                    List<Bindable> itemsList = parseItems(snapshots.getDocuments());
                    GetItemListener listener = listenerRef.get();
                    if (listener != null) listener.onItemsLoaded(itemsList);
                })
                .addOnFailureListener(e -> {
                    GetItemListener listener = listenerRef.get();
                    if (listener != null) listener.onItemsLoadFailed();
                });
    }


    @Override
    public ListenerRegistration listen(WeakReference<GetItemListener> listenerRef) {
        Log.d(TAG_METHOD_CALL, "Method < listen > in FirebaseDao called");
        return getUserQuery()
                .addSnapshotListener(Schedulers.getIo(), (snapshots, e) -> {
                    if (snapshots != null) {
                        List<Bindable> itemsList = parseItems(snapshots.getDocuments());
                        GetItemListener listener = listenerRef.get();
                        if (listener != null) listener.onItemsLoaded(itemsList);
                    }
                    else if (e != null) {
                        GetItemListener listener = listenerRef.get();
                        if (listener != null) listener.onItemsLoadFailed();
                    }
                });
    }


    private List<Bindable> parseItems(List<DocumentSnapshot> documents) {
        List<Bindable> itemsList = new ArrayList<>(documents.size());
        for (DocumentSnapshot snapshot : documents) {
            if (snapshot.contains(Docs.HOMEWORK))
                itemsList.add(DZMapper.restoreInstance(snapshot));
            else if (snapshot.contains(Docs.TEXT))
                itemsList.add(ImpMapper.restoreInstance(snapshot));
        }
        return itemsList;
    }


    private Query getUserQuery() {
        return firestore.collection(Docs.COL_DZ)
                .whereEqualTo(Docs.UID, user.getUid());
    }


    /**
     * @deprecated use {@link UserDao#addUser()}
     */
    @Override
    @Deprecated
    public void addInfoAboutNewUser(FirebaseUser newUser, boolean[] options) {
        Map<String, Object> user = new HashMap<>();

        user.put(Docs.EMAIL, newUser.getEmail());
        user.put(Docs.USERNAME, newUser.getDisplayName());
        user.put(Docs.UID, newUser.getUid());

        if (options[0]) {
            firestore.collection(Docs.COL_ADMINS).add(user)
                    .addOnFailureListener(e -> {
                        Log.e(Docs.TAG_FIRESTORE_WRITE, "Failed to write to collection < "
                                + Docs.COL_ADMINS + " > :", e);
                    });
        } else {
            firestore.collection(Docs.DOC_GROUP_USERS).add(user)
                    .addOnFailureListener(e -> {
                        Log.e(Docs.TAG_FIRESTORE_WRITE, "Failed to write to collection < "
                                + Docs.DOC_GROUP_USERS + " > :", e);
                    });
        }
    }


    @Override
    public void addDZ(@NonNull DZ dz) {
        String groupId = UserOptions.getCurrent().getGroupId();
        firestore.collection(groupId).document(Docs.DOC_DATA).collection(Docs.COL_DZ)
                .document(dz.getId()).set(DZMapper.createMap(dz));
    }


    @Override
    public void deleteDZ(@NonNull DZ dz) {
        String groupId = UserOptions.getCurrent().getGroupId();
        firestore.collection(groupId).document(Docs.DOC_DATA).collection(Docs.COL_DZ)
                .document(dz.getId()).delete();
    }


    @Override
    public void addImportant(@NonNull Important imp) {
        String groupId = UserOptions.getCurrent().getGroupId();
        firestore.collection(groupId).document(Docs.DOC_DATA).collection(Docs.COL_IMPORTANT)
                .document(imp.getId()).set(ImpMapper.createMap(imp));
    }


    @Override
    public void deleteImportant(@NonNull Important imp) {
        String groupId = UserOptions.getCurrent().getGroupId();
        firestore.collection(groupId).document(Docs.DOC_DATA).collection(Docs.COL_IMPORTANT)
                .document(imp.getId()).delete();
    }

}
