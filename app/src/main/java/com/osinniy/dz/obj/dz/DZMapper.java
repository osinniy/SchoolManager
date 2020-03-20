package com.osinniy.dz.obj.dz;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.osinniy.dz.database.FireDocs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@SuppressWarnings("unchecked")
public class DZMapper {

    public static Map<String, Object> createMap(@NonNull DZ dz) {
        Map<String, Object> map = new HashMap<>();

        map.put(FireDocs.HOMEWORK, dz.getHomework());
        map.put(FireDocs.TIME, new Timestamp(dz.getDate()));
        map.put(FireDocs.UID, dz.getUid());
        map.put(FireDocs.ID, dz.getId());
        map.put(FireDocs.IS_CHANGED, dz.isChanged());

        return map;
    }


    public static DZ restoreInstance(@NonNull Map<String, Object> map) {
        return new DZ(
                (Map<String, String>) map.get(FireDocs.HOMEWORK),
                (Date) map.get(FireDocs.TIME),
                String.valueOf(map.get(FireDocs.ID)),
                String.valueOf(map.get(FireDocs.UID)),
                (boolean) map.get(FireDocs.IS_CHANGED)
        );
    }


    public static DZ restoreInstance(@NonNull DocumentSnapshot doc) {
        return new DZ(
                (Map<String, String>) doc.get(FireDocs.HOMEWORK),
                ((Timestamp) Objects.requireNonNull(doc.get(FireDocs.TIME))).toDate(),
                doc.getString(FireDocs.ID),
                doc.getString(FireDocs.UID),
                (boolean) doc.get(FireDocs.IS_CHANGED)
        );
    }

}
