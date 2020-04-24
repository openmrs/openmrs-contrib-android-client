package org.openmrs.mobile.utilities

import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.models.PersonAddress
import org.openmrs.mobile.models.PersonName


class PatientMerger {
    fun mergePatient(oldPatient: Patient, newPatient: Patient): Patient {
        mergePatientsPerson(oldPatient, newPatient)
        oldPatient.id = newPatient.id
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
        return if (!StringUtils.notNull(oldValue)) {
            newValue
        } else oldValue
    }
}