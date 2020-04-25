package com.osinniy.school.firebase.groups;

import android.content.Context;

import androidx.annotation.Nullable;

import com.osinniy.school.firebase.Factory;
import com.osinniy.school.obj.groups.Group;
import com.osinniy.school.obj.options.UserOptions;

import java.util.Map;

public class GroupManager {

    private static GroupDao dao = Factory.getInstance().getGroupDao();

    @Nullable
    private static Group currentGroup;


    public static Group newGroup(Map<String, Object> metadata) {
        return currentGroup = dao.addGroup(metadata);
    }


    public static boolean tryToEnter(String code) {
        return dao.enterGroup(code);
    }


    public static void exitGroup(Context c) {
        dao.exitGroup();

        UserOptions.getCurrent().writeToShared(c);
    }


    public static boolean isUserInGroup() {
        return UserOptions.getCurrent().getGroupId() != null;
    }


    @Nullable
    public static Group getCurrentGroup() {
        return currentGroup;
    }


    static void setCurrentGroup(@Nullable Group group) {
        currentGroup = group;
    }

}
