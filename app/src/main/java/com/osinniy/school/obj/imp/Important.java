package com.osinniy.school.obj.imp;

import androidx.annotation.Nullable;

import com.osinniy.school.obj.Bindable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Important implements Bindable {

    private String name;
    private String text;

    private final Date creationDate;
    private Date editDate;

    @Nullable
    private final String id;
    private final String uid;
    private boolean isChanged;


    public Important(String name, String text, @Nullable String id, String uid) {
        this.name = name;
        this.text = text;
        this.id = id;
        this.uid = uid;

        creationDate = new Date();
        isChanged = false;
    }


    Important(String name, String text, Date creationDate, Date editDate,
              @Nullable String id, String uid, boolean isChanged) {
        this.name = name;
        this.text = text;
        this.creationDate = creationDate;
        this.editDate = editDate;
        this.id = id;
        this.uid = uid;
        this.isChanged = isChanged;
    }


    public Map<String, Object> update(String newText) {
        text = newText;
        editDate = new Date();
        isChanged = true;
        return ImpMapper.createMap(this);
    }


    @Override
    @Nullable
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getEditDate() {
        return editDate;
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
                name.equals(important.name) &&
                text.equals(important.text) &&
                creationDate.equals(important.creationDate) &&
                Objects.equals(editDate, important.editDate) &&
                Objects.equals(id, important.id) &&
                uid.equals(important.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, text, creationDate, editDate, id, uid, isChanged);
    }

}
