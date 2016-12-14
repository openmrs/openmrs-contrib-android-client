package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsContract;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.test.MockCallOnFailure;
import org.openmrs.mobile.test.MockCallOnResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LastViewedPatientsPresenterTest {

    @Captor
    private ArgumentCaptor<Call<List<Patient>>> argumentCaptor;

    @Mock
    private PatientDAO patientDAO;
    @Mock
    private RestApi restApi;
    @Mock
    LastViewedPatientsContract.View view;

    private LastViewedPatientsPresenter lastViewedPatientsPresenter;
    private Patient firstPatient;
    private Patient secondPatient;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        lastViewedPatientsPresenter = new LastViewedPatientsPresenter(
                view,
                restApi,
                patientDAO);
        firstPatient = createPatient(1l);
        secondPatient = createPatient(2l);
        when(patientDAO.isUserAlreadySaved(firstPatient.getUuid())).thenReturn(true);
        when(patientDAO.isUserAlreadySaved(secondPatient.getUuid())).thenReturn(false);
    }

    private Call<Results<Patient>> mockSuccessCall(List<Patient> patientList) {
        Results<Patient> results = new Results<>();
        results.setResults(patientList);
        Response<Results<Patient>> success = Response.success(results);
        return new MockCallOnResponse<>(success);
    }

    private Call<Results<Patient>> mockFailureCall() {
        Throwable throwable = Mockito.mock(Throwable.class);
        return new MockCallOnFailure<>(throwable);
    }

    private Patient createPatient(Long id) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setUuid("patient_one_uuid"+id);
        return patient;
    }

    @Test
    public void shouldUpdateLastViewedPatientList_allOK(){
        List<Patient> patientList = Arrays.asList(firstPatient, secondPatient);
        when(restApi.getLastViewedPatients()).thenReturn(mockSuccessCall(patientList));
        lastViewedPatientsPresenter.updateLastViewedList();
        verify(restApi).getLastViewedPatients();
        verify(view).updateList(Collections.singletonList(secondPatient));
        verify(view).setListVisibility(true);
        verify(view, atLeast(2)).setEmptyListVisibility(false);
        verify(view).stopRefreshing();
    }

    @Test
    public void shouldUpdateLastViewedPatientList_Error(){
        when(restApi.getLastViewedPatients()).thenReturn(mockFailureCall());
        lastViewedPatientsPresenter.updateLastViewedList();
        verify(restApi).getLastViewedPatients();
        verify(view).setSpinnerVisibility(false);
        verify(view, atLeast(2)).setListVisibility(false);
        verify(view).showErrorToast(anyString());
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
        verify(view).setSpinnerVisibility(false);
        verify(view).setListVisibility(false);
        verify(view).showErrorToast(anyString());
        verify(view).stopRefreshing();
    }
}
