package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsContract;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.openmrs.mobile.test.MockErrorResponse;
import org.openmrs.mobile.test.MockFailure;
import org.openmrs.mobile.test.MockSuccessResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;

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
    LastViewedPatientsContract.View view;

    private LastViewedPatientsPresenter lastViewedPatientsPresenter;
    private Patient firstPatient;
    private Patient secondPatient;

    @Before
    public void setup(){
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
        return new MockSuccessResponse<>(patientList);
    }

    private Call<Results<Patient>> mockErrorCall(int code){
        return new MockErrorResponse<>(code);
    }

    private Call<Results<Patient>> mockFailureCall() {
        Throwable throwable = Mockito.mock(Throwable.class);
        return new MockFailure<>(throwable);
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
    public void shouldUpdateLastViewedPatientList_ServerError(){
        List<Patient> patientList = Arrays.asList(firstPatient, secondPatient);
        when(restApi.getLastViewedPatients()).thenReturn(mockErrorCall(401));
        lastViewedPatientsPresenter.updateLastViewedList();
        verify(restApi).getLastViewedPatients();
        verify(view, atLeast(2)).setListVisibility(false);
        verify(view).setEmptyListVisibility(false);
        verify(view).setEmptyListVisibility(true);
        verify(view).setEmptyListText(anyString());
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
