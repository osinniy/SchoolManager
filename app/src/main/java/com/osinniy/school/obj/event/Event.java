package com.osinniy.school.obj.event;

import android.os.Parcel;

import com.osinniy.school.obj.Bindable;

import java.util.Date;

public class Event implements Bindable {

    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getType() {
        return Bindable.VIEW_TYPE_EVENT;
    }

    @Override
    public boolean isChanged() {
        return false;
    }

    @Override
    public Date getCreationDate() {
        return null;
    }

    @Override
    public Date getEditDate() {
        return null;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }
        @Override
        public Event[] newArray(int size) {
            return new Event[0];
        }
    };

    private Event(Parcel in) {
    }

}
