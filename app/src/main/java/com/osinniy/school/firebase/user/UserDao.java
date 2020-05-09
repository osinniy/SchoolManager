package com.osinniy.school.firebase.user;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.osinniy.school.firebase.Docs;
import com.osinniy.school.obj.options.UserOptions;
import com.osinniy.school.obj.options.UserOptionsMapper;
import com.osinniy.school.utils.Status;

public class UserDao {

    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

    @NonNull
    private UserOptions currentOptions = initDefOptions();


    public void addUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fs.collection(Docs.COL_USERS).document(user.getUid()).set(
                UserOptionsMapper.createMap(currentOptions)
        );
    }


    public void deleteUser() {
        fs.collection(Docs.COL_USERS).document(FirebaseAuth.getInstance().getUid()).delete();
    }


    @NonNull
    public UserOptions getOptions() {
        return currentOptions;
    }


    public void setOptions(@NonNull UserOptions userOptions) {
        this.currentOptions = userOptions;
    }


    public void reloadOptions() {
        downloadOptions(null, null);
    }


    public void reloadOptions(Runnable successCode, Runnable failureCode) {
        downloadOptions(successCode, failureCode);
    }


    @Nullable
    public UserOptions updateOptions(@Nullable UserOptions newOptions) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userDoc = fs.collection(Docs.COL_USERS).document(user.getUid());
        if (newOptions == null) userDoc.delete();
        else userDoc.set(UserOptionsMapper.createMap(newOptions));

        return newOptions;
    }


    private void downloadOptions(@Nullable Runnable successCode, @Nullable Runnable failureCode) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Status.isOptionsDownloaded = false;
        fs.collection(Docs.COL_USERS)
                .document(user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    currentOptions = UserOptionsMapper.restoreInstance(snapshot);
                    Status.isOptionsDownloaded = true;
                    if (successCode != null) successCode.run();
                })
                .addOnFailureListener(e -> {
                    Log.w(Docs.TAG_FIRESTORE_READ, "Cannot download user options: ", e);
                    FirebaseCrashlytics.getInstance().log("Cannot download user options");
                    FirebaseCrashlytics.getInstance().recordException(e);
                    if (failureCode != null) failureCode.run();
                });
    }


    @NonNull
    private UserOptions initDefOptions() {
        return new UserOptions().edit()
                .setUsername("User")
                .setPhotoUri(null)
                .setGroupId("null")
                .setAdmin(false)
                .commit();
    }


}
