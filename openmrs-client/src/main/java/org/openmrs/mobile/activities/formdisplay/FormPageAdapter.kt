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
package org.openmrs.mobile.activities.formdisplay

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.openmrs.android_sdk.library.models.Page
import org.openmrs.mobile.bundle.FormFieldsWrapper

class FormPageAdapter(fm: FragmentManager,
                      private val pageList: List<Page>,
                      private val formFieldsWrapperList: List<FormFieldsWrapper>?) : FragmentPagerAdapter(fm) {

    val registeredFragments = SparseArray<Fragment>()

    override fun getItem(position: Int): Fragment {
        val formFieldWrapper = if (formFieldsWrapperList != null) formFieldsWrapperList[position] else null
        return FormDisplayPageFragment.newInstance(pageList[position], formFieldWrapper)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun getCount(): Int {
        return pageList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return pageList[position].label
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }
}
