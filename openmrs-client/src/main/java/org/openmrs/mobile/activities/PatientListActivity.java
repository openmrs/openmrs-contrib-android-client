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

package org.openmrs.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.PatientsVitalsListFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.PatientListBundle;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class PatientListActivity extends ACBaseActivity {

    private String mSelectedPatientUUID;
    private Long mSelectedPatientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_capture_vitals);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (null != savedInstanceState) {
            mSelectedPatientUUID = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
        }

        List<Patient> patientList = new PatientDAO().getAllPatients();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.patientVitalsList, PatientsVitalsListFragment.newInstance(new PatientListBundle(patientList))).commit();
    }


    public void startFormEntry(String patientUUID, Long patientID) {
        mSelectedPatientUUID = patientUUID;
        mSelectedPatientID = patientID;

        if(patientUUID!=null)
        {
            startEncounterForPatient();
        }
        else
            ToastUtil.error("Patient not yet registered, cannot create encounter.");
    }

    public void startEncounterForPatient() {
        try {
            Intent intent = new Intent(this, FormListActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, mSelectedPatientID);
            startActivity(intent);
        } catch (Exception e) {
            dismissProgressDialog(true, null, R.string.failed_to_open_vitals_form);
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        }
    }

    public void startVisit() {
        showProgressDialog(R.string.action_start_visit);
        new VisitsManager().startVisit(
                VisitsHelper.createStartVisitListener(mSelectedPatientUUID, mSelectedPatientID, this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, mSelectedPatientID);

    }

}
