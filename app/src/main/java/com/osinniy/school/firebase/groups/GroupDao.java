package com.osinniy.school.firebase.groups;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.Factory;
import com.osinniy.school.firebase.user.UserDao;
import com.osinniy.school.obj.groups.Group;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.utils.Status;
import com.osinniy.school.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static com.osinniy.school.firebase.Docs.NUM_OF_MEMBERS;

@SuppressWarnings("unchecked")
public class GroupDao {

    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

    private final UserDao userDao = Factory.getInstance().getUserDao();

    private final String uid = FirebaseAuth.getInstance().getUid();

    public ArrayList<String> groupCodes;


    Group addGroup(Map<String, Object> metadata) {
        String code = Util.generateGroupCode();

        CollectionReference thisGroup = fs.collection("Group_".concat(code));

        thisGroup.document(Docs.DOC_GROUP_USERS).set(
                Collections.singletonMap(Docs.USERS_ARRAY, Collections.singletonList(uid))
        );

        fs.document(Docs.REF_GROUP_CODES).get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> codes = getGroupCodes(snapshot);

                    if (codes.add(code)) {
                        fs.document(Docs.REF_GROUP_CODES)
                                .update(Docs.GROUP_CODES_ARRAY, codes);
                    }
                });

        thisGroup.document(Docs.DOC_METADATA).set(metadata);

        thisGroup.document(Docs.DOC_TIMETABLE).set(Collections.emptyMap());

        UserOptions.getCurrent().edit().setGroupId("Group_".concat(code)).commit();

        ArrayList<String> uids = new ArrayList<>(Collections.singletonList(uid));

        return new Group(code, uids, metadata);
    }


    boolean enterGroup(@NonNull String code) {
        if (!checkCode(code)) return false;

        String groupId = "Group_".concat(code);

        fs.collection(groupId)
                .document(Docs.DOC_GROUP_USERS)
                .get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> uids = getUids(snapshot);

                    if (!uids.contains(uid)) {
                        uids.add(uid);
                        fs.collection(groupId).document(Docs.DOC_GROUP_USERS).set(
                                Collections.singletonMap(Docs.USERS_ARRAY, uids)
                        );
                    }

                    fs.collection(groupId).document(Docs.DOC_METADATA).get()
                            .addOnSuccessListener(metadataSnapshot -> {
                                if (snapshot.exists()) {
                                    Map<String, Object> metadata = metadataSnapshot.getData();
                                    if (snapshot.contains(NUM_OF_MEMBERS)) {
                                        long numOfMembers = snapshot.getLong(NUM_OF_MEMBERS);
                                        metadata.put(NUM_OF_MEMBERS, ++numOfMembers);
                                        fs.collection(groupId).document(Docs.DOC_METADATA)
                                                .update(NUM_OF_MEMBERS, numOfMembers);
                                    }
                                    GroupManager.setCurrentGroup(new Group(code, uids, metadata));
                                }
                            });
                });

        UserOptions.getCurrent().edit().setGroupId(groupId).commit();

        return true;
    }


    public boolean checkCode(@NonNull String code) {
        if (Status.isGroupCodesDownloaded) {
            for (String c : groupCodes) {
                if (c.equals(code)) return true;
            }
            return false;
        }
        else return false;
    }


    void exitGroup() {
        String groupId = GroupManager.getCurrentGroup().getId();

        fs.collection(groupId)
                 .document(Docs.DOC_GROUP_USERS)
                 .get()
                 .addOnSuccessListener(snapshot -> {
                     ArrayList<String> uids = getUids(snapshot);

                     if (uids.contains(uid)) {
                         uids.remove(uid);
                         fs.collection(groupId).document(Docs.DOC_GROUP_USERS).set(
                                 Collections.singletonMap(Docs.USERS_ARRAY, uids)
                         );
                     }
                 });

        UserOptions.getCurrent().edit().setGroupId(null).commit();

        if (UserOptions.getCurrent().isAdmin()) deleteAllGroupData();

        GroupManager.setCurrentGroup(null);
    }


    public void reloadGroupData() {
        String groupId = UserOptions.getCurrent().getGroupId();
        fs.collection(groupId)
                .document(Docs.DOC_GROUP_USERS)
                .get()
                .addOnSuccessListener(usersSnapshot -> {
                    ArrayList<String> uids = getUids(usersSnapshot);

                    fs.collection(groupId).document(Docs.DOC_METADATA).get()
                            .addOnSuccessListener(metadataSnapshot -> {
                                Map<String, Object> metadata = metadataSnapshot.getData();
                                GroupManager.setCurrentGroup(new Group(groupId.substring(6), uids, metadata));
                            });
                });
    }


    private void deleteAllGroupData() {
        fs.document(Docs.REF_GROUP_CODES).get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> codes = getGroupCodes(snapshot);

                    if (codes.remove(GroupManager.getCurrentGroup().getCode()))
                        fs.document(Docs.REF_GROUP_CODES).update(Docs.GROUP_CODES_ARRAY, codes);
                });

        fs.collection(UserOptions.getCurrent().getGroupId()).get()
                 .addOnSuccessListener(snapshots -> {
                     for (DocumentSnapshot snapshot : snapshots.getDocuments()) {
                         snapshot.getReference().delete();
                     }
                 });
    }


    private static ArrayList<String> getGroupCodes(@NonNull DocumentSnapshot doc) {
        return (ArrayList<String>) doc.get(Docs.GROUP_CODES_ARRAY);
    }


    private static ArrayList<String> getUids(@NonNull DocumentSnapshot doc) {
        return (ArrayList<String>) doc.get(Docs.USERS_ARRAY);
    }

}
