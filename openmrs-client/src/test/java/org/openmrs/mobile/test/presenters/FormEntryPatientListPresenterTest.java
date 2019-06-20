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
import org.mockito.Mockito;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.formentrypatientlist.FormEntryPatientListContract;
import org.openmrs.mobile.activities.formentrypatientlist.FormEntryPatientListPresenter;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.test.ACUnitTestBaseRx;
import org.openmrs.mobile.utilities.StringUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@PrepareForTest(StringUtils.class)
public class FormEntryPatientListPresenterTest extends ACUnitTestBaseRx {

    @Mock
    private FormEntryPatientListContract.View view;
    @Mock
    private PatientDAO patientDAO;

    private FormEntryPatientListPresenter presenter;
    private int NO_STRING_ID = R.string.last_vitals_none_label;
    private String mQuery = "someQuery";
    private List<Patient> patientList;

    @Before
    public void setUp() {
        super.setUp();
        presenter = new FormEntryPatientListPresenter(view, patientDAO);
        PowerMockito.mockStatic(StringUtils.class);
        patientList = new ArrayList<>();
    }

    @Test
    public void shouldUpdatePatientList_allOk() {
        Patient patient = createPatient(1l, mQuery);
        patientList.add(patient);

        Mockito.lenient().when(StringUtils.notNull(anyString())).thenReturn(true);
        Mockito.lenient().when(patientDAO.getAllPatients()).thenReturn(Observable.just(patientList));

        presenter.setQuery(mQuery);
        presenter.subscribe();

        verify(view).updateListVisibility(true, NO_STRING_ID, null);
        verify(view).updateAdapter(patientList);
    }

    @Test
    public void shouldUpdatePatientList_emptyPatientList() {
        Mockito.lenient().when(StringUtils.notNull(anyString())).thenReturn(true);
        Mockito.lenient().when(patientDAO.getAllPatients()).thenReturn(Observable.just(patientList));

        presenter.setQuery(mQuery);
        presenter.subscribe();

        int NO_RESULT_FOR_QUERY_STRING_ID = R.string.search_patient_no_result_for_query;
        verify(view).updateListVisibility(false, NO_RESULT_FOR_QUERY_STRING_ID, mQuery);
        verify(view).updateAdapter(patientList);
    }


    @Test
    public void shouldUpdatePatientList_nullQuery() {
        Patient patient = createPatient(1l);
        patientList.add(patient);

        Mockito.lenient().when(StringUtils.notNull(anyString())).thenReturn(false);
        Mockito.lenient().when(patientDAO.getAllPatients()).thenReturn(Observable.just(patientList));

        presenter.setQuery(null);
        presenter.subscribe();

        verify(view).updateListVisibility(true, NO_STRING_ID, null);
        verify(view).updateAdapter(patientList);
    }

    @Test
    public void shouldUpdatePatientList_nullQueryEmptyPatientList() {
        Mockito.lenient().when(StringUtils.notNull(anyString())).thenReturn(false);
        Mockito.lenient().when(patientDAO.getAllPatients()).thenReturn(Observable.just(patientList));

        presenter.setQuery(null);
        presenter.subscribe();

        int NO_RESULT_STRING_ID = R.string.search_patient_no_results;
        verify(view).updateListVisibility(false, NO_RESULT_STRING_ID, null);
        verify(view).updateAdapter(patientList);
    }
}
