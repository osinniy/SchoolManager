package com.osinniy.school.obj;

import androidx.annotation.Nullable;

import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.osinniy.school.obj.Bindable.VIEW_TYPE_DZ;
import static com.osinniy.school.obj.Bindable.VIEW_TYPE_IMPORTANT;

public class Bindables {

    public static final Comparator<Bindable> BINDABLE_COMPARATOR = (o1, o2) -> {
        if (o1.isChanged() && o2.isChanged())
            return o2.getEditDate().compareTo(o1.getEditDate());
        if (!o1.isChanged() && !o2.isChanged())
            return o2.getCreationDate().compareTo(o1.getCreationDate());
        else
            return o1.isChanged() ? 1 : -1;
    };


    @Nullable
    public static Important findLastImportant(List<Bindable> sortedItems) {
        List<Important> founded = new ArrayList<>(sortedItems.size());
        for (int i = 0; i < sortedItems.size(); i++) {
            Bindable item = sortedItems.get(sortedItems.size() - i - 1);
            if (item.getType() == VIEW_TYPE_IMPORTANT) founded.add((Important) item);
        }
        if (founded.size() >= 4) return founded.get(founded.size() - 1);
        return null;
    }


    @Nullable
    public static DZ findLastDZ(List<Bindable> sortedItems) {
        for (Bindable item : sortedItems) {
            if (item.getType() == VIEW_TYPE_DZ) return (DZ) item;
        }
        return null;
    }


    @Nullable
    public static DZ findTomorrowDZ(List<DZ> items) {
        Collections.sort(items, BINDABLE_COMPARATOR);
        int date = Calendar.getInstance().get(Calendar.DATE);
        for (DZ item : items) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getTargetDate());
            cal.roll(Calendar.DATE, -1);
            if (cal.get(Calendar.DATE) == date) return item;
        }
        return null;
    }

}
