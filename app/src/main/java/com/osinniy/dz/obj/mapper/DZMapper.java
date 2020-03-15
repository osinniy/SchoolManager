package com.osinniy.dz.obj.mapper;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.osinniy.dz.database.FireDocs;
import com.osinniy.dz.obj.DZ;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class DZMapper {

    public static Map<String, Object> createMap(@NonNull DZ dz) {
        Map<String, Object> map = new HashMap<>();

        map.put(FireDocs.HOMEWORK, dz.getHomework());
        map.put(FireDocs.TIME, dz.getFullTime());
        map.put(FireDocs.UID, dz.getUid());
        map.put(FireDocs.ID, dz.getId());
        map.put(FireDocs.IS_CHANGED, dz.isChanged());

        return map;
    }


    public static DZ restoreInstanceFromMap(@NonNull Map<String, Object> map) {
        return new DZ(
                (Map<String, String>) map.get(FireDocs.HOMEWORK),
                String.valueOf(map.get(FireDocs.TIME)),
                String.valueOf(map.get(FireDocs.ID)),
                String.valueOf(map.get(FireDocs.UID)),
                (boolean) map.get(FireDocs.IS_CHANGED)
        );
    }


    public static DZ restoreInstanceFromDocument(@NonNull DocumentSnapshot doc) {
        return new DZ(
                (Map<String, String>) doc.get(FireDocs.HOMEWORK),
                doc.getString(FireDocs.TIME),
                doc.getString(FireDocs.ID),
                doc.getString(FireDocs.UID),
                (boolean) doc.get(FireDocs.IS_CHANGED)
        );
    }

}
