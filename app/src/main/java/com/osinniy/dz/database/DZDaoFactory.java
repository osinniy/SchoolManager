package com.osinniy.dz.database;

public class DZDaoFactory {

    private static DZDaoFactory instance;
    private DZDao dao;

    public static DZDaoFactory getInstance() {
        if (instance == null) instance = new DZDaoFactory();
        return instance;
    }

    public DZDao getDao() {
        if (dao == null) dao = new FirebaseDZDao();
        return dao;
    }

}
