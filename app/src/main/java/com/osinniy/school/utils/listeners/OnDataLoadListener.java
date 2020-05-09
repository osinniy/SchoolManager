package com.osinniy.school.utils.listeners;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;

@FunctionalInterface
public interface OnDataLoadListener {

    void onLoadFinished(@Nullable DocumentSnapshot data, @Nullable Exception e);

}
