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

import com.google.common.base.Objects;

import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PatientComparator {

    private static final int MIN_SCORE = 6;
    private static final List<String> PATIENT_FIELDS = Arrays.asList("name", "gender",
            "birthdate", "addres");

    public List<Patient> findSimilarPatient(List<Patient> patientList, Patient patient){
        List<Patient> similarPatients = new LinkedList<>();
        for(Patient patient1: patientList){
            int score = comparePatients(patient1, patient);
            if(score >= MIN_SCORE){
                similarPatients.add(patient1);
            }
        }
        return similarPatients;
    }

    private int comparePatients(Patient existingPatient, Patient newPatient) {
        int score = 0;
//        Person newPerson = newPatient.getPerson();
//        Person existingPerson = existingPatient.getPerson();

        for(String field: PATIENT_FIELDS){
            switch (field){
                case "name":
                    score += compareFullPersonName(newPatient, existingPatient);
                    break;
                case "gender":
                    score += compareGender(newPatient, existingPatient);
                    break;
                case "birthdate":
                    score += compareBirthdate(newPatient, existingPatient);
                    break;
                case "addres":
                    score += compareAddress(newPatient, existingPatient);
                    break;
                default:
                    score += 0;
                    break;
            }
        }
        return score;
    }

    private int compareAddress(Patient newPatient, Patient existingPatient) {
        int score = 0;
        if (existingPatient.getAddress() != null && newPatient.getAddress() != null) {
            if(Objects.equal(newPatient.getAddress().getAddress1(), existingPatient.getAddress().getAddress1())){
                score += 1;
            }
            if(Objects.equal(newPatient.getAddress().getAddress2(), existingPatient.getAddress().getAddress2())){
                score += 1;
            }
            if(Objects.equal(newPatient.getAddress().getCityVillage(), existingPatient.getAddress().getAddress2())){
                score += 1;
            }
            if(Objects.equal(newPatient.getAddress().getCountry(), existingPatient.getAddress().getCountry())){
                score += 1;
            }
            if(Objects.equal(newPatient.getAddress().getStateProvince(), existingPatient.getAddress().getStateProvince())){
                score += 1;
            }
            if(Objects.equal(newPatient.getAddress().getPostalCode(), existingPatient.getAddress().getPostalCode())){
                score += 1;
            }
        }
        return score == 6 ? MIN_SCORE-1:score;
    }

    private int compareBirthdate(Patient newPatient, Patient existingPatient) {
        int score = 0;
        if(Objects.equal(newPatient.getBirthdate(), existingPatient.getBirthdate())){
            score += 1;
        }
        return score;
    }

    private int compareGender(Patient newPatient, Patient existingPatient) {
        int score = 0;
        if(Objects.equal(newPatient.getGender(), existingPatient.getGender())){
            score += 1;
        }
        return score;
    }

    private int compareFullPersonName(Patient newPatient, Patient existingPatient) {
        int score = 0;
        if(Objects.equal(newPatient.getName().getGivenName(), existingPatient.getName().getGivenName())){
            score += 1;
        }
        if(Objects.equal(newPatient.getName().getFamilyName(), existingPatient.getName().getFamilyName())){
            score += 1;
        }
        if(Objects.equal(newPatient.getName().getMiddleName(), existingPatient.getName().getMiddleName())){
            score += 1;
        }
        //if the whole name is the same we return MIN_SCORE-1 so if any other field will be equal(e.g gender) this patient is marked as similar
        return score == 3 ? MIN_SCORE-1:score;
    }
}
