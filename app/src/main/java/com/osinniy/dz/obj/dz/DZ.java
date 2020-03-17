package com.osinniy.dz.obj.dz;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class DZ {

    private Date date = new Date();

    private Map<String, String> homework;
    private String id;
    private String uid;

    private boolean isChanged = false;


    public DZ(Map<String, String> homework, String id, String uid) {
        this.homework = homework;
        this.uid = uid;
        this.id = id;
    }


    DZ(Map<String, String> homework, Date date, String id, String uid, boolean isChanged) {
        this.homework = homework;
        this.date = date;
        this.id = id;
        this.uid = uid;
        this.isChanged = isChanged;
    }


    public Map<String, Object> update(Map<String, String> newHomework) {
        homework.clear();
        homework.putAll(newHomework);
        date = new Date();
        isChanged = true;
        return DZMapper.createMap(this);
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

    public Date getDate() {
        return date;
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
                date.equals(dz.date) &&
                Objects.equals(homework, dz.homework) &&
                id.equals(dz.id) &&
                Objects.equals(uid, dz.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, homework, id, uid, isChanged);
    }

}
