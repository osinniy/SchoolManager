package com.osinniy.dz.obj.mapper;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.osinniy.dz.obj.DZ;
import com.osinniy.dz.util.Tools;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class DZMapper {

    public static Map<String, Object> createMap(@NonNull DZ dz) {
        Map<String, Object> map = new HashMap<>();

        map.put(Tools.HOMEWORK, dz.getHomework());
        map.put(Tools.TIME, dz.getFullTime());
        map.put(Tools.UID, dz.getUid());
        map.put(Tools.ID, dz.getId());
        map.put(Tools.IS_CHANGED, dz.isChanged());

        return map;
    }


    public static DZ restoreInstanceFromMap(@NonNull Map<String, Object> map) {
        return new DZ(
                (Map<String, String>) map.get(Tools.HOMEWORK),
                String.valueOf(map.get(Tools.TIME)),
                String.valueOf(map.get(Tools.ID)),
                String.valueOf(map.get(Tools.UID)),
                (boolean) map.get(Tools.IS_CHANGED)
        );
    }


    public static DZ restoreInstanceFromDocument(@NonNull DocumentSnapshot doc) {
        return new DZ(
                (Map<String, String>) doc.get(Tools.HOMEWORK),
                doc.getString(Tools.TIME),
                doc.getString(Tools.ID),
                doc.getString(Tools.UID),
                (boolean) doc.get(Tools.IS_CHANGED)
        );
    }

}
