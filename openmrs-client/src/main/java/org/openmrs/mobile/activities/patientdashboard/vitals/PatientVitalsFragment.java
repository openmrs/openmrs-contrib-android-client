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

package org.openmrs.mobile.activities.patientdashboard.vitals;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.application.OpenMRSInflater;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Observation;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

public class PatientVitalsFragment extends Fragment implements PatientDashboardContract.ViewPatientVitals {

    private LinearLayout mContent;
    private TextView mLastVitalsLabel;
    private TextView mEmptyList;
    private TextView mLastVitalsDate;

    private LayoutInflater mInflater;

    private PatientDashboardContract.PatientVitalsPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_vitals, null, false);
        mContent = (LinearLayout) root.findViewById(R.id.vitalsDetailsContent);
        mLastVitalsLabel = (TextView) root.findViewById(R.id.lastVitalsLabel);
        mEmptyList = (TextView) root.findViewById(R.id.lastVitalsNoneLabel);
        mLastVitalsDate = (TextView) root.findViewById(R.id.lastVitalsDate);

        this.mInflater = inflater;

        FontsUtil.setFont(mEmptyList, FontsUtil.OpenFonts.OPEN_SANS_EXTRA_BOLD);
        FontsUtil.setFont(mLastVitalsLabel, FontsUtil.OpenFonts.OPEN_SANS_EXTRA_BOLD);
        FontsUtil.setFont(mLastVitalsDate, FontsUtil.OpenFonts.OPEN_SANS_SEMIBOLD);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionDelete:
                mPresenter.deletePatient();
                this.getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(PatientDashboardContract.PatientVitalsPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showNoVitalsNotification() {
        mLastVitalsLabel.setVisibility(View.GONE);
        mContent.setVisibility(View.GONE);
        mEmptyList.setVisibility(View.VISIBLE);
        mEmptyList.setText(getString(R.string.last_vitals_none_label));
    }

    @Override
    public void showEncounterVitals(Encounter encounter) {
        mLastVitalsDate.setText(DateUtils.convertTime(encounter.getEncounterDatetime(), DateUtils.DATE_WITH_TIME_FORMAT));
        OpenMRSInflater openMRSInflater = new OpenMRSInflater(mInflater);
        mContent.removeAllViews();
        for (Observation obs : encounter.getObservations()) {
            openMRSInflater.addKeyValueStringView(mContent, obs.getDisplay(), obs.getDisplayValue());
        }
    }

    public static PatientVitalsFragment newInstance() {
        return new PatientVitalsFragment();
    }
}
