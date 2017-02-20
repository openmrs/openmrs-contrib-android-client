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

package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.diagnosis.PatientDashboardDiagnosisPresenter;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.test.ACUnitTestBaseRx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PatientDashboardDiagnosisPresenterTest extends ACUnitTestBaseRx {

    @Mock
    private PatientDashboardContract.ViewPatientDiagnosis view;
    @Mock
    private EncounterDAO encounterDAO;

    private PatientDashboardDiagnosisPresenter presenter;
    private Patient patient;

    @Before
    public void setUp(){
        super.setUp();
        patient = createPatient(1L);
        presenter = new PatientDashboardDiagnosisPresenter(patient, view, encounterDAO);
        mockActiveAndroidContext();
    }

    @Test
    public void shouldLoadDiagnosisFromDB(){
        when(encounterDAO.getAllEncountersByType(eq(patient.getId()), any()))
                .thenReturn(Observable.just(createEncounters(false)));
        presenter.loadDiagnosis();
        verify(view).setDiagnosesToDisplay(createDiagnosisList(2));
    }

    @Test
    public void shouldLoadDiagnosisFromDB_shouldNotShowDuplicates(){
        when(encounterDAO.getAllEncountersByType(eq(patient.getId()), any()))
                .thenReturn(Observable.just(createEncounters(true)));
        presenter.loadDiagnosis();
        verify(view).setDiagnosesToDisplay(createDiagnosisList(2));
    }

    private List<Encounter> createEncounters(boolean withDuplicates){
        Encounter encounter = new Encounter();
        List<Observation> observations = createObservations();
        if (withDuplicates){
            observations.addAll(createObservations());
        }
        encounter.setObservations(observations);
        return Collections.singletonList(encounter);
    }

    private List<Observation> createObservations() {
        List<Observation> observations = new ArrayList<>();
        List<String> diagnosisList = createDiagnosisList(2);
        for (String diag : diagnosisList) {
            Observation observation = new Observation();
            observation.setDiagnosisList(diag);
            observations.add(observation);
        }
        return observations;
    }

    private List<String> createDiagnosisList(int diagnosisCount) {
        List<String> diagnosisList = new ArrayList<>();
        for(int i=0; i < diagnosisCount; i++){
            diagnosisList.add("diag"+i);
        }
        return diagnosisList;
    }


}
