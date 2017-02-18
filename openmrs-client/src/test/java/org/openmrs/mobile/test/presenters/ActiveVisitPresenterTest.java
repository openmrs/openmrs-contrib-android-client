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
import org.openmrs.mobile.activities.activevisits.ActiveVisitPresenter;
import org.openmrs.mobile.activities.activevisits.ActiveVisitsContract;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.test.ACUnitTestBaseRx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActiveVisitPresenterTest extends ACUnitTestBaseRx {

    @Mock
    private ActiveVisitsContract.View view;

    @Mock
    private VisitDAO visitDAO;

    private ActiveVisitPresenter presenter;
    private List<Visit> visitList;

    @Before
    public void setUp(){
        super.setUp();
        presenter = new ActiveVisitPresenter(view, visitDAO);
        visitList = createVisitList();
    }

    @Test
    public void updateVisitsFromDB_allOK(){
        when(visitDAO.getActiveVisits()).thenReturn(Observable.just(visitList));
        presenter.updateVisitsInDatabaseList();
        verify(view).setEmptyListText(anyInt());
        verify(view).updateListVisibility(visitList);
    }

    @Test
    public void updateVisitsFromDB_error(){
        when(visitDAO.getActiveVisits()).thenReturn(Observable.error(new Throwable("error")));
        presenter.updateVisitsInDatabaseList();
        verify(view, atLeast(2)).setEmptyListText(anyInt());
    }

    @Test
    public void updateVisitsFromDBWithQuery_allOK(){
        when(visitDAO.getActiveVisits()).thenReturn(Observable.just(visitList));
        String query = "visit1";
        presenter.updateVisitsInDatabaseList(query);
        verify(view).setEmptyListText(anyInt(), eq(query));
        verify(view).updateListVisibility(Collections.singletonList(visitList.get(0)));
    }

    @Test
    public void updateVisitsFromDBWithQuery_error(){
        when(visitDAO.getActiveVisits()).thenReturn(Observable.error(new Throwable("error")));
        String query = "visit1";
        presenter.updateVisitsInDatabaseList(query);
        verify(view, atLeast(2)).setEmptyListText(anyInt(), eq(query));
    }

    private List<Visit> createVisitList() {
        List<Visit> list = new ArrayList<>();
        list.add(createVisit("visit1"));
        list.add(createVisit("visit2"));
        return list;
    }

    @NonNull
    private Visit createVisit(String display) {
        Visit visit = new Visit();
        visit.setLocation(new Location(display));
        visit.setVisitType(new VisitType(display));
        visit.setPatient(createPatient(1l));
        return visit;
    }
}
