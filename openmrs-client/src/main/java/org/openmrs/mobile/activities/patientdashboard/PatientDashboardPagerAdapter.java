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

package org.openmrs.mobile.activities.patientdashboard;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.charts.PatientChartsFragment;
import org.openmrs.mobile.activities.patientdashboard.charts.PatientDashboardChartsPresenter;
import org.openmrs.mobile.activities.patientdashboard.details.PatientDashboardDetailsPresenter;
import org.openmrs.mobile.activities.patientdashboard.details.PatientDetailsFragment;
import org.openmrs.mobile.activities.patientdashboard.diagnosis.PatientDashboardDiagnosisPresenter;
import org.openmrs.mobile.activities.patientdashboard.diagnosis.PatientDiagnosisFragment;
import org.openmrs.mobile.activities.patientdashboard.visits.PatientDashboardVisitsPresenter;
import org.openmrs.mobile.activities.patientdashboard.visits.PatientVisitsFragment;
import org.openmrs.mobile.activities.patientdashboard.vitals.PatientDashboardVitalsPresenter;
import org.openmrs.mobile.activities.patientdashboard.vitals.PatientVitalsFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class PatientDashboardPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_COUNT = 5;

    private static final int DETAILS_TAB_POS = 0;
    private static final int DIAGNOSIS_TAB_POS = 1;
    private static final int VISITS_TAB_POS = 2;
    private static final int VITALS_TAB_POS = 3;
    private static final int CHARTS_TAB_POS = 4;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    private String mPatientId;

    private Context context;

    PatientDashboardPagerAdapter(FragmentManager fm, Context context, String id) {
        super(fm);
        this.context = context;
        this.mPatientId = id;
    }

    @NotNull
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case DETAILS_TAB_POS:
                PatientDetailsFragment patientDetailsFragment = PatientDetailsFragment.newInstance();
                new PatientDashboardDetailsPresenter(mPatientId, patientDetailsFragment);
                return patientDetailsFragment;
            case DIAGNOSIS_TAB_POS:
                PatientDiagnosisFragment patientDiagnosisFragment = PatientDiagnosisFragment.newInstance();
                new PatientDashboardDiagnosisPresenter(mPatientId, patientDiagnosisFragment);
                return patientDiagnosisFragment;
            case VISITS_TAB_POS:
                PatientVisitsFragment patientVisitsFragment = PatientVisitsFragment.newInstance();
                new PatientDashboardVisitsPresenter(mPatientId, patientVisitsFragment);
                return patientVisitsFragment;
            case VITALS_TAB_POS:
                PatientVitalsFragment patientVitalsFragment = PatientVitalsFragment.newInstance();
                new PatientDashboardVitalsPresenter(mPatientId, patientVitalsFragment);
                return patientVitalsFragment;
            case CHARTS_TAB_POS:
                PatientChartsFragment patientChartsFragment = PatientChartsFragment.newInstance();
                new PatientDashboardChartsPresenter(mPatientId, patientChartsFragment);
                return patientChartsFragment;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case DETAILS_TAB_POS:
                return context.getString(R.string.patient_scroll_tab_details_label);
            case DIAGNOSIS_TAB_POS:
                return context.getString(R.string.patient_scroll_tab_diagnosis_label);
            case VISITS_TAB_POS:
                return context.getString(R.string.patient_scroll_tab_visits_label);
            case VITALS_TAB_POS:
                return context.getString(R.string.patient_scroll_tab_vitals_label);
            case CHARTS_TAB_POS:
                return context.getString(R.string.patient_scroll_tab_charts_label);
            default:
                return super.getPageTitle(position);
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

}