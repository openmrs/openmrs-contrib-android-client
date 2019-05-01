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

import java.io.Serializable;
import java.util.List;

public class PatientAndMatchingPatients implements Serializable {

    private Patient patient;
    private List<Patient> matchingPatientList;

    public PatientAndMatchingPatients(Patient patient, List<Patient> matchingPatientList) {
        this.patient = patient;
        this.matchingPatientList = matchingPatientList;
    }

    public Patient getPatient() {
        return patient;
    }

    public List<Patient> getMatchingPatientList() {
        return matchingPatientList;
    }
}
