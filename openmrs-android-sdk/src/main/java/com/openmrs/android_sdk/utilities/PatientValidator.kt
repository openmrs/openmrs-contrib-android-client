package com.openmrs.android_sdk.utilities

import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.utilities.StringUtils.ILLEGAL_ADDRESS_CHARACTERS
import com.openmrs.android_sdk.utilities.StringUtils.ILLEGAL_CHARACTERS
import com.openmrs.android_sdk.utilities.StringUtils.validateText

/**
 * This utility class validates patient's data presence and legality for registering.
 *
 * @param patient the patient to validate
 * @param isPatientUnidentified whether the patient being registered is unidentified or not
 * @param countriesList the available countries that can be picked from as the patient's country
 */
class PatientValidator(private val patient: Patient,
                       var isPatientUnidentified: Boolean,
                       private val countriesList: List<String>) {

    /**
     * Validates legality and presence of the necessary data of the patient object passed in the constructor
     */
    fun validate(): Boolean = patient.run {
        /* Checks for identified or unidentified patient */
        if (gender.isNullOrBlank()) return false
        if (birthdate.isNullOrBlank()) return false
        if (isPatientUnidentified) return true

        /* Additional checks for identified patient */

        // Validate names
        with(name) {
            if (givenName.isNullOrBlank() || !validateText(givenName, ILLEGAL_CHARACTERS)) return false
            // Middle name can be left empty
            if (middleName != null && !validateText(middleName, ILLEGAL_CHARACTERS)) return false
            if (familyName.isNullOrBlank() || !validateText(familyName, ILLEGAL_CHARACTERS)) return false
        }

        // Validate addresses
        if (address == null) return false
        with(address) {
            if ((address1.isNullOrBlank() && address2.isNullOrBlank())
                    || !validateText(address1, ILLEGAL_ADDRESS_CHARACTERS)
                    || !validateText(address2, ILLEGAL_ADDRESS_CHARACTERS)) {
                return false
            }
            if (country != null && !countriesList.contains(country!!)) return false
        }

        return true
    }
}
