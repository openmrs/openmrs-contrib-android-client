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

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardContract;
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.test.ACUnitTestBaseRx;
import org.openmrs.mobile.utilities.DateUtils;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VisitDashboardPresenterTest extends ACUnitTestBaseRx {

    @Mock
    private VisitDashboardContract.View view;
    @Mock
    private VisitDAO visitDAO;
    @Mock
    private RestApi restApi;

    private VisitDashboardPresenter presenter;
    private Long visitId = 1L;

    @Before
    public void setUp(){
        super.setUp();
        presenter = new VisitDashboardPresenter(restApi, visitDAO, visitId, view);
    }

    @Test
    public void shouldEndVisit_allOK(){
        Visit visit = createVisit(visitId);
        when(restApi.endVisitByUUID(eq(visit.getUuid()), any())).thenReturn(mockSuccessCall(visit));
        when(visitDAO.getVisitByID(visitId)).thenReturn(Observable.just(visit));
        when(visitDAO.saveOrUpdate(any(), eq(visitId))).thenReturn(Observable.just(visitId));
        presenter.endVisit();
        verify(view).moveToPatientDashboard();
    }

    @Test
    public void shouldEndVisit_error(){
        Visit visit = createVisit(visitId);
        when(restApi.endVisitByUUID(eq(visit.getUuid()), any())).thenReturn(mockErrorCall(401));
        when(visitDAO.getVisitByID(visitId)).thenReturn(Observable.just(visit));
        presenter.endVisit();
        verify(view).showErrorToast(any());
    }

    @Test
    public void shouldFillForm_allOK(){
        Visit visit = createVisit(visitId);
        when(visitDAO.getVisitByID(visitId)).thenReturn(Observable.just(visit));
        presenter.fillForm();
        verify(view).startCaptureVitals(visit.getPatient().getId());
    }

    @Test
    public void shouldFillForm_patientNotRegistered(){
        Visit visit = createVisit(visitId);
        visit.getPatient().setUuid(null);
        when(visitDAO.getVisitByID(visitId)).thenReturn(Observable.just(visit));
        presenter.fillForm();
        verify(view).showErrorToast(anyInt());
    }

    @Test
    public void shouldUpdatePatientName_allOK(){
        Visit visit = createVisit(visitId);
        when(visitDAO.getVisitByID(visitId)).thenReturn(Observable.just(visit));
        presenter.updatePatientName();
        verify(view).setActionBarTitle(anyString());
    }

    @Test
    public void shouldInflateMenuForActiveVisit_allOK(){
        Visit visit = createVisit(visitId);
        when(visitDAO.getVisitByID(visitId)).thenReturn(Observable.just(visit));
        presenter.checkIfVisitActive();
        verify(view).setActiveVisitMenu();
    }

    @Test
    public void shouldNotInflateMenuForPastVisit_allOK(){
        Visit visit = createVisit(visitId);
        visit.setStopDatetime(DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
        when(visitDAO.getVisitByID(visitId)).thenReturn(Observable.just(visit));
        presenter.checkIfVisitActive();
        verify(view, atMost(0)).setActiveVisitMenu();
    }

    @NonNull
    private Visit createVisit(Long visitId) {
        Visit visit = new Visit();
        visit.setUuid("some_visit_uuid");
        visit.setPatient(createPatient(1l));
        visit.setId(visitId);
        return visit;
    }
}
