package com.osinniy.school.firebase.groups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.obj.groups.Group;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.utils.Util;
import com.osinniy.school.utils.listeners.OnDataLoadListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.osinniy.school.firebase.Docs.NUM_OF_MEMBERS;
import static com.osinniy.school.obj.timetable.Timetable.FRIDAY;
import static com.osinniy.school.obj.timetable.Timetable.MONDAY;
import static com.osinniy.school.obj.timetable.Timetable.THURSDAY;
import static com.osinniy.school.obj.timetable.Timetable.TUESDAY;
import static com.osinniy.school.obj.timetable.Timetable.WEDNESDAY;

@SuppressWarnings("unchecked")
public class GroupDao {

    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

    public ArrayList<String> groupCodes;


    Group addGroup(Map<String, Object> metadata) {
        String code = Util.generateGroupCode();
        metadata.put(Docs.CODE, code);
        String groupId = "Group_".concat(code);

        CollectionReference thisGroup = fs.collection(groupId);

        final String uid = FirebaseAuth.getInstance().getUid();

        thisGroup.document(Docs.DOC_GROUP_USERS).set(
                Collections.singletonMap(Docs.USERS_ARRAY, Collections.singletonList(uid))
        );

        fs.document(Docs.REF_GROUP_CODES).get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> codes = getGroupCodes(snapshot);
                    codes.add(code);
                    fs.document(Docs.REF_GROUP_CODES).update(Docs.GROUP_CODES_ARRAY, codes);
                });

        thisGroup.document(Docs.DOC_METADATA).set(metadata);

        Map<String, Object> timetable = new ArrayMap<>(5);
        timetable.put(MONDAY, Collections.emptyList());
        timetable.put(TUESDAY, Collections.emptyList());
        timetable.put(WEDNESDAY, Collections.emptyList());
        timetable.put(THURSDAY, Collections.emptyList());
        timetable.put(FRIDAY, Collections.emptyList());
        thisGroup.document(Docs.DOC_TIMETABLE).set(timetable);

        UserOptions.getCurrent().edit()
                .setGroupId(groupId)
                .setAdmin(true)
                .commit();

        Factory.getInstance().getUserDao().updateOptions(UserOptions.getCurrent());

        ArrayList<String> uids = new ArrayList<>(Collections.singletonList(uid));

        return new Group(code, uids, metadata);
    }


    boolean enterGroup(@NonNull String code, @Nullable Runnable successCode) {
        if (!checkCode(code)) return false;

        String groupId = "Group_".concat(code);

        UserOptions.Editor optionsEditor = UserOptions.getCurrent().edit();

        fs.collection(groupId)
                .document(Docs.DOC_GROUP_USERS)
                .get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> uids = getUids(snapshot);
                    final String uid = FirebaseAuth.getInstance().getUid();
                    AtomicBoolean newUser = new AtomicBoolean(false);

                    if (!uids.contains(uid)) {
                        newUser.set(true);
                        uids.add(uid);
                        fs.collection(groupId).document(Docs.DOC_GROUP_USERS).set(
                                Collections.singletonMap(Docs.USERS_ARRAY, uids)
                        );
                    }

                    fs.collection(groupId)
                            .document(Docs.DOC_METADATA)
                            .get()
                            .addOnSuccessListener(metadataSnapshot -> {
                                if (metadataSnapshot.contains(NUM_OF_MEMBERS)) {
                                    long numOfMembers = metadataSnapshot.getLong(NUM_OF_MEMBERS);
                                    if (newUser.get())
                                        fs.collection(groupId).document(Docs.DOC_METADATA)
                                                .update(NUM_OF_MEMBERS, ++numOfMembers);

                                    optionsEditor.setAdmin(
                                            uid.equals(metadataSnapshot.getString(Docs.ADMIN_ID))
                                    )
                                    .setGroupId(groupId).commit();

                                    Factory.getInstance().getUserDao().updateOptions(UserOptions.getCurrent());

                                    if (successCode != null) successCode.run();
                                }
                            })
                            .addOnFailureListener(e -> Util.logException("Metadata load failed", e));
                    });
        return true;
    }


    public boolean checkCode(@NonNull String code) {
        for (String c : groupCodes)
            if (c.equals(code)) return true;
        return false;
    }


    void exitGroup() {
        String groupId = UserOptions.getCurrent().getGroupId();
        if (groupId.equals("null")) return;

        fs.collection(groupId)
                 .document(Docs.DOC_GROUP_USERS)
                 .get()
                 .addOnSuccessListener(snapshot -> {
                     ArrayList<String> uids = getUids(snapshot);

                     final String uid = FirebaseAuth.getInstance().getUid();
                     if (uids.contains(uid)) {
                         uids.remove(uid);
                         fs.collection(groupId).document(Docs.DOC_GROUP_USERS).set(
                                 Collections.singletonMap(Docs.USERS_ARRAY, uids)
                         );
                     }
                 });

        Factory.getInstance().getUserDao().updateOptions(
                UserOptions.getCurrent().edit().setGroupId("null").commit()
        );

//        GroupManager.setCurrentGroup(null);
    }


//    public void reloadGroupData() {
//        String groupId = UserOptions.getCurrent().getGroupId();
//        fs.collection(groupId)
//                .document(Docs.DOC_GROUP_USERS)
//                .get()
//                .addOnSuccessListener(usersSnapshot -> {
//                    ArrayList<String> uids = getUids(usersSnapshot);
//
//                    fs.collection(groupId).document(Docs.DOC_METADATA).get()
//                            .addOnSuccessListener(metadataSnapshot -> {
//                                Map<String, Object> metadata = metadataSnapshot.getData();
//                                GroupManager.setCurrentGroup(new Group(groupId.substring(6), uids, metadata));
//                            });
//                });
//    }


    public void getMetadata(@NonNull OnDataLoadListener listener) {
        fs.collection(UserOptions.getCurrent().getGroupId())
                .document(Docs.DOC_METADATA)
                .get()
                .addOnSuccessListener(snapshot -> listener.onLoadFinished(snapshot, null))
                .addOnFailureListener(e -> {
                    Util.logException("Metadata load failed", e);
                    listener.onLoadFinished(null, e);
                });
    }


    // TODO: 07.05.2020 nested collections are not deleted
    public void deleteAllGroupData() {
        String groupId = UserOptions.getCurrent().getGroupId();
        if (groupId.equals("null")) return;

        fs.document(Docs.REF_GROUP_CODES).get()
                .addOnSuccessListener(snapshot -> {
                    String code = groupId.substring(6);
                    ArrayList<String> codes = getGroupCodes(snapshot);

                    if (codes.contains(code)) {
                        codes.remove(code);
                        fs.document(Docs.REF_GROUP_CODES).update(Docs.GROUP_CODES_ARRAY, codes);
                    }
        });

        fs.collection(groupId).get()
                 .addOnSuccessListener(snapshots -> {
                     for (DocumentSnapshot snapshot : snapshots.getDocuments()) {
                         snapshot.getReference().delete();
                     }
                 });

        Factory.getInstance().getUserDao().updateOptions(
                UserOptions.getCurrent().edit().setGroupId("null").commit()
        );
    }


    private static ArrayList<String> getGroupCodes(@NonNull DocumentSnapshot doc) {
        return (ArrayList<String>) doc.get(Docs.GROUP_CODES_ARRAY);
    }


    private static ArrayList<String> getUids(@NonNull DocumentSnapshot doc) {
        return (ArrayList<String>) doc.get(Docs.USERS_ARRAY);
    }

}
