/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.api;

import android.app.IntentService;
import android.content.Intent;

import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;
import java.util.ListIterator;

public class PatientService extends IntentService {

    public PatientService() {
        super("Register Patients");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(NetworkUtils.isOnline()) {
            List<Patient> patientList = new PatientDAO().getAllPatients();
            final ListIterator<Patient> it = patientList.listIterator();
            while (it.hasNext()) {
                final Patient patient=it.next();
                if(!patient.isSynced()) {
                    new PatientApi().syncPatient(patient);
                }
            }
        } else {
            ToastUtil.error("No internet connection. Patient Registration data is saved locally " +
                    "and will sync when internet connection is restored. ");
        }
    }
}