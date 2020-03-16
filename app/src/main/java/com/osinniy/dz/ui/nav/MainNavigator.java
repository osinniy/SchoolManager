package com.osinniy.dz.ui.nav;

import androidx.fragment.app.FragmentManager;

import com.osinniy.dz.ui.dashboard.DashboardFragment;

public class MainNavigator
        implements DashboardFragment.Navigator {

    private final FragmentManager fManager;

    public MainNavigator(FragmentManager fManager) {
        this.fManager = fManager;
    }

}
