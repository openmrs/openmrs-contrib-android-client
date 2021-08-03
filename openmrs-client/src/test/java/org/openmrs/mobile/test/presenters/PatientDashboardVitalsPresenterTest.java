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

import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.dao.EncounterDAO;
import com.openmrs.android_sdk.library.dao.LocationDAO;
import com.openmrs.android_sdk.library.dao.VisitDAO;
import com.openmrs.android_sdk.library.models.Encounter;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.NetworkUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.vitals.PatientDashboardVitalsPresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.repository.VisitRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.test.ACUnitTestBaseRx;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;

import rx.Observable;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@PrepareForTest(NetworkUtils.class)
public class PatientDashboardVitalsPresenterTest extends ACUnitTestBaseRx {
    @Mock
    private OpenMRS openMRS;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private PatientDashboardContract.ViewPatientVitals viewPatientVitals;
    @Mock
    private EncounterDAO encounterDAO;
    @Mock
    private RestApi restApi;
    @Mock
    private VisitDAO visitDAO;
    @Mock
    private LocationDAO locationDAO;
    private PatientDashboardVitalsPresenter presenter;
    private Patient patient;

    @Before
    public void setUp() {
        super.setUp();
        patient = createPatient(1L);
        VisitRepository visitRepository = new VisitRepository(openMRSLogger, restApi, visitDAO, locationDAO, encounterDAO);
        presenter = new PatientDashboardVitalsPresenter(patient, viewPatientVitals, encounterDAO, visitRepository);
        PowerMockito.mockStatic(NetworkUtils.class);
    }

    @Test
    public void subscribe_allOk() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString()))
            .thenReturn(mockSuccessCall(Collections.singletonList(new Encounter())));
        Encounter encounter = new Encounter();
        Mockito.lenient().when(encounterDAO.getLastVitalsEncounter(patient.getUuid())).thenReturn(Observable.just(encounter));
        presenter.subscribe();
        verify(encounterDAO, times(2)).getLastVitalsEncounter(patient.getUuid());
        verify(viewPatientVitals, times(2)).showEncounterVitals(encounter);
    }

    @Test
    public void subscribe_nullEncounter() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString()))
            .thenReturn(mockSuccessCall(Collections.singletonList(new Encounter())));
        Mockito.lenient().when(encounterDAO.getLastVitalsEncounter(patient.getUuid())).thenReturn(Observable.just(null));
        presenter.subscribe();
        verify(encounterDAO, times(2)).getLastVitalsEncounter(patient.getUuid());
        verify(viewPatientVitals, times(2)).showNoVitalsNotification();
    }

    @Test
    public void subscribe_errorResponse() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString())).thenReturn(mockErrorCall(401));
        Encounter encounter = new Encounter();
        Mockito.lenient().when(encounterDAO.getLastVitalsEncounter(patient.getUuid())).thenReturn(Observable.just(encounter));
        presenter.subscribe();
        verify(viewPatientVitals).showErrorToast(anyString());
        verify(encounterDAO).getLastVitalsEncounter(patient.getUuid());
        verify(viewPatientVitals).showEncounterVitals(encounter);
    }

    @Test
    public void subscribe_errorResponseNullEncounter() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString())).thenReturn(mockErrorCall(401));
        Mockito.lenient().when(encounterDAO.getLastVitalsEncounter(patient.getUuid())).thenReturn(Observable.just(null));
        presenter.subscribe();
        verify(viewPatientVitals).showErrorToast(anyString());
        verify(encounterDAO).getLastVitalsEncounter(patient.getUuid());
        verify(viewPatientVitals).showNoVitalsNotification();
    }

    @Test
    public void subscribe_networkError() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(false);
        Encounter encounter = new Encounter();
        Mockito.lenient().when(encounterDAO.getLastVitalsEncounter(patient.getUuid())).thenReturn(Observable.just(encounter));
        presenter.subscribe();
        verify(encounterDAO).getLastVitalsEncounter(patient.getUuid());
        verify(viewPatientVitals).showEncounterVitals(encounter);
    }

    @Test
    public void subscribe_networkErrorNullEncounter() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(false);
        Mockito.lenient().when(encounterDAO.getLastVitalsEncounter(patient.getUuid())).thenReturn(Observable.just(null));
        presenter.subscribe();
        verify(encounterDAO).getLastVitalsEncounter(patient.getUuid());
        verify(viewPatientVitals).showNoVitalsNotification();
    }

    @Test
    public void shouldStartFormDisplayActivityWithEncounter_allOK() {
        Encounter encounter = new Encounter();
        Mockito.lenient().when(encounterDAO.getLastVitalsEncounter(patient.getUuid())).thenReturn(Observable.just(encounter));
        presenter.startFormDisplayActivityWithEncounter();
        verify(viewPatientVitals).startFormDisplayActivity(encounter);
    }
}
