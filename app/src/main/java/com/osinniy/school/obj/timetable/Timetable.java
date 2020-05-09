package com.osinniy.school.obj.timetable;

import androidx.annotation.CheckResult;
import androidx.collection.ArrayMap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Timetable {

    public static final String MONDAY    = "Monday";
    public static final String TUESDAY   = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";
    public static final String THURSDAY  = "Thursday";
    public static final String FRIDAY    = "Friday";

    private static ArrayList<String> monday;
    private static ArrayList<String> tuesday;
    private static ArrayList<String> wednesday;
    private static ArrayList<String> thursday;
    private static ArrayList<String> friday;


    public static Task<DocumentSnapshot> refresh() {
        return FirebaseFirestore.getInstance()
                .collection(UserOptions.getCurrent().getGroupId())
                .document(Docs.DOC_TIMETABLE)
                .get()
                .addOnSuccessListener(snapshot -> {
                    monday = (ArrayList<String>) snapshot.get(MONDAY);
                    tuesday = (ArrayList<String>) snapshot.get(TUESDAY);
                    wednesday = (ArrayList<String>) snapshot.get(WEDNESDAY);
                    thursday = (ArrayList<String>) snapshot.get(THURSDAY);
                    friday = (ArrayList<String>) snapshot.get(FRIDAY);
                })
                .addOnFailureListener(e -> Util.logException("Timetable refreshing failed", e));
    }


    public static Task<Void> push() {
        if (!UserOptions.getCurrent().isAdmin())
            throw new IllegalAccessError("Users cannot modify timetable");

        Map<String, Object> newData = getAll();

        return FirebaseFirestore.getInstance()
                .collection(UserOptions.getCurrent().getGroupId())
                .document(Docs.DOC_TIMETABLE)
                .update(newData);
    }


    public static void setAll(Map<String, ArrayList<String>> newData) {
        if (newData.containsKey(MONDAY)) monday = newData.get(MONDAY);
        if (newData.containsKey(TUESDAY)) tuesday = newData.get(TUESDAY);
        if (newData.containsKey(WEDNESDAY)) wednesday = newData.get(WEDNESDAY);
        if (newData.containsKey(THURSDAY)) thursday = newData.get(THURSDAY);
        if (newData.containsKey(FRIDAY)) friday = newData.get(FRIDAY);
    }


    public static Map<String, Object> getAll() {
        Map<String, Object> map = new ArrayMap<>(5);

        map.put(MONDAY, monday);
        map.put(TUESDAY, tuesday);
        map.put(WEDNESDAY, wednesday);
        map.put(THURSDAY, thursday);
        map.put(FRIDAY, friday);

        return map;
    }


    @CheckResult
    public static ArrayList<String> getFromDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
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


    public static ArrayList<String> getMonday() {
        return monday;
    }

    public static ArrayList<String> getTuesday() {
        return tuesday;
    }

    public static ArrayList<String> getWednesday() {
        return wednesday;
    }

    public static ArrayList<String> getThursday() {
        return thursday;
    }

    public static ArrayList<String> getFriday() {
        return friday;
    }

}
