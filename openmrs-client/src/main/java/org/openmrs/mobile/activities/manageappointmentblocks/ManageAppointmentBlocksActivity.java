package org.openmrs.mobile.activities.manageappointmentblocks;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;


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

public class ManageAppointmentBlocksActivity extends ACBaseActivity {
    private ManageAppointmentBlocksContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointment_blocks);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        ManageAppointmentBlocksFragment manageAppointmentBlocksFragment =
                (ManageAppointmentBlocksFragment) getSupportFragmentManager().findFragmentById(R.id.manageAppointmentBlocksContentFrame);
        if (manageAppointmentBlocksFragment == null) {
            manageAppointmentBlocksFragment = ManageAppointmentBlocksFragment.newInstance();
        }
        if (!manageAppointmentBlocksFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    manageAppointmentBlocksFragment, R.id.manageAppointmentBlocksContentFrame);
        }

        mPresenter = new ManageAppointmentBlocksPresenter(manageAppointmentBlocksFragment);
    }
}
