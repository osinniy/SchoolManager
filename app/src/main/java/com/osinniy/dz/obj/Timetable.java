package com.osinniy.dz.obj;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.osinniy.dz.database.FireDocs;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class Timetable {

    private static Map<String, String> monday;
    private static Map<String, String> tuesday;
    private static Map<String, String> wednesday;
    private static Map<String, String> thursday;
    private static Map<String, String> friday;


    public static void refresh() {
        FirebaseFirestore.getInstance().collection(FireDocs.COL_SCHOOL_SCHEDULE).get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot doc : snapshots) {
                        if (doc.contains(FireDocs.MONDAY))
                            monday = (Map<String, String>) doc.get(FireDocs.MONDAY);
                        if (doc.contains(FireDocs.TUESDAY))
                            tuesday = (Map<String, String>) doc.get(FireDocs.TUESDAY);
                        if (doc.contains(FireDocs.WEDNESDAY))
                            wednesday = (Map<String, String>) doc.get(FireDocs.WEDNESDAY);
                        if (doc.contains(FireDocs.THURSDAY))
                            thursday = (Map<String, String>) doc.get(FireDocs.THURSDAY);
                        if (doc.contains(FireDocs.FRIDAY))
                            friday = (Map<String, String>) doc.get(FireDocs.FRIDAY);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(FireDocs.TAG_FIRESTORE_READ, "Failed to read from collection < "
                            + FireDocs.COL_SCHOOL_SCHEDULE + " > :", e);
                });
    }

    public static Map<String, Object> getAll() {
        Map<String, Object> map = new HashMap<>();

        map.put(FireDocs.MONDAY, monday);
        map.put(FireDocs.TUESDAY, tuesday);
        map.put(FireDocs.WEDNESDAY, wednesday);
        map.put(FireDocs.THURSDAY, thursday);
        map.put(FireDocs.FRIDAY, friday);

        return map;
    }

    @Nullable
    @CheckForNull
    public static Map<String, String> getToday() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 2: return monday;
            case 3: return tuesday;
            case 4: return wednesday;
            case 5: return thursday;
            case 6: return friday;
        }
        return null;
    }

    public static Map<String, String> getMonday() {
        return monday;
    }

    public static Map<String, String> getTuesday() {
        return tuesday;
    }

    public static Map<String, String> getWednesday() {
        return wednesday;
    }

    public static Map<String, String> getThursday() {
        return thursday;
    }

    public static Map<String, String> getFriday() {
        return friday;
    }

}
