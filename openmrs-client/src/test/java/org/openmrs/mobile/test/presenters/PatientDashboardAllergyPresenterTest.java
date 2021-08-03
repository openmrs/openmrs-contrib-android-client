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
import androidx.lifecycle.MutableLiveData;

import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.allergy.PatientAllergyFragment;
import org.openmrs.mobile.activities.patientdashboard.allergy.PatientDashboardAllergyPresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.repository.AllergyRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.test.ACUnitTestBaseRx;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.verify;

@PrepareForTest({NetworkUtils.class, ToastUtil.class, OpenMRS.class, OpenMRSLogger.class, AppDatabaseHelper.class, OpenmrsAndroid.class})
public class PatientDashboardAllergyPresenterTest extends ACUnitTestBaseRx {
    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();
    MutableLiveData<List<Allergy>> allergyLiveData = Mockito.mock(MutableLiveData.class);
    List<Allergy> allergyList;
    @Mock
    private RestApi restApi;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private OpenMRS openMRS;
    @Mock
    private AppDatabaseHelper appDatabaseHelper;
    @Mock
    private PatientDashboardContract.ViewPatientAllergy viewPatientAllergy;

    private PatientDashboardAllergyPresenter presenter;
    private Patient patient;
    private PatientAllergyFragment fragment = new PatientAllergyFragment();

    @Before
    public void setUp() {
        super.setUp();
        AllergyRoomDAO allergyRoomDAO = Mockito.mock(AllergyRoomDAO.class, RETURNS_MOCKS);
        patient = createPatient(1L);
        AllergyRepository allergyRepository = new AllergyRepository(patient.getId().toString(), allergyRoomDAO);
        presenter = new PatientDashboardAllergyPresenter(patient, viewPatientAllergy, restApi, allergyRepository);
        mockStaticMethods();
    }

    private void mockStaticMethods() {
        PowerMockito.mockStatic(NetworkUtils.class);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.mockStatic(OpenMRSLogger.class);
        PowerMockito.mockStatic(AppDatabaseHelper.class);
        PowerMockito.mockStatic(OpenmrsAndroid.class);
        Mockito.lenient().when(OpenMRS.getInstance()).thenReturn(openMRS);
        PowerMockito.when(OpenmrsAndroid.getOpenMRSLogger()).thenReturn(openMRSLogger);
        PowerMockito.mockStatic(ToastUtil.class);
    }

    @Test
    public void shouldGetAllergies_AllOK() {
        Allergy allergy = createAllergy(1L, "doctor");
        allergyList = Arrays.asList(allergy);
        allergyLiveData.postValue(allergyList);

        Mockito.lenient().when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getAllergies("patient_one_uuid" + 1L)).thenReturn(mockSuccessCall(allergyList));

        presenter.getAllergy(fragment);
        presenter.updateViews(allergyList);

        verify(restApi).getAllergies("patient_one_uuid" + 1L);
        verify(viewPatientAllergy).showAllergyList(allergyList);
    }

    @Test
    public void shouldGetAllergies_Error() {
        Mockito.lenient().when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getAllergies("patient_one_uuid" + 1L)).thenReturn(mockErrorCall(401));

        presenter.getAllergy(fragment);
        verify(restApi).getAllergies("patient_one_uuid" + 1L);
    }
}
