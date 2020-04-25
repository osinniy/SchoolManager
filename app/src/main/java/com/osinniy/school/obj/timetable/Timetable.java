package com.osinniy.school.obj.timetable;

import android.util.Log;

import androidx.annotation.CheckResult;

import com.google.firebase.firestore.FirebaseFirestore;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.firebase.groups.GroupManager;
import com.osinniy.school.obj.options.UserOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

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
        FirebaseFirestore.getInstance()
                .collection(GroupManager.getCurrentGroup().getId())
                .document(Docs.DOC_TIMETABLE)
                .get()
                .addOnSuccessListener(snapshot -> {
                    monday = (Map<String, String>) snapshot.get(MONDAY);
                    tuesday = (Map<String, String>) snapshot.get(TUESDAY);
                    wednesday = (Map<String, String>) snapshot.get(WEDNESDAY);
                    thursday = (Map<String, String>) snapshot.get(THURSDAY);
                    friday = (Map<String, String>) snapshot.get(FRIDAY);
                })
                .addOnFailureListener(e -> {
                    Log.e(Docs.TAG_FIRESTORE_READ, "Timetable refreshing was failed: ", e);
                });
    }

    public static void update() {
        if (!UserOptions.getCurrent().isAdmin()) return;

        Map<String, Object> newData = getAll();

        FirebaseFirestore.getInstance()
                .collection(GroupManager.getCurrentGroup().getId())
                .document(Docs.DOC_TIMETABLE)
                .update(newData);
    }

    public static void setAll(Map<String, Map<String, String>> newData) {
        if (newData.containsKey(MONDAY)) monday = newData.get(MONDAY);
        if (newData.containsKey(TUESDAY)) tuesday = newData.get(TUESDAY);
        if (newData.containsKey(WEDNESDAY)) wednesday = newData.get(WEDNESDAY);
        if (newData.containsKey(THURSDAY)) thursday = newData.get(THURSDAY);
        if (newData.containsKey(FRIDAY)) friday = newData.get(FRIDAY);
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
    @CheckResult
    public static Map<String, String> getToday() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY: return monday;
            case Calendar.TUESDAY: return tuesday;
            case Calendar.WEDNESDAY: return wednesday;
            case Calendar.THURSDAY: return thursday;
            case Calendar.FRIDAY: return friday;
            default: return null;
        }
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
