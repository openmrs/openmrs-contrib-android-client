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
import com.openmrs.android_sdk.library.models.Visit

object FilterUtil {
    /**
     * Used to filter list by specified query
     * Its possible to filter patients by: Name, Surname (Family Name) or ID.
     * @param patientList list of patients to filter
     * @param query query that needs to be contained in Name, Surname or ID.
     * @return patient list filtered by query
     */
    @JvmStatic
    fun getPatientsFilteredByQuery(patientList: List<Patient?>, query: String?): List<Patient> {
        val filteredList: MutableList<Patient> = ArrayList()
        for (patient in patientList) {
            val searchableWords = getPatientSearchableWords(patient)
            if (doesAnySearchableWordFitQuery(searchableWords, query)) {
                if (patient != null) {
                    filteredList.add(patient)
                }
            }
        }
        return filteredList
    }

    @JvmStatic
    fun getPatientsWithActiveVisitsFilteredByQuery(visitList: List<Visit?>?, query: String?): List<Visit> {
        val filteredList: MutableList<Visit> = ArrayList()
        if (visitList != null) {
            for (visit in visitList) {
                val patient = visit?.patient
                val patientsWithActiveVisitsSearchableWords: MutableList<String?> = ArrayList()
                patientsWithActiveVisitsSearchableWords.addAll(getVisitSearchableWords(visit))
                patientsWithActiveVisitsSearchableWords.addAll(getPatientSearchableWords(patient))
                if (doesAnySearchableWordFitQuery(patientsWithActiveVisitsSearchableWords, query)) {
                    if (visit != null) {
                        filteredList.add(visit)
                    }
                }
            }
        }
        return filteredList
    }

    @JvmStatic
    private fun getPatientSearchableWords(patient: Patient?): List<String?> {
        val patientIdentifier = patient?.identifier?.identifier
        val fullName = patient?.name?.nameString
        val givenFamilyName = "${patient?.name?.givenName} ${" "} ${patient?.name?.familyName}"
        val searchableWords: MutableList<String?> = ArrayList()
        searchableWords.add(patientIdentifier)
        searchableWords.add(fullName)
        searchableWords.add(givenFamilyName)
        return searchableWords
    }

    @JvmStatic
    private fun getVisitSearchableWords(visit: Visit?): List<String> {
        val visitPlace = visit?.location?.display
        val visitType = visit?.visitType?.display
        val searchableWords: MutableList<String> = ArrayList()
        searchableWords.add(visitPlace!!)
        searchableWords.add(visitType!!)
        return searchableWords
    }

    @JvmStatic
    private fun doesAnySearchableWordFitQuery(searchableWords: List<String?>, query: String?): Boolean {
        var mutableQuery = query
        var i = 0
        while (i < searchableWords.size) {
            var searchableWord = searchableWords[i]
            if (searchableWord != null) {
                val queryLength = mutableQuery?.trim { it <= ' ' }?.length
                searchableWord = searchableWord.toLowerCase()
                mutableQuery = mutableQuery?.toLowerCase()?.trim { it <= ' ' }
                val fits = searchableWord.length >= queryLength!! && searchableWord.contains(mutableQuery.toString())
                if (fits) {
                    return true
                }
            }
            i++
        }
        return false
    }
}