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

package org.openmrs.mobile.activities.formdisplay;

import android.util.SparseArray;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.Page;
import org.openmrs.mobile.utilities.FormService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class FormPageAdapter extends FragmentPagerAdapter {

    private List<Page> mPageList;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

    FormPageAdapter(FragmentManager fm, String valueRef) {
        super(fm);
        Form form = FormService.getForm(valueRef);
        mPageList = form.getPages();
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        FormDisplayPageFragment fragment = FormDisplayPageFragment.newInstance();
        new FormDisplayPagePresenter(fragment, mPageList.get(position));
        return fragment;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return mPageList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageList.get(position).getLabel();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public SparseArray<Fragment> getRegisteredFragments() {
        return mRegisteredFragments;
    }
}