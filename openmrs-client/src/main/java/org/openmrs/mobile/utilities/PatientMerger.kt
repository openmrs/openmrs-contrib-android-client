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
import com.openmrs.android_sdk.library.models.PersonAddress
import com.openmrs.android_sdk.library.models.PersonName
import com.openmrs.android_sdk.utilities.StringUtils.notNull

class PatientMerger {
    fun mergePatient(oldPatient: Patient, newPatient: Patient): Patient {
        mergePatientsPerson(oldPatient, newPatient)
        oldPatient.id=newPatient.id
        return oldPatient
    }

    private fun mergePatientsPerson(oldPatient: Patient, newPatient: Patient) {
        mergePersonNames(oldPatient.name, newPatient.name)
        mergePersonAddress(oldPatient.address, newPatient.address)
        oldPatient.gender = getNewValueIfOldIsNull(oldPatient.gender, newPatient.gender)
        oldPatient.birthdate = getNewValueIfOldIsNull(oldPatient.birthdate, newPatient.birthdate)
    }

    private fun mergePersonAddress(oldAddress: PersonAddress, newAddress: PersonAddress) {
        oldAddress.address1 = getNewValueIfOldIsNull(oldAddress.address1, newAddress.address1)
        oldAddress.address2 = getNewValueIfOldIsNull(oldAddress.address2, newAddress.address2)
        oldAddress.cityVillage = getNewValueIfOldIsNull(oldAddress.cityVillage, newAddress.cityVillage)
        oldAddress.country = getNewValueIfOldIsNull(oldAddress.country, newAddress.country)
        oldAddress.postalCode = getNewValueIfOldIsNull(oldAddress.postalCode, newAddress.postalCode)
        oldAddress.stateProvince = getNewValueIfOldIsNull(oldAddress.stateProvince, newAddress.stateProvince)
    }

    private fun mergePersonNames(oldName: PersonName, newName: PersonName) {
        oldName.givenName = getNewValueIfOldIsNull(oldName.givenName, newName.givenName)
        oldName.middleName = getNewValueIfOldIsNull(oldName.middleName, newName.middleName)
        oldName.familyName = getNewValueIfOldIsNull(oldName.familyName, newName.familyName)
    }

    private fun getNewValueIfOldIsNull(oldValue: String?, newValue: String?): String? {
        return if (!notNull(oldValue)) {
            newValue
        } else oldValue
    }
}