package com.osinniy.school.obj;

import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public interface Bindable extends Parcelable {

    int VIEW_TYPE_IMPORTANT = 1;
    int VIEW_TYPE_DZ = 0;
    int VIEW_TYPE_EVENT = -1;


    String getId();

    int getType();

    boolean isChanged();

    Date getCreationDate();

    Date getEditDate();


    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    @NonNull
    String toString();

    @Override
    default int describeContents() {
        return hashCode();
    }

}
