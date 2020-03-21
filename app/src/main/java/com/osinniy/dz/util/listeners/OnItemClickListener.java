package com.osinniy.dz.util.listeners;

import com.osinniy.dz.obj.dz.DZ;
import com.osinniy.dz.obj.imp.Important;

public interface OnItemClickListener {

    void onItemImportantClicked(Important item);

    void onItemDZClicked(DZ item);

}
