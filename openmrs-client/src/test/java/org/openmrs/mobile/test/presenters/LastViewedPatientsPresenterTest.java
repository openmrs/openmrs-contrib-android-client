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
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsContract;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsPresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.repository.LocationRepository;
import com.openmrs.android_sdk.library.api.repository.PatientRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@PrepareForTest({OpenMRS.class, NetworkUtils.class, RestServiceBuilder.class, ToastUtil.class, OpenmrsAndroid.class})
@RunWith(PowerMockRunner.class)
public class LastViewedPatientsPresenterTest extends ACUnitTestBase {
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private RestApi restApi;
    @Mock
    private LastViewedPatientsContract.View view;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private OpenMRS openMRS;
    private LastViewedPatientsPresenter lastViewedPatientsPresenter;
    private Patient firstPatient;
    private Patient secondPatient;
    private int limit = 15;
    private int startIndex = 0;

    @Before
    public void setUp() {
        mockStaticMethods();
        PatientRepository patientRepository = new PatientRepository(openMRSLogger, patientDAO, restApi, locationRepository);
        lastViewedPatientsPresenter = new LastViewedPatientsPresenter(view, restApi, patientDAO, patientRepository);
        firstPatient = createPatient(1l);
        secondPatient = createPatient(2l);
        Mockito.lenient().when(patientDAO.isUserAlreadySaved(firstPatient.getUuid())).thenReturn(true);
        Mockito.lenient().when(patientDAO.isUserAlreadySaved(secondPatient.getUuid())).thenReturn(false);
    }

    @Test
    public void shouldUpdateLastViewedPatientList_allOK() {
        List<Patient> patientList = Arrays.asList(firstPatient, secondPatient);
        Mockito.lenient().when(restApi.getLastViewedPatients(limit, startIndex)).thenReturn(mockSuccessCall(patientList));
        lastViewedPatientsPresenter.updateLastViewedList();
        verify(restApi).getLastViewedPatients(limit, startIndex);
        verify(view).updateList(Collections.singletonList(secondPatient));
        verify(view).setListVisibility(true);
        verify(view, atLeast(2)).setEmptyListVisibility(false);
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldUpdateLastViewedPatientList_ServerError() {
        Mockito.lenient().when(restApi.getLastViewedPatients(limit, startIndex)).thenReturn(mockErrorCall(401));
        lastViewedPatientsPresenter.updateLastViewedList();
        verify(restApi).getLastViewedPatients(limit, startIndex);
        verify(view, atLeast(2)).setListVisibility(false);
        verify(view).setEmptyListVisibility(false);
        verify(view).setEmptyListVisibility(true);
        verify(view).setEmptyListText(anyString());
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldUpdateLastViewedPatientList_Error() {
        Mockito.lenient().when(restApi.getLastViewedPatients(limit, startIndex)).thenReturn(mockFailureCall());
        lastViewedPatientsPresenter.updateLastViewedList();
        verify(restApi).getLastViewedPatients(limit, startIndex);
        verify(view).setProgressBarVisibility(false);
        verify(view, atLeast(2)).setListVisibility(false);
        verify(view).setEmptyListText(Mockito.any());
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldFindPatientsWithQuery_allOK() {
        List<Patient> patientList = Arrays.asList(firstPatient, secondPatient);
        Mockito.lenient().when(restApi.getPatients("query", "full")).thenReturn(mockSuccessCall(patientList));
        lastViewedPatientsPresenter.findPatients("query");
        verify(restApi).getPatients("query", "full");
        verify(view).updateList(Collections.singletonList(secondPatient));
        verify(view).setListVisibility(true);
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldFindPatientsWithQuery_Error() {
        Mockito.lenient().when(restApi.getPatients("query", "full")).thenReturn(mockFailureCall());
        lastViewedPatientsPresenter.findPatients("query");
        verify(restApi).getPatients("query", "full");
        verify(view).setProgressBarVisibility(false);
        verify(view).setEmptyListText(Mockito.any());
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldLoadMorePatients_allOK() {
        List<Patient> patientList = Arrays.asList(firstPatient, secondPatient);
        Mockito.lenient().when(restApi.getLastViewedPatients(limit, startIndex)).thenReturn(mockSuccessCall(patientList));
        lastViewedPatientsPresenter.loadMorePatients();
        verify(view).showRecycleViewProgressBar(false);
        verify(view).addPatientsToList(lastViewedPatientsPresenter.filterNotDownloadedPatients(patientList));
    }

    private void mockStaticMethods() {
        PowerMockito.mockStatic(NetworkUtils.class);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.mockStatic(OpenmrsAndroid.class);
        PowerMockito.when(OpenMRS.getInstance()).thenReturn(openMRS);
        PowerMockito.when(OpenmrsAndroid.getOpenMRSLogger()).thenReturn(openMRSLogger);
        PowerMockito.mockStatic(ToastUtil.class);
    }
}
