package com.osinniy.school.firebase.firestore;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;

import java.lang.ref.WeakReference;
import java.util.List;

public interface Dao {

    void load(WeakReference<GetItemListener> listener);

    Task<Void> addDZ(@NonNull DZ dz);

    Task<Void> deleteDZ(@NonNull DZ dz);

    Task<Void> addImportant(@NonNull Important imp);

    Task<Void> deleteImportant(@NonNull Important imp);

    interface GetItemListener {

        void onDZLoaded(List<DZ> dzList);

        void onImportantLoaded(List<Important> importantList);

        void onItemsLoadFailed(Exception e);

    }

}
