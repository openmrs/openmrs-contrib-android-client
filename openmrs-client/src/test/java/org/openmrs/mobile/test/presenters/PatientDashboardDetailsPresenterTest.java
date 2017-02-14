package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.details.PatientDashboardDetailsPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.api.retrofit.VisitApi;
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
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NetworkUtils.class)
public class PatientDashboardDetailsPresenterTest extends ACUnitTestBaseRx {

    @Mock
    private PatientDashboardContract.ViewPatientDetails view;
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private VisitDAO visitDAO;
    @Mock
    private PatientApi patientApi;
    @Mock
    private RestApi restApi;
    @Mock
    private EncounterDAO encounterDAO;

    private PatientDashboardDetailsPresenter presenter;
    private Patient patient;

    @Before
    public void setUp(){
        super.setUp();
        VisitApi visitApi = new VisitApi(restApi, visitDAO, new LocationDAO(), encounterDAO);
        patient = createPatient(1L);
        presenter = new PatientDashboardDetailsPresenter(patient, patientDAO, view, visitApi, patientApi);
        PowerMockito.mockStatic(NetworkUtils.class);
    }


    @Test
    public void shouldSynchronizePatient_onlineMode_successCalls(){
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        when(restApi.findVisitsByPatientUUID(anyString(), anyString()))
                .thenReturn(mockSuccessCall(Collections.singletonList(new Visit())));
        when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(mockSuccessCall(Collections.singletonList(new Encounter())));
        when(visitDAO.saveOrUpdate(any(), anyLong())).thenReturn(Observable.just(1L));

        presenter.synchronizePatient();
        verify(view).showDialog(anyInt());
        verify(view).showToast(anyInt(), eq(false));
        verify(view).dismissDialog();
    }

    @Test
    public void shouldSynchronizePatient_onlineMode_errorCalls(){
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        when(restApi.findVisitsByPatientUUID(anyString(), anyString()))
                .thenReturn(mockErrorCall(401));
        when(restApi.getLastVitals(anyString(), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(mockErrorCall(401));

        presenter.synchronizePatient();
        verify(view).showDialog(anyInt());
        verify(view).showToast(anyInt(), eq(true));
        verify(view).dismissDialog();
    }

    @Test
    public void shouldSynchronizePatient_offlineMode(){
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(false);
        presenter.synchronizePatient();
        verify(view).showToast(anyInt(), eq(true));
        verify(view).resolvePatientDataDisplay(any());
    }

    @Test
    public void shouldShowPatientOnStartup_onlineMode(){
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(true);
        when(patientDAO.findPatientByID(patient.getId().toString())).thenReturn(patient);
        presenter.subscribe();
        verify(view).setMenuTitle(anyString(), anyString());
        verify(view).resolvePatientDataDisplay(any());
        verify(view, never()).attachSnackbarToActivity();
    }

    @Test
    public void shouldShowPatientOnStartup_offlineMode(){
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(false);
        when(patientDAO.findPatientByID(patient.getId().toString())).thenReturn(patient);
        presenter.subscribe();
        verify(view).setMenuTitle(anyString(), anyString());
        verify(view).resolvePatientDataDisplay(any());
        verify(view).attachSnackbarToActivity();
    }
}
