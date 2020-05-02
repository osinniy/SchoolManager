package com.osinniy.school.firebase.firestore;

import androidx.annotation.NonNull;

import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;

import java.lang.ref.WeakReference;
import java.util.List;

public interface Dao {

    void load(WeakReference<GetItemListener> listener);

    void addDZ(@NonNull DZ dz);

    void deleteDZ(@NonNull DZ dz);

    void addImportant(@NonNull Important imp);

    void deleteImportant(@NonNull Important imp);

    interface GetItemListener {

        void onDZLoaded(List<DZ> dzList);

        void onImportantLoaded(List<Important> importantList);

        void onItemsLoadFailed(Exception e);

    }

}
