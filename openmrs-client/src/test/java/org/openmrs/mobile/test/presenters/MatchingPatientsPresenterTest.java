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
import com.openmrs.android_sdk.utilities.PatientAndMatchingPatients;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.matchingpatients.MatchingPatientsContract;
import org.openmrs.mobile.activities.matchingpatients.MatchingPatientsPresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.repository.LocationRepository;
import com.openmrs.android_sdk.library.api.repository.PatientRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

@PrepareForTest({OpenMRS.class, NetworkUtils.class, RestServiceBuilder.class, ToastUtil.class, OpenmrsAndroid.class})
@RunWith(PowerMockRunner.class)
public class MatchingPatientsPresenterTest extends ACUnitTestBase {
    @Mock
    private MatchingPatientsContract.View view;
    @Mock
    private RestApi restApi;
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private OpenMRS openMRS;
    private MatchingPatientsPresenter presenter;
    private Queue<PatientAndMatchingPatients> patientAndMatchingPatientsQueue;

    @Before
    public void setUp() {
        mockStaticMethods();
        patientAndMatchingPatientsQueue = createMatchingPatientsList();
        PatientRepository patientRepository = new PatientRepository(openMRSLogger, patientDAO, restApi, locationRepository);
        presenter = new MatchingPatientsPresenter(view, patientAndMatchingPatientsQueue, restApi, patientDAO, patientRepository);
    }

    @Test
    public void mergePatientsTest_userNotSelected() {
        presenter.setSelectedPatient(null);
        presenter.mergePatients();
        verify(view).notifyUser(anyInt());
    }

    @Test
    public void mergePatientsTest_allOk() {
        Patient patient = createPatient(4l);
        presenter.setSelectedPatient(patient);
        Mockito.lenient().when(restApi.updatePatient(any(), Mockito.any(), Mockito.any())).thenReturn(mockSuccessCall(patient.getPatientDto()));
        presenter.mergePatients();
        verify(view).finishActivity();
    }

    @Test
    public void mergePatientsTest_errorResponse() {
        Patient patient = createPatient(4l);
        presenter.setSelectedPatient(patient);
        Mockito.lenient().when(restApi.updatePatient(any(), any(), any())).thenReturn(mockErrorCall(401));
        presenter.mergePatients();
        verify(view).showErrorToast(Mockito.any());
    }

    @Test
    public void mergePatientsTest_failure() {
        Patient patient = createPatient(4l);
        presenter.setSelectedPatient(patient);
        Mockito.lenient().when(restApi.updatePatient(any(), Mockito.any(), Mockito.any())).thenReturn(mockFailureCall());
        presenter.mergePatients();
        verify(view).showErrorToast(Mockito.any());
    }

    @Test
    public void registerNewPatientTest_noMorePatients_allOK() {
        presenter.registerNewPatient();
        verify(view).finishActivity();
    }

    @Test
    public void registerNewPatientTest_morePatientsLeft_allOK() {
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

    private void mockStaticMethods() {
        PowerMockito.mockStatic(NetworkUtils.class);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.mockStatic(OpenmrsAndroid.class);
        PowerMockito.when(OpenMRS.getInstance()).thenReturn(openMRS);
        PowerMockito.when(OpenmrsAndroid.getOpenMRSLogger()).thenReturn(openMRSLogger);
        PowerMockito.mockStatic(ToastUtil.class);
    }
}
