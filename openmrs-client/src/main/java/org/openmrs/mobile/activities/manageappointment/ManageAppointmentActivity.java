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

package org.openmrs.mobile.activities.manageappointment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class ManageAppointmentActivity extends ACBaseActivity {

    public ManageAppointmentContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_manage_appointment);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        ManageAppointmentFragment manageAppointmentFragment =
                (ManageAppointmentFragment) getSupportFragmentManager().findFragmentById(R.id.manageAppointmentContentFrame);
        if (manageAppointmentFragment == null) {
            manageAppointmentFragment = ManageAppointmentFragment.newInstance();
        }
        if (!manageAppointmentFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    manageAppointmentFragment, R.id.manageAppointmentContentFrame);
        }

        // Create the presenter
        mPresenter = new ManageAppointmentPresenter(manageAppointmentFragment);
    }

   
}
