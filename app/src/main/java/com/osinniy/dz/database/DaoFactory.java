package com.osinniy.dz.database;

public class DaoFactory {

    private static DaoFactory instance;
    private Dao dao;
    private UserDao userDao;
    private GroupDao groupDao;

    public static DaoFactory getInstance() {
        if (instance == null) instance = new DaoFactory();
        return instance;
    }

    public Dao getDao() {
        if (dao == null) dao = new FirebaseDao();
        return dao;
    }

    public UserDao getUserDao() {
        if (userDao == null) userDao = new UserDao();
        return userDao;
    }

    public GroupDao getGroupDao() {
        if (groupDao == null) groupDao = new GroupDao();
        return groupDao;
    }

}
