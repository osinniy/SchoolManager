package com.osinniy.school.obj.imp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.osinniy.school.firebase.Docs;

import java.util.HashMap;
import java.util.Map;

public class ImpMapper {

    public static Map<String, Object> createMap(@NonNull Important imp) {
        Map<String, Object> map = new HashMap<>(7);

        map.put(Docs.NAME, imp.getName());
        map.put(Docs.TEXT, imp.getText());
        map.put(Docs.CREATION_DATE, new Timestamp(imp.getCreationDate()));
        map.put(Docs.EDIT_DATE, imp.getEditDate() == null ? null : new Timestamp(imp.getEditDate()));
        map.put(Docs.UID, imp.getUid());
        map.put(Docs.ID, imp.getId());
        map.put(Docs.IS_CHANGED, imp.isChanged());

        return map;
    }


    public static Important restoreInstance(@NonNull Map<String, Object> map) {
        return new Important(
                String.valueOf(map.get(Docs.NAME)),
                String.valueOf(map.get(Docs.TEXT)),
                ((Timestamp) map.get(Docs.CREATION_DATE)).toDate(),
                ((Timestamp) map.get(Docs.EDIT_DATE)).toDate(),
                String.valueOf(map.get(Docs.ID)),
                String.valueOf(map.get(Docs.UID)),
                (boolean) map.get(Docs.IS_CHANGED)
        );
    }


    @Nullable
    public static Important restoreInstance(@NonNull DocumentSnapshot doc) {
        if (doc.getData() == null) return null;
        return new Important(
                doc.getString(Docs.NAME),
                doc.getString(Docs.TEXT),
                doc.getTimestamp(Docs.CREATION_DATE).toDate(),
                doc.getTimestamp(Docs.EDIT_DATE) == null ? null : doc.getTimestamp(Docs.EDIT_DATE).toDate(),
                doc.getId(),
                doc.getString(Docs.UID),
                doc.getBoolean(Docs.IS_CHANGED)
        );
    }

}
