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

package org.openmrs.mobile.activities.formadmission;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.ActionBar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class FormAdmissionActivity extends ACBaseActivity {

    Long patientID;
    String encounterType;
    String formName;

    public static FormAdmissionFragment newInstance() {
        return new FormAdmissionFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_admission);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patientID = bundle.getLong(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
            encounterType = (String) bundle.get(ApplicationConstants.BundleKeys.ENCOUNTERTYPE);
            formName = (String) bundle.get(ApplicationConstants.BundleKeys.FORM_NAME);
        }

        FormAdmissionFragment formAdmissionFragment = (FormAdmissionFragment) getSupportFragmentManager().findFragmentById(R.id.admissionFormContentFrame);
        if (formAdmissionFragment == null) {
            formAdmissionFragment = FormAdmissionFragment.newInstance();
        }
        if (!formAdmissionFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    formAdmissionFragment, R.id.admissionFormContentFrame);
        }

        new FormAdmissionPresenter(formAdmissionFragment, patientID, encounterType, formName, getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
