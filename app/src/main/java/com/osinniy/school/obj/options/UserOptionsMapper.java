package com.osinniy.school.obj.options;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.osinniy.school.obj.options.UserOptions.GROUP_ID;
import static com.osinniy.school.obj.options.UserOptions.IS_ADMIN;
import static com.osinniy.school.obj.options.UserOptions.PHOTO_URI;
import static com.osinniy.school.obj.options.UserOptions.USERNAME;

public class UserOptionsMapper {

    public static Map<String, Object> createMap(@NonNull UserOptions options) {
        Map<String, Object> map = new HashMap<>();

        map.put(USERNAME, options.getUsername());
        map.put(PHOTO_URI, options.getPhotoUri());
        map.put(IS_ADMIN, options.isAdmin());
        map.put(GROUP_ID, options.getGroupId());

        return map;
    }


    public static UserOptions restoreInstance(@NonNull Map<String, Object> map) {
        return new UserOptions().edit()
                .setUsername(String.valueOf(map.get(USERNAME)))
                .setPhotoUri(Uri.parse(String.valueOf(map.get(PHOTO_URI))))
                .setAdmin((boolean) map.get(IS_ADMIN))
                .setGroupId(String.valueOf(map.get(GROUP_ID)))
                .commit();
    }


    public static UserOptions restoreInstance(@NonNull DocumentSnapshot doc) {
        UserOptions.Editor editor = new UserOptions().edit();

        if (doc.contains(USERNAME)) editor.setUsername(doc.getString(USERNAME));
        if (doc.contains(PHOTO_URI)) editor.setPhotoUri(Uri.parse(doc.getString(PHOTO_URI)));
        if (doc.contains(IS_ADMIN)) editor.setAdmin(doc.getBoolean(IS_ADMIN));
        if (doc.contains(GROUP_ID)) editor.setGroupId(doc.getString(GROUP_ID));

        return editor.commit();
    }

}
