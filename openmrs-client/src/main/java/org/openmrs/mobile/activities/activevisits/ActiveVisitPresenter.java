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

package org.openmrs.mobile.activities.activevisits;

import org.openmrs.mobile.R;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

import java.util.ArrayList;
import java.util.List;

public class ActiveVisitPresenter implements ActiveVisitsContract.Presenter{

    private ActiveVisitsContract.View mActiveVisitsView;

    public ActiveVisitPresenter(ActiveVisitsContract.View mActiveVisitsView) {
        this.mActiveVisitsView = mActiveVisitsView;
        this.mActiveVisitsView.setPresenter(this);
    }

    @Override
    public void start() {
        updateVisitsInDatabaseList();
    }


    private List<Visit> getPatientsFilteredByQuery(List<Visit> visitList, String query) {
        List<Visit> filteredList = new ArrayList<>();
        String queryLowerCase = query.toLowerCase();

        for (Visit visit : visitList) {
            Patient patient = new PatientDAO().findPatientByID(visit.getPatient().getId().toString());

            String visitPlace = visit.getLocation().getDisplay().toLowerCase();
            String visitType = visit.getVisitType().getDisplay().toLowerCase();
            String patientName = patient.getPerson().getNames().get(0).getGivenName().toLowerCase();
            String patientSurname = patient.getPerson().getNames().get(0).getFamilyName().toLowerCase();
            String patientIdentifier = patient.getIdentifier().getIdentifier().toLowerCase();

            boolean isVisitPlaceFitQuery = visitPlace.length() >= queryLowerCase.length() && visitPlace.substring(0, queryLowerCase.length()).equals(queryLowerCase);
            boolean isVisitTypeFitQuery = visitType.length() >= queryLowerCase.length() && visitType.substring(0, queryLowerCase.length()).equals(queryLowerCase);
            boolean isPatientNameFitQuery = patientName.length() >= queryLowerCase.length() && patientName.substring(0, queryLowerCase.length()).equals(queryLowerCase);
            boolean isPatientSurnameFitQuery = patientSurname.length() >= queryLowerCase.length() && patientSurname.substring(0, queryLowerCase.length()).equals(queryLowerCase);
            boolean isPatientIdentifierFitQuery = false;
            if (patientIdentifier != null) {
                isPatientIdentifierFitQuery = patientIdentifier.length() >= queryLowerCase.length() && patientIdentifier.substring(0, queryLowerCase.length()).equals(queryLowerCase);
            }
            if (isPatientNameFitQuery || isPatientSurnameFitQuery || isPatientIdentifierFitQuery || isVisitPlaceFitQuery || isVisitTypeFitQuery) {
                filteredList.add(visit);
            }
        }
        return filteredList;
    }

    @Override
    public void updateVisitsInDatabaseList() {
        mActiveVisitsView.setEmptyListText(R.string.search_visits_no_results);
        List<Visit> visits = new VisitDAO().getAllActiveVisits();
        mActiveVisitsView.updateListVisibility(visits);
    }

    public void updateVisitsInDatabaseList(String query) {
        mActiveVisitsView.setEmptyListText(R.string.search_patient_no_result_for_query, query);
        List<Visit> visits = getPatientsFilteredByQuery(new VisitDAO().getAllActiveVisits(), query);
        mActiveVisitsView.updateListVisibility(visits);
    }
}
