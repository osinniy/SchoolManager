package com.osinniy.school.obj.imp;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.osinniy.school.obj.Bindable;
import com.osinniy.school.utils.Util;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Important implements Bindable {

    private String name;
    private String text;

    private final Date creationDate;
    private Date editDate;

    @NonNull
    private String id;
    private final String uid;
    private boolean isChanged;


    public Important(String name, String text, String uid) {
        this.name = name;
        this.text = text;
        this.uid = uid;

        id = Util.generateID();
        creationDate = new Date();
        isChanged = false;
    }


    Important(String name, String text, Date creationDate, Date editDate,
              @NonNull String id, String uid, boolean isChanged) {
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
    @NonNull
    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
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
        return Bindable.VIEW_TYPE_IMPORTANT;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Important important = (Important) o;
        return isChanged == important.isChanged &&
                Objects.equals(name, important.name) &&
                Objects.equals(text, important.text) &&
                Objects.equals(creationDate, important.creationDate) &&
                Objects.equals(editDate, important.editDate) &&
                id.equals(important.id) &&
                Objects.equals(uid, important.uid);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, text, creationDate, editDate, id, uid, isChanged);
    }

    @Override
    @NonNull
    public String toString() {
        return "Important{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", creationDate=" + creationDate +
                ", editDate=" + editDate +
                ", id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", isChanged=" + isChanged +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(text);
        dest.writeLong(creationDate.getTime());
        dest.writeLong(editDate == null ? 0 : editDate.getTime());
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeByte(isChanged ? (byte) 1 : (byte) 0);
    }

    public static final Creator<Important> CREATOR = new Creator<Important>() {
        @Override
        public Important createFromParcel(Parcel source) {
            return new Important(source);
        }
        @Override
        public Important[] newArray(int size) {
            return new Important[0];
        }
    };

    private Important(Parcel in) {
        name = in.readString();
        text = in.readString();
        creationDate = new Date(in.readLong());
        long editDateTime = in.readLong();
        editDate = editDateTime == 0 ? null : new Date(editDateTime);
        id = Objects.requireNonNull(in.readString());
        uid = in.readString();
        isChanged = in.readByte() == 1;
    }

}
