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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.formdisplay.FormDisplayActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.application.OpenMRSInflater;
import org.openmrs.mobile.bundle.FormFieldsWrapper;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.ToastUtil;

public class PatientVitalsFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientVitals {

    private LinearLayout mContent;
    private LinearLayout mFormHeader;
    private TextView mEmptyList;
    private TextView mLastVitalsDate;
    private PatientDashboardActivity patientsVitals;
    private LayoutInflater mInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_vitals, null, false);
        mContent = root.findViewById(R.id.vitalsDetailsContent);
        mEmptyList = root.findViewById(R.id.lastVitalsNoneLabel);
        mLastVitalsDate = root.findViewById(R.id.lastVitalsDate);
        mFormHeader = root.findViewById(R.id.lastVitalsLayout);

        ImageButton formEditIcon = root.findViewById(R.id.form_edit_icon);

        formEditIcon.setOnClickListener(view -> ((PatientDashboardVitalsPresenter) mPresenter).startFormDisplayActivityWithEncounter());

        this.mInflater = inflater;
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // This method is intentionally empty
    }

    @Override
    public void showNoVitalsNotification() {
        mFormHeader.setVisibility(View.GONE);
        mContent.setVisibility(View.GONE);
        mEmptyList.setVisibility(View.VISIBLE);
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

    @Override
    public void startFormDisplayActivity(Encounter encounter) {
        Form form = encounter.getForm();
        if(form != null){
            Intent intent = new Intent(getContext(), FormDisplayActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.FORM_NAME, form.getName());
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, encounter.getPatient().getId());
            intent.putExtra(ApplicationConstants.BundleKeys.VALUEREFERENCE, form.getValueReference());
            intent.putExtra(ApplicationConstants.BundleKeys.ENCOUNTERTYPE, encounter.getEncounterType().getUuid());
            intent.putParcelableArrayListExtra(ApplicationConstants.BundleKeys.FORM_FIELDS_LIST_BUNDLE, FormFieldsWrapper.create(encounter));
            startActivity(intent);
        } else {
            ToastUtil.notify(getString(R.string.form_error));
        }

    }

    @Override
    public void showErrorToast(String errorMessage) {
        ToastUtil.error(errorMessage);
    }

    public static PatientVitalsFragment newInstance() {
        return new PatientVitalsFragment();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                patientsVitals.hideFABs(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
