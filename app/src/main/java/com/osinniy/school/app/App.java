package com.osinniy.school.app;

import android.app.Application;

public class App extends Application {

//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
//
//        UserDao dao = Factory.getInstance().getUserDao();
//        dao.setOptions(UserOptions.fromShared(this));
//        UserOptions currentOptions = dao.getOptions();
//        if (!currentOptions.getGroupId().equals("null")) {
//            if (currentOptions.isAdmin())
//                startActivity(new Intent(this, AdminActivity.class));
//            else
//                startActivity(new Intent(this, MainActivity.class));
//        }
//        else startActivity(new Intent(this, GroupsActivity.class));
//    }

}
