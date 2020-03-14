package com.osinniy.dz.obj.mapper;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.osinniy.dz.obj.DZ;
import com.osinniy.dz.util.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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
        DZ.Builder newDZ = new DZ.Builder();

        if (map.containsKey(Tools.HOMEWORK))   newDZ.withHomework((Map<String, String>) map.get(Tools.HOMEWORK));
        if (map.containsKey(Tools.TIME))       newDZ.withTime(String.valueOf(map.get(Tools.TIME)));
        if (map.containsKey(Tools.UID))        newDZ.withUser(String.valueOf(map.get(Tools.UID)));
        if (map.containsKey(Tools.ID))         newDZ.withId(String.valueOf(map.get(Tools.ID)));
        if (map.containsKey(Tools.IS_CHANGED)) newDZ.withChangedFlag((boolean) map.get(Tools.IS_CHANGED));

        return newDZ.build();
    }


    public static DZ restoreInstanceFromDocument(@NonNull DocumentSnapshot document) {
        return new DZ.Builder()
                .withId(document.getId())
                .withHomework((Map<String, String>) document.get(Tools.HOMEWORK))
                .withUser(document.getString(Tools.ID))
                .withTime(document.getString(Tools.TIME))
                .withChangedFlag(Objects.requireNonNull(document.getBoolean(Tools.IS_CHANGED)))
                .build();
    }

}
