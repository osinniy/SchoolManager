package com.osinniy.school.obj.dz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.osinniy.school.obj.Bindable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class DZ implements Bindable {

    private Map<Integer, String> homework;

    private final Date creationDate;
    private Date editDate;

    @Nullable
    private final String id;
    private final String uid;
    private boolean isChanged;


    public DZ(Map<Integer, String> homework, @Nullable String id, String uid) {
        this.homework = homework;
        this.uid = uid;
        this.id = id;

        creationDate = new Date();
        isChanged = false;
    }


    DZ(Map<Integer, String> homework, Date creationDate, Date editDate,
       @Nullable String id, String uid, boolean isChanged) {
        this.homework = homework;
        this.creationDate = creationDate;
        this.editDate = editDate;
        this.id = id;
        this.uid = uid;
        this.isChanged = isChanged;
    }


    public Map<String, Object> update(@NonNull Map<Integer, String> newHomework) {
        homework.clear();
        homework.putAll(newHomework);
        editDate = new Date();
        isChanged = true;
        return DZMapper.createMap(this);
    }


    @Override
    @Nullable
    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public Map<Integer, String> getHomework() {
        return homework;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getEditDate() {
        return editDate;
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
                creationDate.equals(dz.creationDate) &&
                Objects.equals(editDate, dz.editDate) &&
                Objects.equals(homework, dz.homework) &&
                Objects.equals(id, dz.id) &&
                uid.equals(dz.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creationDate, editDate, homework, id, uid, isChanged);
    }

}
