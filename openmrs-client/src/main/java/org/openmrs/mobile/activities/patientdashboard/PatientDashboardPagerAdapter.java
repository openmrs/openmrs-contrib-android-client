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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import org.openmrs.mobile.activities.patientdashboard.details.PatientDashboardDetailsPresenter;
import org.openmrs.mobile.activities.patientdashboard.details.PatientDetailsFragment;
import org.openmrs.mobile.activities.patientdashboard.diagnosis.PatientDashboardDiagnosisPresenter;
import org.openmrs.mobile.activities.patientdashboard.diagnosis.PatientDiagnosisFragment;
import org.openmrs.mobile.activities.patientdashboard.visits.PatientDashboardVisitsPresenter;
import org.openmrs.mobile.activities.patientdashboard.visits.PatientVisitsFragment;
import org.openmrs.mobile.activities.patientdashboard.vitals.PatientDashboardVitalsPresenter;
import org.openmrs.mobile.activities.patientdashboard.vitals.PatientVitalsFragment;

class PatientDashboardPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_COUNT = 4;

    private static final int DETAILS_TAB_POS = 0;
    private static final int DIAGNOSIS_TAB_POS = 1;
    private static final int VISITS_TAB_POS = 2;
    private static final int VITALS_TAB_POS = 3;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    private String mPatientId;

    PatientDashboardPagerAdapter(FragmentManager fm, String id) {
        super(fm);
        this.mPatientId = id;
    }

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
            default:
                return null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

}