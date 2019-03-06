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

package org.openmrs.mobile.activities.addeditpatient;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.dashboard.DashboardActivity;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.widget.Toolbar;

public class AddEditPatientActivity extends ACBaseActivity {

    public AddEditPatientContract.Presenter mPresenter;
    public AddEditPatientFragment addEditPatientFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_patient_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        addEditPatientFragment =
                (AddEditPatientFragment) getSupportFragmentManager().findFragmentById(R.id.patientInfoContentFrame);
        if (addEditPatientFragment == null) {
            addEditPatientFragment = AddEditPatientFragment.newInstance();
        }
        if (!addEditPatientFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    addEditPatientFragment, R.id.patientInfoContentFrame);
        }

        //Check if bundle includes patient ID
        Bundle patientBundle = savedInstanceState;
        if (patientBundle != null) {
            patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        } else {
            patientBundle = getIntent().getExtras();
        }
        String patientID = "";
        if (patientBundle != null) {
            patientID = patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        }

        List<String> countries = Arrays.asList(getResources().getStringArray(R.array.countries_array));
        // Create the mPresenter
        mPresenter = new AddEditPatientPresenter(addEditPatientFragment, countries, patientID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.isRegisteringPatient()) {
            boolean createDialog = addEditPatientFragment.areFieldsNotEmpty();
            super.onBackPressed();
            if (createDialog) {
                showInfoLostDialog();
            } else {
                if (!mPresenter.isRegisteringPatient()) {
                    super.onBackPressed();
                }
            }
        }
    }

    /**
     * The method creates a warning dialog when the user presses back button while registering a patient
     */
    private void showInfoLostDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle(R.string.dialog_title_are_you_sure);
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.dialog_message_data_lost)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_stay, (dialog, id) -> dialog.cancel())
                .setNegativeButton(R.string.dialog_button_leave, (dialog, id) -> {
                    Intent intent = new Intent(AddEditPatientActivity.this, DashboardActivity.class);
                    startActivity(intent);
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}