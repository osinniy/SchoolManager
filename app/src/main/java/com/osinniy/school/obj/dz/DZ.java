package com.osinniy.school.obj.dz;

import android.os.Parcel;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.osinniy.school.obj.Bindable;
import com.osinniy.school.utils.Util;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class DZ implements Bindable {

    private SparseArray<String> homework;

    private Date targetDate;
    private final Date creationDate;
    private Date editDate;

    @NonNull
    private String id;
    private final String uid;
    private boolean isChanged;


    public DZ(SparseArray<String> homework, Date targetDate) {
        if (targetDate == null) throw new IllegalArgumentException(
                "Illegal target date", new NullPointerException("target date shouldn't be null")
        );

        this.homework = homework;
        this.targetDate = targetDate;

        uid = FirebaseAuth.getInstance().getUid();
        id = Util.generateID();
        creationDate = new Date();
        isChanged = false;
    }


    DZ(SparseArray<String> homework, Date targetDate, Date creationDate, Date editDate,
       @NonNull String id, String uid, boolean isChanged) {
        this.homework = homework;
        this.targetDate = targetDate;
        this.creationDate = creationDate;
        this.editDate = editDate;
        this.id = id;
        this.uid = uid;
        this.isChanged = isChanged;
    }


    public Map<String, Object> update(@NonNull SparseArray<String> newHomework, Date newTargetDate) {
        homework = newHomework;
        targetDate = newTargetDate;
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

    public Date getTargetDate() {
        return targetDate;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public Date getEditDate() {
        return editDate;
    }

    @Override
    public boolean isChanged() {
        return isChanged;
    }

    @Override
    public int getType() {
        return Bindable.VIEW_TYPE_DZ;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DZ dz = (DZ) o;
        return isChanged == dz.isChanged &&
                Objects.equals(homework, dz.homework) &&
                Objects.equals(targetDate, dz.targetDate) &&
                Objects.equals(creationDate, dz.creationDate) &&
                Objects.equals(editDate, dz.editDate) &&
                id.equals(dz.id) &&
                Objects.equals(uid, dz.uid);
    }


    @Override
    public int hashCode() {
        return Objects.hash(homework, targetDate, creationDate, editDate, id, uid, isChanged);
    }

    @Override
    @NonNull
    public String toString() {
        return "DZ{" +
                "homework=" + homework +
                ", targetDate=" + targetDate +
                ", creationDate=" + creationDate +
                ", editDate=" + editDate +
                ", id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", isChanged=" + isChanged +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSparseArray(homework);
        dest.writeLong(targetDate.getTime());
        dest.writeLong(creationDate.getTime());
        dest.writeLong(editDate == null ? 0 : editDate.getTime());
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeByte(isChanged ? (byte) 1 : (byte) 0);
    }

    public static final Creator<DZ> CREATOR = new Creator<DZ>() {
        @Override
        public DZ createFromParcel(Parcel source) {
            return new DZ(source);
        }
        @Override
        public DZ[] newArray(int size) {
            return new DZ[0];
        }
    };

    private DZ(Parcel in) {
        homework = in.readSparseArray(getClass().getClassLoader());
        targetDate = new Date(in.readLong());
        creationDate = new Date(in.readLong());
        long editDateTime = in.readLong();
        editDate = editDateTime == 0 ? null : new Date(editDateTime);
        id = Objects.requireNonNull(in.readString());
        uid = in.readString();
        isChanged = in.readByte() == 1;
    }

}
