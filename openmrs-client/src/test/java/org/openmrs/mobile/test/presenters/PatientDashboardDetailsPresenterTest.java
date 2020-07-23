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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.details.PatientDashboardDetailsPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.repository.PatientRepository;
import org.openmrs.mobile.api.repository.VisitRepository;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.test.ACUnitTestBaseRx;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;

import rx.Observable;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@PrepareForTest(NetworkUtils.class)
public class PatientDashboardDetailsPresenterTest extends ACUnitTestBaseRx {

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private PatientDashboardContract.ViewPatientDetails view;
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private VisitDAO visitDAO;
    @Mock
    private LocationDAO locationDAO;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private RestApi restApi;
    @Mock
    private EncounterDAO encounterDAO;

    private PatientDashboardDetailsPresenter presenter;
    private Patient patient;

    @Before
    public void setUp() {
        super.setUp();
        VisitRepository visitRepository = new VisitRepository(restApi, visitDAO, locationDAO, encounterDAO);
        patient = createPatient(1L);
        presenter = new PatientDashboardDetailsPresenter(patient, patientDAO, view, visitRepository, patientRepository);
        PowerMockito.mockStatic(NetworkUtils.class);
    }


    @Test
    public void shouldSynchronizePatient_onlineMode_successCalls() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.findVisitsByPatientUUID(anyString(), anyString()))
                .thenReturn(mockSuccessCall(Collections.singletonList(new Visit())));
        Mockito.lenient().when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(mockSuccessCall(Collections.singletonList(new Encounter())));
        Mockito.lenient().when(visitDAO.saveOrUpdate(any(), anyLong())).thenReturn(Observable.just(1L));

        presenter.synchronizePatient();
        verify(view).showDialog(anyInt());
        verify(view).showToast(anyInt(), eq(false));
        verify(view).dismissDialog();
    }

    @Test
    public void shouldSynchronizePatient_onlineMode_errorCalls() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.findVisitsByPatientUUID(anyString(), anyString()))
                .thenReturn(mockErrorCall(401));
        Mockito.lenient().when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(mockErrorCall(401));

        presenter.synchronizePatient();
        verify(view).showDialog(anyInt());
        verify(view).showToast(anyInt(), eq(true));
        verify(view).dismissDialog();
    }

    @Test
    public void shouldSynchronizePatient_offlineMode() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(false);
        presenter.synchronizePatient();
        verify(view).showToast(anyInt(), eq(true));
        verify(view).resolvePatientDataDisplay(any());
    }

    @Test
    public void shouldShowPatientOnStartup_onlineMode() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(patientDAO.findPatientByID(patient.getId().toString())).thenReturn(patient);
        Mockito.lenient().when(restApi.findVisitsByPatientUUID(anyString(), anyString()))
                .thenReturn(mockSuccessCall(Collections.singletonList(new Visit())));
        Mockito.lenient().when(visitDAO.saveOrUpdate(any(), anyInt())).thenReturn(Observable.just(1L));
        Mockito.lenient().when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(mockSuccessCall(Collections.singletonList(new Encounter())));
        presenter.subscribe();
        verify(view).setMenuTitle(anyString(), anyString());
        verify(view).resolvePatientDataDisplay(any());
        verify(view, never()).attachSnackbarToActivity();
    }

    @Test
    public void shouldShowPatientOnStartup_onlineMode_errorCalls() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.findVisitsByPatientUUID(anyString(), anyString()))
                .thenReturn(mockErrorCall(401));
        Mockito.lenient().when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(mockErrorCall(401));
        Mockito.lenient().when(patientDAO.findPatientByID(anyString())).thenReturn(patient);
        presenter.subscribe();
        verify(view).showToast(anyInt(), eq(true));
        verify(view).dismissDialog();
        verify(view).setMenuTitle(anyString(), anyString());
        verify(view).resolvePatientDataDisplay(any());
        verify(view, never()).attachSnackbarToActivity();
    }

    @Test
    public void shouldShowPatientOnStartup_offlineMode() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(false);
        Mockito.lenient().when(patientDAO.findPatientByID(patient.getId().toString())).thenReturn(patient);
        presenter.subscribe();
        verify(view).setMenuTitle(anyString(), anyString());
        verify(view).resolvePatientDataDisplay(any());
        verify(view).attachSnackbarToActivity();
    }
}
