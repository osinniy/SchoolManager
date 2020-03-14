package com.osinniy.dz.obj;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.osinniy.dz.util.Tools;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SchoolSchedule {

    private static Map<String, String> monday;
    private static Map<String, String> tuesday;
    private static Map<String, String> wednesday;
    private static Map<String, String> thursday;
    private static Map<String, String> friday;


    public static void refresh(@NonNull DocumentSnapshot doc) {
        if (doc.contains(Tools.MONDAY)) monday = (Map<String, String>) doc.get(Tools.MONDAY);
        if (doc.contains(Tools.TUESDAY)) tuesday = (Map<String, String>) doc.get(Tools.TUESDAY);
        if (doc.contains(Tools.WEDNESDAY)) wednesday = (Map<String, String>) doc.get(Tools.WEDNESDAY);
        if (doc.contains(Tools.THURSDAY)) thursday = (Map<String, String>) doc.get(Tools.THURSDAY);
        if (doc.contains(Tools.FRIDAY)) friday = (Map<String, String>) doc.get(Tools.FRIDAY);
    }

    public static Map<String, Object> getAll() {
        Map<String, Object> map = new HashMap<>();

        map.put(Tools.MONDAY, monday);
        map.put(Tools.TUESDAY, tuesday);
        map.put(Tools.WEDNESDAY, wednesday);
        map.put(Tools.THURSDAY, thursday);
        map.put(Tools.FRIDAY, friday);

        return map;
    }

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
