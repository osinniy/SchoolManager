package com.osinniy.dz.obj.dz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.osinniy.dz.database.FireDocs;

import java.util.HashMap;
import java.util.Map;

public class DZMapper {

    public static Map<String, Object> createMap(@NonNull DZ dz) {
        Map<String, Object> map = new HashMap<>();

        map.put(FireDocs.HOMEWORK, dz.getHomework());
        map.put(FireDocs.DATE, new Timestamp(dz.getDate()));
        map.put(FireDocs.UID, dz.getUid());
        map.put(FireDocs.ID, dz.getId());
        map.put(FireDocs.IS_CHANGED, dz.isChanged());

        return map;
    }

    @SuppressWarnings("all")
    public static DZ restoreInstance(@NonNull Map<String, Object> map) {
        return new DZ(
                (Map<Integer, String>) map.get(FireDocs.HOMEWORK),
                ((Timestamp) map.get(FireDocs.DATE)).toDate(),
                String.valueOf(map.get(FireDocs.ID)),
                String.valueOf(map.get(FireDocs.UID)),
                (boolean) map.get(FireDocs.IS_CHANGED)
        );
    }


    @Nullable
    public static DZ restoreInstance(@NonNull DocumentSnapshot doc) {
        if (doc.getData() == null) return null;
        return restoreInstance(doc.getData());
    }

}
