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

package org.openmrs.mobile.activities.registerpatient;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class RegisterPatientActivity extends ACBaseActivity {

    public RegisterPatientContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_register_patient);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        RegisterPatientFragment registerPatientFragment =
                (RegisterPatientFragment) getSupportFragmentManager().findFragmentById(R.id.registerPatientContentFrame);
        if (registerPatientFragment == null) {
            registerPatientFragment = RegisterPatientFragment.newInstance();
        }
        if (!registerPatientFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    registerPatientFragment, R.id.registerPatientContentFrame);
        }

        // Create the mPresenter
        mPresenter = new SyncPatientPresenter(registerPatientFragment);
    }

}