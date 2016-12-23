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

package org.openmrs.mobile.activities.patientdashboard.diagnosis;

import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardMainPresenterImpl;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

public class PatientDashboardDiagnosisPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientDiagnosisPresenter {

    private PatientDashboardContract.ViewPatientDiagnosis mPatientDiagnosisView;


    public PatientDashboardDiagnosisPresenter(String id,
                                            PatientDashboardContract.ViewPatientDiagnosis patientDiagnosisView) {
        this.mPatient = new PatientDAO().findPatientByID(id);
        this.mPatientDiagnosisView = patientDiagnosisView;
        this.mPatientDiagnosisView.setPresenter(this);
    }

    private List<String> getAllDiagnosis(List<Encounter> encounters) {
        List<String> diagnosis = new ArrayList<String>();

        for (Encounter encounter : encounters) {
            for (Observation obs : encounter.getObservations()) {
                if (obs.getDiagnosisList() != null
                        && !obs.getDiagnosisList().equals(ApplicationConstants.EMPTY_STRING)
                        && !diagnosis.contains(obs.getDiagnosisList())) {
                    diagnosis.add(obs.getDiagnosisList());
                }
            }
        }
        return diagnosis;
    }

    @Override
    public void start() {
        loadDiagnosis();
    }

    @Override
    public void loadDiagnosis() {
        List<Encounter> mVisitNoteEncounters = new EncounterDAO().getAllEncountersByType(mPatient.getId(),
                new EncounterType(EncounterType.VISIT_NOTE));
        mPatientDiagnosisView.setDiagnosesToDisplay(getAllDiagnosis(mVisitNoteEncounters));
    }
}
