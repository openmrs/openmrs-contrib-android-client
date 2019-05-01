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

package org.openmrs.mobile.utilities;

import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

import java.util.ArrayList;
import java.util.List;

public class FilterUtil {

    /**
     * Used to filter list by specified query
     * Its possible to filter patients by: Name, Surname (Family Name) or ID.
     * @param patientList list of patients to filter
     * @param query query that needs to be contained in Name, Surname or ID.
     * @return patient list filtered by query
     */
    public static List<Patient> getPatientsFilteredByQuery(List<Patient> patientList, String query) {
        List<Patient> filteredList = new ArrayList<>();

        for (Patient patient : patientList) {

            List<String> searchableWords = getPatientSearchableWords(patient);

            if (doesAnySearchableWordFitQuery(searchableWords, query)) {
                filteredList.add(patient);
            }

        }
        return filteredList;
    }

    public static List<Visit> getPatientsWithActiveVisitsFilteredByQuery(List<Visit> visitList, String query) {
        List<Visit> filteredList = new ArrayList<>();

        for (Visit visit : visitList) {
            Patient patient = visit.getPatient();
            List<String> patientsWithActiveVisitsSearchableWords = new ArrayList<>();
            patientsWithActiveVisitsSearchableWords.addAll(getVisitSearchableWords(visit));
            patientsWithActiveVisitsSearchableWords.addAll(getPatientSearchableWords(patient));

            if (doesAnySearchableWordFitQuery(patientsWithActiveVisitsSearchableWords, query)) {
                filteredList.add(visit);
            }
        }
        return filteredList;
    }

    private static List<String> getPatientSearchableWords(Patient patient) {
        String patientIdentifier = patient.getIdentifier().getIdentifier();
        String fullName = patient.getPerson().getName().getNameString();
        String givenFamilyName = patient.getPerson().getName().getGivenName() + " "
                + patient.getPerson().getName().getFamilyName();

        List<String> searchableWords = new ArrayList<>();
        searchableWords.add(patientIdentifier);
        searchableWords.add(fullName);
        searchableWords.add(givenFamilyName);

        return searchableWords;
    }

    private static List<String> getVisitSearchableWords(Visit visit) {
        String visitPlace = visit.getLocation().getDisplay();
        String visitType = visit.getVisitType().getDisplay();

        List<String> searchableWords = new ArrayList<>();
        searchableWords.add(visitPlace);
        searchableWords.add(visitType);

        return searchableWords;
    }

    private static boolean doesAnySearchableWordFitQuery(List<String> searchableWords, String query) {
        for (String searchableWord : searchableWords) {
            if (searchableWord != null) {
                int queryLength = query.trim().length();
                searchableWord = searchableWord.toLowerCase();
                query = query.toLowerCase().trim();
                boolean fits = searchableWord.length() >= queryLength && searchableWord.contains(query);
                if (fits) {
                    return true;
                }
            }
        }
        return false;
    }

}
