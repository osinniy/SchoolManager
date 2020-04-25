package com.osinniy.school.obj.groups;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.osinniy.school.firebase.Docs;

import java.util.HashMap;
import java.util.Map;

public class GroupMetadata {

    public static Map<String, Object> create(String name, @Nullable String desc) {
        Map<String, Object> map = new HashMap<>();

        map.put(Docs.NAME, name);
        map.put(Docs.DESCRIPTION, desc);
        map.put(Docs.CREATION_DATE, Timestamp.now());
        map.put(Docs.NUM_OF_MEMBERS, 1);

        return map;
    }

}
