package org.openmrs.mobile.utilities

import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.models.Visit
import java.util.*


object FilterUtil {
    /**
     * Used to filter list by specified query
     * Its possible to filter patients by: Name, Surname (Family Name) or ID.
     * @param patientList list of patients to filter
     * @param query query that needs to be contained in Name, Surname or ID.
     * @return patient list filtered by query
     */
    fun getPatientsFilteredByQuery(patientList: List<Patient>, query: String): List<Patient> {
        val filteredList: MutableList<Patient> = ArrayList()
        for (patient in patientList) {
            val searchableWords = getPatientSearchableWords(patient)
            if (doesAnySearchableWordFitQuery(searchableWords, query)) {
                filteredList.add(patient)
            }
        }
        return filteredList
    }

    fun getPatientsWithActiveVisitsFilteredByQuery(visitList: List<Visit>, query: String): List<Visit> {
        val filteredList: MutableList<Visit> = ArrayList()
        for (visit in visitList) {
            val patient = visit.patient
            val patientsWithActiveVisitsSearchableWords: MutableList<String?> = ArrayList()
            patientsWithActiveVisitsSearchableWords.addAll(getVisitSearchableWords(visit))
            patientsWithActiveVisitsSearchableWords.addAll(getPatientSearchableWords(patient))
            if (doesAnySearchableWordFitQuery(patientsWithActiveVisitsSearchableWords, query)) {
                filteredList.add(visit)
            }
        }
        return filteredList
    }

    private fun getPatientSearchableWords(patient: Patient): List<String?> {
        val patientIdentifier = patient.identifier.identifier
        val fullName = patient.name.nameString
        val givenFamilyName = (patient.name.givenName + " "
                + patient.name.familyName)
        val searchableWords: MutableList<String?> = ArrayList()
        searchableWords.add(patientIdentifier)
        searchableWords.add(fullName)
        searchableWords.add(givenFamilyName)
        return searchableWords
    }

    private fun getVisitSearchableWords(visit: Visit): List<String> {
        val visitPlace = visit.location.display
        val visitType = visit.visitType.display
        val searchableWords: MutableList<String> = ArrayList()
        searchableWords.add(visitPlace)
        searchableWords.add(visitType)
        return searchableWords
    }

    private fun doesAnySearchableWordFitQuery(searchableWords: List<String?>, query: String): Boolean {
        var query = query
        var i = 0
        while (i<searchableWords.size) {
            var searchableWord = searchableWords[i]
            if (searchableWord != null) {
                val queryLength = query.trim { it <= ' ' }.length
                searchableWord = searchableWord.toLowerCase()
                query = query.toLowerCase().trim { it <= ' ' }
                val fits = searchableWord.length >= queryLength && searchableWord.contains(query)
                if (fits) {
                    return true
                }
            }
            i++
        }
        return false
    }
}
