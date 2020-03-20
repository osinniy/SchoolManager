package com.osinniy.dz.obj.timetable;

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

import static com.osinniy.dz.ui.dashboard.DashboardFragment.TAG_METHOD_CALL;

@SuppressWarnings("unchecked")
public class Timetable {

    public static final String MONDAY    = "Monday";
    public static final String TUESDAY   = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";
    public static final String THURSDAY  = "Thursday";
    public static final String FRIDAY    = "Friday";

    private static Map<String, String> monday;
    private static Map<String, String> tuesday;
    private static Map<String, String> wednesday;
    private static Map<String, String> thursday;
    private static Map<String, String> friday;


    public static void refresh() {
        Log.d(TAG_METHOD_CALL, "Method < refresh > in Timetable called");
        FirebaseFirestore.getInstance().collection(FireDocs.COL_SCHOOL_SCHEDULE).get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot doc : snapshots) {
                        if (doc.contains(MONDAY))
                            monday = (Map<String, String>) doc.get(MONDAY);
                        if (doc.contains(TUESDAY))
                            tuesday = (Map<String, String>) doc.get(TUESDAY);
                        if (doc.contains(WEDNESDAY))
                            wednesday = (Map<String, String>) doc.get(WEDNESDAY);
                        if (doc.contains(THURSDAY))
                            thursday = (Map<String, String>) doc.get(THURSDAY);
                        if (doc.contains(FRIDAY))
                            friday = (Map<String, String>) doc.get(FRIDAY);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(FireDocs.TAG_FIRESTORE_READ, "Failed to read from collection < "
                            + FireDocs.COL_SCHOOL_SCHEDULE + " > :", e);
                });
    }

    public static Map<String, Object> getAll() {
        Map<String, Object> map = new HashMap<>();

        map.put(MONDAY, monday);
        map.put(TUESDAY, tuesday);
        map.put(WEDNESDAY, wednesday);
        map.put(THURSDAY, thursday);
        map.put(FRIDAY, friday);

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
