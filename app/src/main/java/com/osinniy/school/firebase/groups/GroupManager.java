package com.osinniy.school.firebase.groups;

import androidx.annotation.Nullable;

import com.osinniy.school.firebase.Factory;
import com.osinniy.school.obj.groups.Group;
import com.osinniy.school.obj.options.UserOptions;

import java.util.Map;

public class GroupManager {

    private static GroupDao dao = Factory.getInstance().getGroupDao();

//    @Nullable
//    private static Group currentGroup;


    public static Group newGroup(Map<String, Object> metadata) {
        return dao.addGroup(metadata);
    }


    public static boolean tryToEnter(String code, @Nullable Runnable successCode) {
        return dao.enterGroup(code, successCode);
    }


    public static void exitGroup() {
        dao.exitGroup();
    }


    public static boolean isUserInGroup() {
        return !UserOptions.getCurrent().getGroupId().equals("null");
    }


//    @Nullable
//    public static Group getCurrentGroup() {
//        return currentGroup;
//    }
//
//
//    static void setCurrentGroup(@Nullable Group group) {
//        currentGroup = group;
//    }

}
