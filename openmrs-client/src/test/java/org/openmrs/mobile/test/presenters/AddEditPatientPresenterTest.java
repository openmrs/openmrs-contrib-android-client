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
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientContract;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.LocationApi;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientPhoto;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.test.ACUnitTestBaseRx;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({OpenMRS.class, NetworkUtils.class, RestServiceBuilder.class, ToastUtil.class})
@PowerMockIgnore("javax.net.ssl.*")
public class AddEditPatientPresenterTest extends ACUnitTestBaseRx {

    @Mock
    private AddEditPatientContract.View view;
    @Mock
    private RestApi restApi;
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private LocationApi locationApi;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private OpenMRS openMRS;

    private AddEditPatientPresenter presenter;
    private Patient patient;

    private final String INVALID_NAME_1 = "#James";
    private final String INVALID_NAME_2 = "John@Doe";

    private final String INVALID_ADDRESS_1 = "Washington street ^%123";
    private final String INVALID_ADDRESS_2 = "Door $164";

    @Before
    public void setUp() {
        super.setUp();
        patient = createPatient(1L);
        PatientApi patientApi = new PatientApi(openMRS, openMRSLogger, patientDAO, restApi, locationApi);
        presenter = new AddEditPatientPresenter(view, patientApi, patient, patient.getId().toString(),
                Collections.singletonList("country_"+patient.getId()), restApi);
        mockStaticMethods();
    }

    @Test
    public void shouldNotPassValidation_noGivenName(){
        patient.getPerson().getName().setGivenName(null);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_invalidGivenName(){
        patient.getPerson().getName().setGivenName(INVALID_NAME_1);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_noFamilyName(){
        patient.getPerson().getName().setFamilyName(null);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_invalidFamilyName(){
        patient.getPerson().getName().setFamilyName(INVALID_NAME_2);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_noBirthDate(){
        patient.getPerson().setBirthdate(null);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_notFullAddress(){
        patient.getPerson().setAddresses(Collections.singletonList(new PersonAddress()));
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_invalidAddress1(){
        PersonAddress invalidAddress = new PersonAddress();

        invalidAddress.setAddress1(INVALID_ADDRESS_1);

        patient.getPerson().setAddresses(Collections.singletonList(invalidAddress));
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_invalidAddress2(){
        PersonAddress invalidAddress = new PersonAddress();

        invalidAddress.setAddress2(INVALID_ADDRESS_2);

        patient.getPerson().setAddresses(Collections.singletonList(invalidAddress));
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_notValidCountry(){
        patient.getPerson().getAddress().setCountry("United States Of Poland");
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldNotPassValidation_noGender(){
        patient.getPerson().setGender(null);
        presenter.confirmUpdate(patient);
        verify(view).scrollToTop();
    }

    @Test
    public void shouldUpdatePatient_allOk(){
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        when(restApi.updatePatient(any(), anyString(), anyString()))
                .thenReturn(mockSuccessCall(patient));
        when(restApi.uploadPatientPhoto(anyString(), any()))
                .thenReturn(mockSuccessCall(new PatientPhoto()));

        presenter.confirmUpdate(patient);
        verify(view).setErrorsVisibility(false, false, false, false, false, false);
        verify(view).setProgressBarVisibility(true);
        verify(view).hideSoftKeys();
        verify(view).finishPatientInfoActivity();
    }

    @Test
    public void shouldUpdatePatient_errorResponse(){
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        when(restApi.updatePatient(any(), anyString(), anyString()))
                .thenReturn(mockErrorCall(401));
        when(restApi.uploadPatientPhoto(anyString(), any()))
                .thenReturn(mockSuccessCall(new PatientPhoto()));

        presenter.confirmUpdate(patient);
        verify(view).setErrorsVisibility(false, false, false, false, false, false);
        verify(view).setProgressBarVisibility(true);
        verify(view).hideSoftKeys();
        verify(view).setProgressBarVisibility(false);
    }


    private void mockStaticMethods() {
        PowerMockito.mockStatic(NetworkUtils.class);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.when(OpenMRS.getInstance()).thenReturn(openMRS);
        PowerMockito.when(openMRS.getOpenMRSLogger()).thenReturn(openMRSLogger);
        PowerMockito.mockStatic(ToastUtil.class);
    }
}
