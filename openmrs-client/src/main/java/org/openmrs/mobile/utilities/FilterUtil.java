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
        String queryLowerCase = query.toLowerCase();

        for (Patient patient : patientList) {

            String patientName = patient.getPerson().getNames().get(0).getGivenName().toLowerCase();
            String patientSurname = patient.getPerson().getNames().get(0).getFamilyName().toLowerCase();
            String patientIdentifier = patient.getIdentifier().getIdentifier();

            boolean isPatientNameFitQuery = patientName.length() >= queryLowerCase.length() && patientName.substring(0,queryLowerCase.length()).equals(queryLowerCase);
            boolean isPatientSurnameFitQuery = patientSurname.length() >= queryLowerCase.length() && patientSurname.substring(0,queryLowerCase.length()).equals(queryLowerCase);
            boolean isPatientIdentifierFitQuery = false;
            if (patientIdentifier != null) {
                isPatientIdentifierFitQuery = patientIdentifier.length() >= queryLowerCase.length() && patientIdentifier.substring(0,queryLowerCase.length()).toLowerCase().equals(queryLowerCase);
            }
            if (isPatientNameFitQuery || isPatientSurnameFitQuery || isPatientIdentifierFitQuery) {
                filteredList.add(patient);
            }
        }
        return filteredList;
    }

}
