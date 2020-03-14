package com.osinniy.dz.obj;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DZ {

    private final DateFormat timeDateFormatter = new SimpleDateFormat("HH:mm dd.MM", Locale.getDefault());

    private String time = timeDateFormatter.format(new Date());

    private Map<String, String> homework;
    private String id;
    private String uid;

    private boolean isChanged = false;


    private DZ() {}

    public DZ(Map<String, String> homework, String id, String uid) {
        this.homework = homework;
        this.uid = uid;
        this.id = id;
    }


    public void update(Map<String, String> newHomework) {
        homework.clear();
        homework.putAll(newHomework);
        time = timeDateFormatter.format(new Date());
        isChanged = true;
    }


    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public Map<String, String> getHomework() {
        return homework;
    }

    public String getTime() {
        return time.substring(0, 5);
    }

    public String getDate() {
        return time.substring(6, 11);
    }

    public String getFullTime() {
        return time;
    }

    public boolean isChanged() {
        return isChanged;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DZ dz = (DZ) o;
        return isChanged == dz.isChanged &&
                Objects.equals(timeDateFormatter, dz.timeDateFormatter) &&
                time.equals(dz.time) &&
                Objects.equals(homework, dz.homework) &&
                id.equals(dz.id) &&
                Objects.equals(uid, dz.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeDateFormatter, time, homework, id, uid, isChanged);
    }


    public static class Builder {
        private DZ newDZ;

        public Builder() {
            newDZ = new DZ();
        }

        public Builder withHomework(Map<String, String> homework) {
            newDZ.homework = homework;
            return this;
        }

        public Builder withUser(String uid) {
            newDZ.uid = uid;
            return this;
        }

        public Builder withTime(String time) {
            newDZ.time = time;
            return this;
        }

        public Builder withId(String id) {
            newDZ.id = id;
            return this;
        }

        public Builder withChangedFlag(boolean isChanged) {
            newDZ.isChanged = isChanged;
            return this;
        }

        public DZ build() {
            return newDZ;
        }
    }

}
