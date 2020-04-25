package com.osinniy.school.obj.groups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.osinniy.school.firebase.Docs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Group {

    @Nullable
    private ArrayList<String> users;

    @Nullable
    private Map<String, Object> metadata;

    @NonNull
    private final String code;

    private final String id;


    public Group(
            @NonNull String code,
            @Nullable ArrayList<String> users,
            @Nullable Map<String, Object> metadata
    ) {
        this.code = code;
        this.users = users;
        this.metadata = metadata;

        id = "Group_".concat(code);
    }


    public Group(@NonNull String code) {
        this.code = code;

        id = "Group_".concat(code);
    }


    public void updateUsers(@NonNull Collection<String> allUsers) {
        if (users != null) {
            users.clear();
            users.addAll(allUsers);
        }
        else
            users = new ArrayList<>(allUsers);
    }

    public void updateMetadata(@NonNull Map<String, Object> newData) {
        if (metadata != null) {
            metadata.clear();
            metadata.putAll(newData);
        }
        else
            metadata = new HashMap<>(newData);
    }


    @Nullable
    public ArrayList<String> getUsers() {
        return users;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    @Nullable
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Nullable
    public String getName() {
        return metadata != null ? String.valueOf(metadata.get(Docs.NAME)) : null;
    }

    public String getId() {
        return id;
    }

}
