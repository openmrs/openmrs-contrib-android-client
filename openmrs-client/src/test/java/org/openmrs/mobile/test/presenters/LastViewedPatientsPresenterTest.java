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
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsContract;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.test.ACUnitTestBase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LastViewedPatientsPresenterTest extends ACUnitTestBase {

    @Mock
    private PatientDAO patientDAO;
    @Mock
    private RestApi restApi;
    @Mock
    private LastViewedPatientsContract.View view;

    private LastViewedPatientsPresenter lastViewedPatientsPresenter;
    private Patient firstPatient;
    private Patient secondPatient;
    private int limit = 15;
    private int startIndex = 0;

    @Before
    public void setUp(){
        lastViewedPatientsPresenter = new LastViewedPatientsPresenter(
                view,
                restApi,
                patientDAO);
        firstPatient = createPatient(1l);
        secondPatient = createPatient(2l);
        when(patientDAO.isUserAlreadySaved(firstPatient.getUuid())).thenReturn(true);
        when(patientDAO.isUserAlreadySaved(secondPatient.getUuid())).thenReturn(false);
    }

    @Test
    public void shouldUpdateLastViewedPatientList_allOK(){
        List<Patient> patientList = Arrays.asList(firstPatient, secondPatient);
        when(restApi.getLastViewedPatients(limit, startIndex)).thenReturn(mockSuccessCall(patientList));
        lastViewedPatientsPresenter.updateLastViewedList();
        verify(restApi).getLastViewedPatients(limit, startIndex);
        verify(view).updateList(Collections.singletonList(secondPatient));
        verify(view).setListVisibility(true);
        verify(view, atLeast(2)).setEmptyListVisibility(false);
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldUpdateLastViewedPatientList_ServerError(){
        when(restApi.getLastViewedPatients(limit, startIndex)).thenReturn(mockErrorCall(401));
        lastViewedPatientsPresenter.updateLastViewedList();
        verify(restApi).getLastViewedPatients(limit, startIndex);
        verify(view, atLeast(2)).setListVisibility(false);
        verify(view).setEmptyListVisibility(false);
        verify(view).setEmptyListVisibility(true);
        verify(view).setEmptyListText(anyString());
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldUpdateLastViewedPatientList_Error(){
        when(restApi.getLastViewedPatients(limit, startIndex)).thenReturn(mockFailureCall());
        lastViewedPatientsPresenter.updateLastViewedList();
        verify(restApi).getLastViewedPatients(limit, startIndex);
        verify(view).setProgressBarVisibility(false);
        verify(view, atLeast(2)).setListVisibility(false);
        verify(view).setEmptyListText(anyString());
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldFindPatientsWithQuery_allOK(){
        List<Patient> patientList = Arrays.asList(firstPatient, secondPatient);
        when(restApi.getPatients("query", "full")).thenReturn(mockSuccessCall(patientList));
        lastViewedPatientsPresenter.findPatients("query");
        verify(restApi).getPatients("query", "full");
        verify(view).updateList(Collections.singletonList(secondPatient));
        verify(view).setListVisibility(true);
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldFindPatientsWithQuery_Error(){
        when(restApi.getPatients("query", "full")).thenReturn(mockFailureCall());
        lastViewedPatientsPresenter.findPatients("query");
        verify(restApi).getPatients("query", "full");
        verify(view).setProgressBarVisibility(false);
        verify(view).setEmptyListText(anyString());
        verify(view).stopRefreshing();
    }
}
