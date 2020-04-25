package com.osinniy.school.firebase.user;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.obj.options.UserOptionsMapper;
import com.osinniy.school.utils.Status;

public class UserDao {

    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @NonNull
    private UserOptions currentOptions = initDefOptions();


    public void addUser() {
        fs.collection(Docs.COL_USERS).document(user.getUid()).set(
                UserOptionsMapper.createMap(initDefOptions())
        );
    }


    @NonNull
    public UserOptions getOptions() {
        return currentOptions;
    }


    public void setOptions(@NonNull UserOptions userOptions) {
        this.currentOptions = userOptions;
    }


    public void reloadOptions() {
        downloadOptions(null);
    }


    public void reloadOptions(Runnable extraCode) {
        downloadOptions(extraCode);
    }


    @Nullable
    public UserOptions updateOptions(@Nullable UserOptions newOptions) {
        DocumentReference userDoc = fs.collection(Docs.COL_USERS).document(user.getUid());
        if (newOptions == null) userDoc.delete();
        else userDoc.set(UserOptionsMapper.createMap(newOptions));

        return newOptions;
    }


    private void downloadOptions(@Nullable Runnable extraCode) {
        fs.collection(Docs.COL_USERS)
                .document(user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    currentOptions = UserOptionsMapper.restoreInstance(snapshot);
                    Status.isOptionsDownloaded = true;
                    if (extraCode != null) extraCode.run();
                })
                .addOnFailureListener(e -> {
                    Log.w(Docs.TAG_FIRESTORE_READ, "Failed to read user options: ", e);
                    Status.isOptionsDownloaded = false;
                });
    }


    @NonNull
    private UserOptions initDefOptions() {
        return new UserOptions().edit()
                .setUsername("User")
                .setPhotoUri(null)
                .setGroupId(null)
                .setAdmin(false)
                .commit();
    }


}
