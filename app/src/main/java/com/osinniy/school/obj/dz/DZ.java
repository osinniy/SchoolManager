package com.osinniy.school.obj.dz;

import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.osinniy.school.obj.Bindable;
import com.osinniy.school.utils.Util;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class DZ implements Bindable {

    private SparseArray<String> homework;

    private final Date creationDate;
    private Date editDate;

    @NonNull
    private String id;
    private final String uid;
    private boolean isChanged;


    public DZ(SparseArray<String> homework, String uid) {
        this.homework = homework;
        this.uid = uid;

        id = Util.generateID();
        creationDate = new Date();
        isChanged = false;
    }


    DZ(SparseArray<String> homework, Date creationDate, Date editDate,
       @NonNull String id, String uid, boolean isChanged) {
        this.homework = homework;
        this.creationDate = creationDate;
        this.editDate = editDate;
        this.id = id;
        this.uid = uid;
        this.isChanged = isChanged;
    }


    public Map<String, Object> update(@NonNull SparseArray<String> newHomework) {
        homework = newHomework;
        editDate = new Date();
        isChanged = true;
        return DZMapper.createMap(this);
    }


    @Override
    @NonNull
    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public SparseArray<String> getHomework() {
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
