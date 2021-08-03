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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import com.openmrs.android_sdk.utilities.ApplicationConstants;

import java.util.Arrays;
import java.util.List;

public class AddEditPatientActivity extends ACBaseActivity {
    public AddEditPatientContract.Presenter mPresenter;
    public AddEditPatientFragment addEditPatientFragment;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_patient_info);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle(R.string.action_register_patient);
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

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Package Manager", e.getMessage());
        }
        Bundle bundle = applicationInfo.metaData;
        String googleMapToken = bundle.getString("com.google.android.geo.API_KEY");
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), googleMapToken);
        }

        PlacesClient placesClient = Places.createClient(this);

        // Create the mPresenter
        mPresenter = new AddEditPatientPresenter(addEditPatientFragment, countries, patientID, placesClient, getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.isRegisteringPatient()) {
            boolean createDialog = addEditPatientFragment.areFieldsNotEmpty();
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
                this,R.style.AlertDialogTheme);
        alertDialogBuilder.setTitle(R.string.dialog_title_reset_patient);
        // set dialog message
        alertDialogBuilder
            .setMessage(R.string.dialog_message_data_lost)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_button_stay, (dialog, id) -> dialog.cancel())
            .setNegativeButton(R.string.dialog_button_leave, (dialog, id) -> {
                // Finish the activity
                super.onBackPressed();
                finish();
            });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        if (alertDialog != null) {
            // Dismiss and clear the dialog to prevent Window leaks
            alertDialog.dismiss();
            alertDialog = null;
        }
        super.onPause();
    }
}
