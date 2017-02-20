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
import org.openmrs.mobile.activities.matchingpatients.MatchingPatientsContract;
import org.openmrs.mobile.activities.matchingpatients.MatchingPatientsPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.openmrs.mobile.utilities.PatientAndMatchingPatients;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MatchingPatientsPresenterTest extends ACUnitTestBase {

    @Mock
    private MatchingPatientsContract.View view;
    @Mock
    private RestApi restApi;
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private PatientApi patientApi;

    private MatchingPatientsPresenter presenter;
    private Queue<PatientAndMatchingPatients> patientAndMatchingPatientsQueue;

    @Before
    public void setUp(){
        patientAndMatchingPatientsQueue = createMatchingPatientsList();
        presenter = new MatchingPatientsPresenter(view, patientAndMatchingPatientsQueue, restApi,
                patientDAO, patientApi);
    }

    @Test
    public void mergePatientsTest_userNotSelected(){
        presenter.setSelectedPatient(null);
        presenter.mergePatients();
        verify(view).notifyUser(anyInt());
    }

    @Test
    public void mergePatientsTest_allOk(){
        Patient patient = createPatient(4l);
        presenter.setSelectedPatient(patient);
        when(restApi.updatePatient(any(), anyString(), anyString())).thenReturn(mockSuccessCall(patient));
        presenter.mergePatients();
        verify(view).finishActivity();
    }

    @Test
    public void mergePatientsTest_errorResponse(){
        Patient patient = createPatient(4l);
        presenter.setSelectedPatient(patient);
        when(restApi.updatePatient(any(), anyString(), anyString())).thenReturn(mockErrorCall(401));
        presenter.mergePatients();
        verify(view).showErrorToast(anyString());
    }

    @Test
    public void mergePatientsTest_failure(){
        Patient patient = createPatient(4l);
        presenter.setSelectedPatient(patient);
        when(restApi.updatePatient(any(), anyString(), anyString())).thenReturn(mockFailureCall());
        presenter.mergePatients();
        verify(view).showErrorToast(anyString());
    }

    @Test
    public void registerNewPatientTest_noMorePatients_allOK(){
        presenter.registerNewPatient();
        verify(view).finishActivity();
    }

    @Test
    public void registerNewPatientTest_morePatientsLeft_allOK(){
        patientAndMatchingPatientsQueue.addAll(createMatchingPatientsList());
        presenter.registerNewPatient();
        verify(view).showPatientsData(any(), any());
    }

    private Queue<PatientAndMatchingPatients> createMatchingPatientsList() {
        Queue<PatientAndMatchingPatients> queue = new ArrayDeque<>();
        List<Patient> matchingPatientsList = new ArrayList<>();
        matchingPatientsList.add(createPatient(2l));
        matchingPatientsList.add(createPatient(3l));
        Patient patient = createPatient(1l);
        PatientAndMatchingPatients patientAndMatchingPatients = new PatientAndMatchingPatients(patient, matchingPatientsList);
        queue.add(patientAndMatchingPatients);
        return queue;
    }
}
