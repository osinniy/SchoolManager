package com.osinniy.school.firebase;

import com.osinniy.school.firebase.firestore.FirestoreDao;
import com.osinniy.school.firebase.groups.GroupDao;
import com.osinniy.school.firebase.user.UserDao;

public class Factory {

    private static volatile Factory instance;

    private volatile FirestoreDao dao;
    private volatile UserDao userDao;
    private volatile GroupDao groupDao;

    private Factory() {}


    public static Factory getInstance() {
        if (instance == null) {
            synchronized (Factory.class) {
                if (instance == null) instance = new Factory();
            }
        }
        return instance;
    }


    public FirestoreDao getFirestoreDao() {
        if (dao == null) {
            synchronized (this) {
                if (dao == null) dao = new FirestoreDao();
            }
        }
        return dao;
    }


    public UserDao getUserDao() {
        if (userDao == null) {
            synchronized (this) {
                if (userDao == null) userDao = new UserDao();
            }
        }
        return userDao;
    }


    public GroupDao getGroupDao() {
        if (groupDao == null) {
            synchronized (this) {
                if (groupDao == null) groupDao = new GroupDao();
            }
        }
        return groupDao;
    }

}
