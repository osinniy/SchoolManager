package com.osinniy.school.obj.groups;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.osinniy.school.firebase.Docs;

import java.util.Map;

public class GroupMetadata {

    public static Map<String, Object> create(String name, @Nullable String desc) {
        Map<String, Object> map = new ArrayMap<>(6);

        map.put(Docs.NAME, name);
        map.put(Docs.DESCRIPTION, desc);
        map.put(Docs.CREATION_DATE, Timestamp.now());
        map.put(Docs.NUM_OF_MEMBERS, 1);
        map.put(Docs.ADMIN_ID, FirebaseAuth.getInstance().getUid());
        map.put(Docs.TIMETABLE_ADDED, false);

        return map;
    }

}
