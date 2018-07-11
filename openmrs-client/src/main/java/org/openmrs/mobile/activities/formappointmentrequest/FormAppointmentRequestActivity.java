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
package org.openmrs.mobile.activities.formappointmentrequest;


import android.os.Bundle;
import android.support.v7.app.ActionBar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class FormAppointmentRequestActivity extends ACBaseActivity {
    public FormAppointmentRequestContract.Presenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_appointment_request);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        FormAppointmentRequestFragment formAppointmentRequestFragment =
                (FormAppointmentRequestFragment) getSupportFragmentManager().findFragmentById(R.id.formAppointmentRequestContentFrame);
        if (formAppointmentRequestFragment == null) {
            formAppointmentRequestFragment = FormAppointmentRequestFragment.newInstance();
        }
        if (!formAppointmentRequestFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    formAppointmentRequestFragment, R.id.formAppointmentRequestContentFrame);
        }

        mPresenter = new FormAppointmentRequestPresenter(formAppointmentRequestFragment);
    }

}
