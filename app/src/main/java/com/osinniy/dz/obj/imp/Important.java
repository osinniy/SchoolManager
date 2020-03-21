package com.osinniy.dz.obj.imp;

import com.osinniy.dz.obj.Objectable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Important implements Objectable {

    private String name;
    private String text;

    private Date date = new Date();
    private String id;
    private String uid;
    private boolean isChanged = false;


    public Important(String name, String text, String id, String uid) {
        this.name = name;
        this.text = text;
        this.id = id;
        this.uid = uid;
    }


    Important(String name, String text, Date date, String id, String uid, boolean isChanged) {
        this.name = name;
        this.text = text;
        this.date = date;
        this.id = id;
        this.uid = uid;
        this.isChanged = isChanged;
    }


    public Map<String, Object> update(String newText) {
        text = newText;
        date = new Date();
        isChanged = true;
        return ImpMapper.createMap(this);
    }


    @Override
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getUid() {
        return uid;
    }

    public boolean isChanged() {
        return isChanged;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Important important = (Important) o;
        return isChanged == important.isChanged &&
                Objects.equals(name, important.name) &&
                Objects.equals(text, important.text) &&
                date.equals(important.date) &&
                id.equals(important.id) &&
                uid.equals(important.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, text, date, id, uid, isChanged);
    }

}
