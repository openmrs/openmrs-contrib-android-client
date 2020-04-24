package org.openmrs.mobile.utilities

import com.google.common.base.Objects
import org.openmrs.mobile.models.Patient
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
        //        Person newPerson = newPatient.getPerson();
//        Person existingPerson = existingPatient.getPerson();
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
        if (Objects.equal(newPatient.name.givenName, existingPatient.name.givenName)) {
            score += 1
        }
        if (Objects.equal(newPatient.name.familyName, existingPatient.name.familyName)) {
            score += 1
        }
        if (Objects.equal(newPatient.name.middleName, existingPatient.name.middleName)) {
            score += 1
        }
        //if the whole name is the same we return MIN_SCORE-1 so if any other field will be equal(e.g gender) this patient is marked as similar
        return if (score == 3) MIN_SCORE - 1 else score
    }

    companion object {
        private const val MIN_SCORE = 6
        private val PATIENT_FIELDS = Arrays.asList("name", "gender",
                "birthdate", "addres")
    }
}
