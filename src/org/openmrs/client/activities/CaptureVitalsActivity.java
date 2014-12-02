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

import android.os.Bundle;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.PatientsVitalsListFragment;
import org.openmrs.client.bundle.PatientListBundle;
import org.openmrs.client.dao.PatientDAO;
import org.openmrs.client.models.Patient;

import java.util.List;

public class CaptureVitalsActivity extends ACBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_capture_vitals);

        List<Patient> patientList = new PatientDAO().getAllPatients();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.patientVitalsList, PatientsVitalsListFragment.newInstance(new PatientListBundle(patientList))).commit();
    }

}
