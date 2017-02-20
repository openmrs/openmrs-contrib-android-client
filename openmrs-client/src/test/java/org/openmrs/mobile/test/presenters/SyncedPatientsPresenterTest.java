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
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsContract;
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsPresenter;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.test.ACUnitTestBaseRx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncedPatientsPresenterTest extends ACUnitTestBaseRx {

    @Mock
    private SyncedPatientsContract.View view;

    @Mock
    private PatientDAO patientDAO;

    private SyncedPatientsPresenter syncedPatientsPresenter;

    private List<Patient> patientList;

    @Before
    public void setUp() {
        super.setUp();
        syncedPatientsPresenter = new SyncedPatientsPresenter(view, patientDAO);

        patientList = new ArrayList<>();

        patientList.add(createPatient(1L));
        patientList.add(createPatient(2L));
        patientList.add(createPatient(3L));
    }

    @Test
    public void shouldShowListWhenFoundPatientsinDatabase() {
        when(patientDAO.getAllPatients()).thenReturn(Observable.just(patientList));
        syncedPatientsPresenter.updateLocalPatientsList();
        verify(view).updateListVisibility(true);
        verify(view).updateAdapter(patientList);
    }

    @Test
    public void shouldShowListWhenFoundPatientsByQuery() {
        final String query = "given_name_3";
        when(patientDAO.getAllPatients()).thenReturn(Observable.just(patientList));
        syncedPatientsPresenter.setQuery(query);
        syncedPatientsPresenter.updateLocalPatientsList();
        verify(view).updateListVisibility(true);
        verify(view).updateAdapter(Collections.singletonList(patientList.get(2)));
    }

    @Test
    public void shouldShowEmptyPatientsDatabaseMessage() {
        when(patientDAO.getAllPatients()).thenReturn(Observable.just(new ArrayList<>()));
        syncedPatientsPresenter.updateLocalPatientsList();
        verify(view).updateListVisibility(false);
    }

    @Test
    public void shouldShowNoResultsForQueryMessage() {
        final String notExistingPatientName = "Patient_20";
        when(patientDAO.getAllPatients()).thenReturn(Observable.just(patientList));
        syncedPatientsPresenter.setQuery(notExistingPatientName);
        syncedPatientsPresenter.updateLocalPatientsList();
        verify(view).updateListVisibility(false, notExistingPatientName);
    }

}
