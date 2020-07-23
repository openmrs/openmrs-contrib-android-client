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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.visits.PatientDashboardVisitsPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.repository.VisitRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.test.ACUnitTestBaseRx;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NetworkUtils.class, OpenMRS.class})
public class PatientDashboardVisitsPresenterTest extends ACUnitTestBaseRx {
    @Mock
    private PatientDashboardContract.ViewPatientVisits view;
    @Mock
    private VisitDAO visitDAO;
    @Mock
    private LocationDAO locationDAO;
    @Mock
    private EncounterDAO encounterDAO;
    @Mock
    private RestApi restApi;
    @Mock
    private OpenMRS openMRS;
    private PatientDashboardVisitsPresenter presenter;
    private Patient patient;

    @Before
    public void setUp() {
        super.setUp();
        patient = createPatient(1L);
        VisitRepository visitRepository = new VisitRepository(restApi, visitDAO, locationDAO, encounterDAO);
        presenter = new PatientDashboardVisitsPresenter(patient, view, visitDAO, visitRepository);
        mockStaticMethods();
    }

    @Test
    public void shouldShowPatientsVisitsOnStartUp_allOK() {
        List<Visit> visitsList = createVisitsList();
        Mockito.lenient().when(visitDAO.getVisitsByPatientID(patient.getId())).thenReturn(Observable.just(visitsList));
        presenter.subscribe();
        verify(view, atLeast(1)).toggleRecyclerListVisibility(true);
        verify(view, atLeast(1)).setVisitsToDisplay(visitsList);
    }

    @Test
    public void shouldShowPatientsVisitsOnStartUp_noVisits() {
        Mockito.lenient().when(visitDAO.getVisitsByPatientID(patient.getId())).thenReturn(Observable.just(Arrays.asList()));
        presenter.subscribe();
        verify(view, atLeast(1)).toggleRecyclerListVisibility(false);
    }

    @Test
    public void shouldShowStartVisitDialog_patientOnVisit() {
        Mockito.lenient().when(visitDAO.getActiveVisitByPatientId(patient.getId())).thenReturn(Observable.just(new Visit()));
        presenter.showStartVisitDialog();
        verify(view).showStartVisitDialog(false);
    }

    @Test
    public void shouldShowStartVisitDialog_noNetwork() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(false);
        Mockito.lenient().when(visitDAO.getActiveVisitByPatientId(patient.getId())).thenReturn(Observable.just(null));
        presenter.showStartVisitDialog();
        verify(view).showErrorToast(anyInt());
    }

    @Test
    public void shouldShowStartVisitDialog_allOk() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(visitDAO.getActiveVisitByPatientId(patient.getId())).thenReturn(Observable.just(null));
        presenter.showStartVisitDialog();
        verify(view).showStartVisitDialog(true);
    }

    @Test
    public void shouldSyncVisits_allOK() {
        Mockito.lenient().when(visitDAO.saveOrUpdate(any(), anyLong())).thenReturn(Observable.just(1L));
        List<Visit> visitsList = createVisitsList();
        Mockito.lenient().when(restApi.findVisitsByPatientUUID(any(), any())).thenReturn(mockSuccessCall(visitsList));
        Mockito.lenient().when(visitDAO.getActiveVisitByPatientId(patient.getId())).thenReturn(Observable.just(new Visit()));
        Mockito.lenient().when(visitDAO.getVisitsByPatientID(patient.getId())).thenReturn(Observable.just(visitsList));
        presenter.syncVisits();
        verify(view).setVisitsToDisplay(visitsList);
        verify(view).showStartVisitDialog(false);
    }

    @Test
    public void shouldSyncVisits_errorResponse() {
        Mockito.lenient().when(restApi.findVisitsByPatientUUID(any(), any())).thenReturn(mockErrorCall(401));
        presenter.syncVisits();
        verify(view).showErrorToast(anyString());
    }

    @Test
    public void shouldSyncVisits_failure() {
        Mockito.lenient().when(restApi.findVisitsByPatientUUID(any(), any())).thenReturn(mockFailureCall());
        presenter.syncVisits();
        verify(view).showErrorToast(Mockito.any());
    }

    @Test
    public void shouldStartVisit_allOK() {
        createMocksForStartVisit();
        Mockito.lenient().when(visitDAO.saveOrUpdate(any(), anyLong())).thenReturn(Observable.just(1L));
        Mockito.lenient().when(restApi.startVisit(any())).thenReturn(mockSuccessCall(new Visit()));
        presenter.startVisit();
        verify(view).goToVisitDashboard(1L);
        verify(view).dismissCurrentDialog();
    }

    @Test
    public void shouldStartVisit_errorResponse() {
        createMocksForStartVisit();
        Mockito.lenient().when(visitDAO.saveOrUpdate(any(), anyLong())).thenReturn(Observable.just(1L));
        Mockito.lenient().when(restApi.startVisit(any())).thenReturn(mockErrorCall(401));
        presenter.startVisit();
        verify(view).showErrorToast(anyString());
        verify(view).dismissCurrentDialog();
    }

    @Test
    public void shouldStartVisit_failure() {
        createMocksForStartVisit();
        Mockito.lenient().when(visitDAO.saveOrUpdate(any(), anyLong())).thenReturn(Observable.just(1L));
        Mockito.lenient().when(restApi.startVisit(any())).thenReturn(mockFailureCall());
        presenter.startVisit();
        verify(view).showErrorToast(Mockito.any());
        verify(view).dismissCurrentDialog();
    }

    private void createMocksForStartVisit() {
        PowerMockito.when(OpenMRS.getInstance()).thenReturn(openMRS);
        PowerMockito.when(locationDAO.findLocationByName(anyString())).thenReturn(new LocationEntity("location"));

        Mockito.lenient().when(openMRS.getLocation()).thenReturn("location");
        Mockito.lenient().when(openMRS.getVisitTypeUUID()).thenReturn("visitTypeUuid");
    }

    private List<Visit> createVisitsList() {
        List<Visit> visits = new ArrayList<>();
        visits.add(new Visit());
        visits.add(new Visit());
        return visits;
    }

    private void mockStaticMethods() {
        PowerMockito.mockStatic(NetworkUtils.class);
        PowerMockito.mockStatic(OpenMRS.class);
    }
}
