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
package org.openmrs.mobile.activities.patientdashboard

import android.content.Context
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.openmrs.android_sdk.utilities.ApplicationConstants.PatientDashboardTabs.ALLERGY_TAB_POS
import com.openmrs.android_sdk.utilities.ApplicationConstants.PatientDashboardTabs.CHARTS_TAB_POS
import com.openmrs.android_sdk.utilities.ApplicationConstants.PatientDashboardTabs.DETAILS_TAB_POS
import com.openmrs.android_sdk.utilities.ApplicationConstants.PatientDashboardTabs.DIAGNOSIS_TAB_POS
import com.openmrs.android_sdk.utilities.ApplicationConstants.PatientDashboardTabs.TAB_COUNT
import com.openmrs.android_sdk.utilities.ApplicationConstants.PatientDashboardTabs.VISITS_TAB_POS
import com.openmrs.android_sdk.utilities.ApplicationConstants.PatientDashboardTabs.VITALS_TAB_POS
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.patientdashboard.allergy.PatientAllergyFragment
import org.openmrs.mobile.activities.patientdashboard.charts.PatientChartsFragment
import org.openmrs.mobile.activities.patientdashboard.details.PatientDetailsFragment
import org.openmrs.mobile.activities.patientdashboard.diagnosis.PatientDiagnosisFragment
import org.openmrs.mobile.activities.patientdashboard.visits.PatientVisitsFragment
import org.openmrs.mobile.activities.patientdashboard.vitals.PatientVitalsFragment

class PatientDashboardPagerAdapter(private val fm: FragmentManager,
                                   private val context: Context,
                                   private val mPatientId: String
) : FragmentPagerAdapter(fm) {

    private val registeredFragments = SparseArray<Fragment>()

    override fun getItem(i: Int): Fragment {
        return when (i) {
            DETAILS_TAB_POS -> PatientDetailsFragment.newInstance(mPatientId)
            ALLERGY_TAB_POS -> PatientAllergyFragment.newInstance(mPatientId)
            DIAGNOSIS_TAB_POS -> PatientDiagnosisFragment.newInstance(mPatientId)
            VISITS_TAB_POS -> PatientVisitsFragment.newInstance(mPatientId)
            VITALS_TAB_POS -> PatientVitalsFragment.newInstance(mPatientId)
            CHARTS_TAB_POS -> PatientChartsFragment.newInstance(mPatientId)
            else -> throw IllegalStateException()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            DETAILS_TAB_POS -> context.getString(R.string.patient_scroll_tab_details_label)
            ALLERGY_TAB_POS -> context.getString(R.string.patient_scroll_tab_allergy_label)
            DIAGNOSIS_TAB_POS -> context.getString(R.string.patient_scroll_tab_diagnosis_label)
            VISITS_TAB_POS -> context.getString(R.string.patient_scroll_tab_visits_label)
            VITALS_TAB_POS -> context.getString(R.string.patient_scroll_tab_vitals_label)
            CHARTS_TAB_POS -> context.getString(R.string.patient_scroll_tab_charts_label)
            else -> super.getPageTitle(position)
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    override fun getCount(): Int = TAB_COUNT
}
