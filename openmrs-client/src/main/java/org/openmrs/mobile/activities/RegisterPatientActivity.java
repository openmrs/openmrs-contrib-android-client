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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.PatientsVitalsListFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.FormManagerBundle;
import org.openmrs.mobile.bundle.PatientListBundle;
import org.openmrs.mobile.dao.FormsDAO;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.net.FormsManager;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.FormsHelper;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class RegisterPatientActivity extends ACBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_register_patients);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

    }
}
