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
package org.openmrs.mobile.utilities

import com.openmrs.android_sdk.library.models.Patient
import com.google.common.base.Objects
import java.util.*

class PatientComparator {
    fun findSimilarPatient(patientList: List<Patient>, patient: Patient): List<Patient> {
        val similarPatients: MutableList<Patient> = LinkedList()
        for (patient1 in patientList) {
            val score = comparePatients(patient1, patient)
            if (score >= MIN_SCORE) {
                similarPatients.add(patient1)
            }
        }
        return similarPatients
    }

    private fun comparePatients(existingPatient: Patient, newPatient: Patient): Int {
        var score = 0

        for (field in PATIENT_FIELDS) {
            score += when (field) {
                "name" -> compareFullPersonName(newPatient, existingPatient)
                "gender" -> compareGender(newPatient, existingPatient)
                "birthdate" -> compareBirthdate(newPatient, existingPatient)
                "addres" -> compareAddress(newPatient, existingPatient)
                else -> 0
            }
        }
        return score
    }

    private fun compareAddress(newPatient: Patient, existingPatient: Patient): Int {
        var score = 0
        if (existingPatient.address != null && newPatient.address != null) {
            if (Objects.equal(newPatient.address.address1, existingPatient.address.address1)) {
                score += 1
            }
            if (Objects.equal(newPatient.address.address2, existingPatient.address.address2)) {
                score += 1
            }
            if (Objects.equal(newPatient.address.cityVillage, existingPatient.address.address2)) {
                score += 1
            }
            if (Objects.equal(newPatient.address.country, existingPatient.address.country)) {
                score += 1
            }
            if (Objects.equal(newPatient.address.stateProvince, existingPatient.address.stateProvince)) {
                score += 1
            }
            if (Objects.equal(newPatient.address.postalCode, existingPatient.address.postalCode)) {
                score += 1
            }
        }
        return if (score == 6) MIN_SCORE - 1 else score
    }

    private fun compareBirthdate(newPatient: Patient, existingPatient: Patient): Int {
        var score = 0
        if (Objects.equal(newPatient.birthdate, existingPatient.birthdate)) {
            score += 1
        }
        return score
    }

    private fun compareGender(newPatient: Patient, existingPatient: Patient): Int {
        var score = 0
        if (Objects.equal(newPatient.gender, existingPatient.gender)) {
            score += 1
        }
        return score
    }

    private fun compareFullPersonName(newPatient: Patient, existingPatient: Patient): Int {
        var score = 0
        if (existingPatient.name != null && newPatient.name != null) {
            if (Objects.equal(newPatient.name.givenName, existingPatient.name.givenName)) {
                score += 1
            }
            if (Objects.equal(newPatient.name.familyName, existingPatient.name.familyName)) {
                score += 1
            }
            if (Objects.equal(newPatient.name.middleName, existingPatient.name.middleName)) {
                score += 1
            }
        }
        //if the whole name is the same we return MIN_SCORE-1 so if any other field will be equal(e.g gender) this patient is marked as similar
        return if (score == 3) MIN_SCORE - 1 else score
    }

    companion object {
        private const val MIN_SCORE = 6
        private val PATIENT_FIELDS = Arrays.asList("name", "gender", "birthdate", "addres")
    }
}