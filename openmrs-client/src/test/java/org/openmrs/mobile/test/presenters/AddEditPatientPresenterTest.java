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
import com.openmrs.android_sdk.library.models.PatientPhoto;
import com.openmrs.android_sdk.library.models.PersonAddress;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientContract;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientPresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.repository.LocationRepository;
import com.openmrs.android_sdk.library.api.repository.PatientRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.test.ACUnitTestBaseRx;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@PrepareForTest({OpenMRS.class, NetworkUtils.class, RestServiceBuilder.class, ToastUtil.class,OpenmrsAndroid.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
public class AddEditPatientPresenterTest extends ACUnitTestBaseRx {

    private final String INVALID_NAME_1 = "#James";
    private final String INVALID_NAME_2 = "John@Doe";
    private final String INVALID_NAME_3 = "Em*%ile";
    private final String INVALID_ADDRESS_1 = "Washington street ^%123";
    private final String INVALID_ADDRESS_2 = "Door $164";
    @Mock
    private AddEditPatientContract.View view;
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
    private AddEditPatientPresenter presenter;
    private Patient patient;

    @Before
    public void setUp() {
        super.setUp();
        patient = createPatient(1L);
        PatientRepository patientRepository = new PatientRepository(openMRSLogger, patientDAO, restApi, locationRepository);
        presenter = new AddEditPatientPresenter(view, patientRepository, patient, patient.getId().toString(),
                Collections.singletonList("country_" + patient.getId()), restApi);
        mockStaticMethods();
    }

    @Test
    public void shouldNotPassValidation_noGivenName() {
        patient.getName().setGivenName(null);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_invalidGivenName() {
        patient.getName().setGivenName(INVALID_NAME_1);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_noFamilyName() {
        patient.getName().setFamilyName(null);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_invalidFamilyName() {
        patient.getName().setFamilyName(INVALID_NAME_2);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_invalidMiddleName() {
        patient.getName().setMiddleName(INVALID_NAME_3);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_noBirthDate() {
        patient.setBirthdate(null);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_notFullAddress() {
        patient.setAddresses(Collections.singletonList(new PersonAddress()));
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_invalidAddress1() {
        PersonAddress invalidAddress = new PersonAddress();

        invalidAddress.setAddress1(INVALID_ADDRESS_1);

        patient.setAddresses(Collections.singletonList(invalidAddress));
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_invalidAddress2() {
        PersonAddress invalidAddress = new PersonAddress();

        invalidAddress.setAddress2(INVALID_ADDRESS_2);

        patient.setAddresses(Collections.singletonList(invalidAddress));
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_notValidCountry() {
        patient.getAddress().setCountry("United States Of Poland");
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_noGender() {
        presenter.subscribe();
        patient.setGender(null);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldUpdatePatient_allOk() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.updatePatient(any(), anyString(), anyString()))
                .thenReturn(mockSuccessCall(patient.getPatientDto()));
        Mockito.lenient().when(restApi.uploadPatientPhoto(anyString(), any()))
                .thenReturn(mockSuccessCall(new PatientPhoto()));

        presenter.subscribe();

        presenter.confirmUpdate(patient);
        verify(view).setErrorsVisibility(false, false, false, false, false, false, false, false, false, false);
        verify(view).setProgressBarVisibility(true);
        verify(view).hideSoftKeys();
        verify(view).finishPatientInfoActivity();
    }

    @Test
    public void shouldUpdatePatient_errorResponse() {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.updatePatient(any(), anyString(), anyString()))
                .thenReturn(mockErrorCall(401));
        Mockito.lenient().when(restApi.uploadPatientPhoto(anyString(), any()))
                .thenReturn(mockSuccessCall(new PatientPhoto()));

        presenter.confirmUpdate(patient);
        verify(view).setErrorsVisibility(false, false, false, false, false, false, false, false, false, false);
        verify(view).setProgressBarVisibility(true);
        verify(view).hideSoftKeys();
        verify(view).setProgressBarVisibility(false);
    }

    @Test
    public void shouldFinishPatientInfoActivity() {
        presenter.finishPatientInfoActivity();
        verify(view).finishPatientInfoActivity();
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
