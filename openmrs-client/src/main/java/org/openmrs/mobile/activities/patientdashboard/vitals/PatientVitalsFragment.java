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

import com.openmrs.android_sdk.library.models.Encounter;
import com.openmrs.android_sdk.library.models.Form;
import com.openmrs.android_sdk.library.models.Observation;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.DateUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.formdisplay.FormDisplayActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.application.OpenMRSInflater;
import org.openmrs.mobile.bundle.FormFieldsWrapper;
import org.openmrs.mobile.databinding.FragmentPatientVitalsBinding;

public class PatientVitalsFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientVitals {
    private LinearLayout content;
    private LinearLayout formHeader;
    private TextView emptyList;
    private TextView lastVitalsDate;
    private PatientDashboardActivity patientsVitals;
    private FragmentPatientVitalsBinding binding= null;
    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPatientVitalsBinding.inflate(inflater, null, false);

        content = binding.vitalsDetailsContent;
        emptyList = binding.lastVitalsNoneLabel;
        lastVitalsDate = binding.lastVitalsDate;
        formHeader = binding.lastVitalsLayout;
        ImageButton formEditIcon = binding.formEditIcon;

        formEditIcon.setOnClickListener(view -> ((PatientDashboardVitalsPresenter) mPresenter).startFormDisplayActivityWithEncounter());
        this.inflater = inflater;
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // This method is intentionally empty
    }

    @Override
    public void showNoVitalsNotification() {
        formHeader.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        emptyList.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEncounterVitals(Encounter encounter) {
        lastVitalsDate.setText(DateUtils.convertTime(encounter.getEncounterDatetime(), DateUtils.DATE_WITH_TIME_FORMAT));
        OpenMRSInflater openMRSInflater = new OpenMRSInflater(inflater);
        content.removeAllViews();
        for (Observation obs : encounter.getObservations()) {
            openMRSInflater.addKeyValueStringView(content, obs.getDisplay(), obs.getDisplayValue());
        }
    }

    @Override
    public void startFormDisplayActivity(Encounter encounter) {
        Form form = encounter.getForm();
        if (form != null) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
