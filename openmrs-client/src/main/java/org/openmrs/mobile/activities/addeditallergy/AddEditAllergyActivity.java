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

package org.openmrs.mobile.activities.addeditallergy;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.ActionBar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import com.openmrs.android_sdk.utilities.ApplicationConstants;

public class AddEditAllergyActivity extends ACBaseActivity {
    private Long patientID;
    private String allergyUuid;

    public static AddEditAllergyFragment newInstance() {
        return new AddEditAllergyFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy_info);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.allergy_heading);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patientID = bundle.getLong(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
            allergyUuid = bundle.getString(ApplicationConstants.BundleKeys.ALLERGY_UUID);
        }

        AddEditAllergyFragment addEditAllergyFragment = (AddEditAllergyFragment) getSupportFragmentManager().findFragmentById(R.id.allergyFrame);
        if (addEditAllergyFragment == null) {
            addEditAllergyFragment = AddEditAllergyFragment.newInstance();
        }
        if (!addEditAllergyFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    addEditAllergyFragment, R.id.allergyFrame);
        }

        new AddEditAllergyPresenter(addEditAllergyFragment, patientID, allergyUuid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

}
