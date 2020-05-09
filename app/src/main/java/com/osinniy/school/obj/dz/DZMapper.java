package com.osinniy.school.obj.dz;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.osinniy.school.firebase.Docs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DZMapper {

    public static Map<String, Object> createMap(@NonNull DZ dz) {
        Map<String, Object> map = new HashMap<>(6);

        map.put(Docs.HOMEWORK, getArrayList(dz.getHomework()));
        map.put(Docs.TARGET_DATE, new Timestamp(dz.getTargetDate()));
        map.put(Docs.CREATION_DATE, new Timestamp(dz.getCreationDate()));
        map.put(Docs.EDIT_DATE, dz.getEditDate() == null ? null : new Timestamp(dz.getEditDate()));
        map.put(Docs.UID, dz.getUid());
        map.put(Docs.ID, dz.getId());
        map.put(Docs.IS_CHANGED, dz.isChanged());

        return map;
    }


    public static DZ restoreInstance(@NonNull Map<String, Object> map) {
        return new DZ(
                (SparseArray<String>) map.get(Docs.HOMEWORK),
                ((Timestamp) map.get(Docs.TARGET_DATE)).toDate(),
                ((Timestamp) map.get(Docs.CREATION_DATE)).toDate(),
                ((Timestamp) map.get(Docs.EDIT_DATE)).toDate(),
                String.valueOf(map.get(Docs.ID)),
                String.valueOf(map.get(Docs.UID)),
                (boolean) map.get(Docs.IS_CHANGED)
        );
    }


    @Nullable
    public static DZ restoreInstance(@NonNull DocumentSnapshot doc) {
        if (doc.getData() == null) return null;
        return new DZ(
                getSparseArray((ArrayList<String>) doc.get(Docs.HOMEWORK)),
                doc.getTimestamp(Docs.TARGET_DATE).toDate(),
                doc.getTimestamp(Docs.CREATION_DATE).toDate(),
                doc.getTimestamp(Docs.EDIT_DATE) == null ? null : doc.getTimestamp(Docs.EDIT_DATE).toDate(),
                doc.getId(),
                doc.getString(Docs.UID),
                doc.getBoolean(Docs.IS_CHANGED)
        );
    }


    private static ArrayList<String> getArrayList(SparseArray<String> from) {
        ArrayList<String> res = new ArrayList<>(from.size());
        for (int i = 0; i < from.size(); i++) res.add(from.get(i));
        return res;
    }

    private static SparseArray<String> getSparseArray(ArrayList<String> from) {
        SparseArray<String> res = new SparseArray<>(from.size());
        for (int i = 0; i < from.size(); i++) res.put(i, from.get(i));
        return res;
    }

}
