package com.osinniy.school.obj.options;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.osinniy.school.R;
import com.osinniy.school.firebase.Factory;

public class UserOptions {

    static final String USERNAME = "username";
    static final String PHOTO_URI = "photoUri";
    static final String GROUP_ID = "groupId";
    static final String IS_ADMIN = "admin";

    private String username;
    @Nullable
    private Uri photoUri;
    @Nullable
    private String groupId;
    private boolean isAdmin;


    public String getUsername() {
        return username;
    }

    @Nullable
    public Uri getPhotoUri() {
        return photoUri;
    }

    @Nullable
    public String getGroupId() {
        return groupId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }


    public static UserOptions getCurrent() {
        return Factory.getInstance().getUserDao().getOptions();
    }


    public static UserOptions fromShared(Context c) {
        SharedPreferences pref = c.getSharedPreferences(
                c.getString(R.string.pref_user_options), Context.MODE_PRIVATE
        );

        return new UserOptions().edit()
                .setUsername(pref.getString(USERNAME, "User"))
                .setPhotoUri(Uri.parse(pref.getString(PHOTO_URI, "null")))
                .setGroupId(pref.getString(GROUP_ID, null))
                .setAdmin(pref.getBoolean(IS_ADMIN, false))
                .commit();
    }


    public void writeToShared(Context c) {
        c.getSharedPreferences(c.getString(R.string.pref_user_options), Context.MODE_PRIVATE).edit()
                .putString(USERNAME, username)
                .putString(PHOTO_URI, photoUri != null ? photoUri.toString() : "null")
                .putString(GROUP_ID, groupId)
                .putBoolean(IS_ADMIN, isAdmin)
                .apply();
    }


    public Editor edit() {
        return new Editor(this);
    }


    public class Editor {
        private UserOptions options;

        private Editor(UserOptions options) {
            this.options = options;
        }

        public Editor setUsername(String username) {
            options.username = username;
            return this;
        }

        public Editor setPhotoUri(@Nullable Uri uri) {
            options.photoUri = uri;
            return this;
        }

        public Editor setGroupId(@Nullable String id) {
            options.groupId = id;
            return this;
        }

        public Editor setAdmin(boolean b) {
            options.isAdmin = b;
            return this;
        }

        public UserOptions apply(Context c) {
            Factory.getInstance().getUserDao().updateOptions(options);
            options.writeToShared(c);
            return commit();
        }

        public UserOptions commit() {
            return options;
        }

    }

}
