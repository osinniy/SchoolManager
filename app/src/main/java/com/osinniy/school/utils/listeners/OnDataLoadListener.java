package com.osinniy.school.utils.listeners;

import com.google.firebase.firestore.DocumentSnapshot;

public interface OnDataLoadListener {

    void onDataLoaded(DocumentSnapshot data, Exception e);

}
