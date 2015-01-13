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

package org.openmrs.client.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.PatientsVitalsListFragment;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.bundle.PatientListBundle;
import org.openmrs.client.dao.FormsDAO;
import org.openmrs.client.dao.PatientDAO;
import org.openmrs.client.models.Patient;
import org.openmrs.client.net.FormsManger;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.ToastUtil;

import java.util.List;

public class CaptureVitalsActivity extends ACBaseActivity {

    public static final int CAPTURE_VITALS_REQUEST_CODE = 1;

    private String mSelectedPatientUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_capture_vitals);

        if (null != savedInstanceState) {
            mSelectedPatientUUID = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
        }

        List<Patient> patientList = new PatientDAO().getAllPatients();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.patientVitalsList, PatientsVitalsListFragment.newInstance(new PatientListBundle(patientList))).commit();
    }

    public void startFormEntryForResult(String patientUUID) {
        mSelectedPatientUUID = patientUUID;
        try {
            Intent intent = new Intent(this, FormEntryActivity.class);
            Uri formURI = new FormsDAO(this.getContentResolver()).getFormURI("8");
            intent.setData(formURI);
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUUID);
            this.startActivityForResult(intent, CAPTURE_VITALS_REQUEST_CODE);
        } catch (Exception e) {
            ToastUtil.showLongToast(this, ToastUtil.ToastType.ERROR, R.string.failed_to_open_vitals_form);
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, mSelectedPatientUUID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                String path = data.getData().toString();
                String instanceID = path.substring(path.lastIndexOf('/') + 1);
                new FormsManger(this).uploadXFormWIthMultiPartRequest(new FormsDAO(getContentResolver()).getSurveysSubmissionDataFromFormInstanceId(instanceID).getFormInstanceFilePath(), mSelectedPatientUUID);
                break;
            case RESULT_CANCELED:
            default:
                break;
        }
    }
}
