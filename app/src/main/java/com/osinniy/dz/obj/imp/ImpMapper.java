package com.osinniy.dz.obj.imp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.osinniy.dz.database.FireDocs;

import java.util.HashMap;
import java.util.Map;

public class ImpMapper {

    public static Map<String, Object> createMap(@NonNull Important imp) {
        Map<String, Object> map = new HashMap<>();

        map.put(FireDocs.NAME, imp.getName());
        map.put(FireDocs.TEXT, imp.getText());
        map.put(FireDocs.DATE, new Timestamp(imp.getDate()));
        map.put(FireDocs.UID, imp.getUid());
        map.put(FireDocs.ID, imp.getId());
        map.put(FireDocs.IS_CHANGED, imp.isChanged());

        return map;
    }

    @SuppressWarnings("all")
    public static Important restoreInstance(@NonNull Map<String, Object> map) {
        return new Important(
                String.valueOf(map.get(FireDocs.NAME)),
                String.valueOf(map.get(FireDocs.TEXT)),
                ((Timestamp) map.get(FireDocs.DATE)).toDate(),
                String.valueOf(map.get(FireDocs.ID)),
                String.valueOf(map.get(FireDocs.UID)),
                (boolean) map.get(FireDocs.IS_CHANGED)
        );
    }


    @Nullable
    public static Important restoreInstance(@NonNull DocumentSnapshot doc) {
        if (doc.getData() == null) return null;
        return restoreInstance(doc.getData());
    }

}
