/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.openmrs.mobile.activities.fragments.ModulesFragment;
import org.openmrs.mobile.models.ModuleInfo;

import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private static List<ModuleInfo> mModules;
    public static final int ITEMS_PER_PAGE = 4;
    public static final int ITEMS_IN_ROW = 2;

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
        mModules = ModuleInfo.getActiveModules();
    }

    public static List<ModuleInfo>  getPage(int position) {
        int from = position * ITEMS_PER_PAGE;
        int to = Math.min((position + 1) * ITEMS_PER_PAGE, mModules.size());
        if (!(from < to)) {
            return null;
        }
        return mModules.subList(from, to);
    }

    public static boolean isEmpty() {
        return mModules.isEmpty();
    }

    @Override
    public Fragment getItem(int position) {
        return ModulesFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return Math.max((int) Math.ceil((double) mModules.size() / (double) ITEMS_PER_PAGE), 1);
    }
}
