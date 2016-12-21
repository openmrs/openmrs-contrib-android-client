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

package org.openmrs.mobile.activities.patientdashboard.vitals;

import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardMainPresenterImpl;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Patient;

public class PatientDashboardVitalsPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientVitalsPresenter {

    private PatientDashboardContract.ViewPatientVitals mPatientVitalsView;

    public PatientDashboardVitalsPresenter(String id, PatientDashboardContract.ViewPatientVitals mPatientVitalsView) {
        this.mPatient = new PatientDAO().findPatientByID(id);
        this.mPatientVitalsView = mPatientVitalsView;
        this.mPatientVitalsView.setPresenter(this);
    }

    @Override
    public void start() {
        Encounter mVitalsEncounter = new EncounterDAO().getLastVitalsEncounter(mPatient.getUuid());
        if (mVitalsEncounter != null) {
            mPatientVitalsView.showEncounterVitals(mVitalsEncounter);
        }
        else {
            mPatientVitalsView.showNoVitalsNotification();
        }
    }
}
