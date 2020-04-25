package com.osinniy.school.utils.listeners;

import com.osinniy.school.obj.dz.DZ;
import com.osinniy.school.obj.imp.Important;

public interface OnItemClickListener {

    void onItemImportantClicked(Important item);

    void onItemDZClicked(DZ item);

}
