package com.osinniy.dz.database;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDao {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private boolean isAdmin;

    public boolean isAdmin() {
        firestore.collection(FireDocs.COL_ADMINS).get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot doc : snapshots) {
                        if (doc.contains(FireDocs.UID))
                            if (isAdmin = doc.getString(FireDocs.UID).equals(user.getUid())) return;
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(FireDocs.TAG_FIRESTORE_WRITE, "Failed to write to collection < "
                            + FireDocs.COL_ADMINS + " > :", e);
                });
        return isAdmin;
    }

}
