package org.openmrs.mobile.utilities

import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.utilities.PatientValidator
import org.junit.Assert.assertFalse
import org.junit.Test
import org.openmrs.mobile.test.ACUnitTestBase

class PatientValidatorTest : ACUnitTestBase() {

    private val countries = listOf("country1", "country2", "country3")

    @Test
    fun `validate patient missing first name`() {
        val validator = PatientValidator(
                createValidPatient().apply { name.givenName = null },
                isPatientUnidentified = false,
                countriesList = countries)

        val isValid = validator.validate()

        assertFalse(isValid)
    }

    @Test
    fun `validate patient invalid first name`() {
        val validator = PatientValidator(
                createValidPatient().apply { name.givenName = INVALID_NAME_1 },
                isPatientUnidentified = false,
                countriesList = countries)

        val isValid = validator.validate()

        assertFalse(isValid)
    }

    @Test
    fun `validate patient invalid middle name`() {
        val validator = PatientValidator(
                createValidPatient().apply { name.middleName = INVALID_NAME_2 },
                isPatientUnidentified = false,
                countriesList = countries
        )

        val isValid = validator.validate()

        assertFalse(isValid)
    }

    @Test
    fun `validate patient missing family name`() {
        val validator = PatientValidator(
                createValidPatient().apply { name.familyName = null },
                isPatientUnidentified = false,
                countriesList = countries
        )

        val isValid = validator.validate()

        assertFalse(isValid)
    }

    @Test
    fun `validate patient with invalid family name`() {
        val validator = PatientValidator(
                createValidPatient().apply { name.familyName = INVALID_NAME_3 },
                isPatientUnidentified = false,
                countriesList = countries
        )

        val isValid = validator.validate()

        assertFalse(isValid)
    }

    @Test
    fun `validate patient missing gender`() {
        val validator = PatientValidator(
                createValidPatient().apply { gender = null },
                isPatientUnidentified = false,
                countriesList = countries
        )

        val isValid = validator.validate()

        assertFalse(isValid)
    }

    @Test
    fun `validate patient missing birthdate`() {
        val validator = PatientValidator(
                createValidPatient().apply { birthdate = null },
                isPatientUnidentified = false,
                countriesList = countries
        )

        val isValid = validator.validate()

        assertFalse(isValid)
    }

    @Test
    fun `validate patient invalid address`() {
        val validator1 = PatientValidator(
                createValidPatient().apply { addresses = emptyList() },
                isPatientUnidentified = false,
                countriesList = countries
        )
        val validator2 = PatientValidator(
                createValidPatient().apply { addresses = listOf(createPersonAddress(1).apply { address1 = INVALID_ADDRESS_1 }) },
                isPatientUnidentified = false,
                countriesList = countries
        )
        val validator3 = PatientValidator(
                createValidPatient().apply { addresses = listOf(createPersonAddress(1).apply { address2 = INVALID_ADDRESS_2 }) },
                isPatientUnidentified = false,
                countriesList = countries
        )

        val isValid = validator1.validate() && validator2.validate() && validator3.validate()

        assertFalse(isValid)
    }

    @Test
    fun `validate patient invalid country`() {
        val validator = PatientValidator(
                createValidPatient().apply { address.country = INVALID_COUNTRY },
                isPatientUnidentified = false,
                countriesList = countries
        )

        val isValid = validator.validate()

        assertFalse(isValid)
    }

    private fun createValidPatient() = updatePatientData(1L, Patient())

    companion object {
        private const val INVALID_NAME_1 = "#James"
        private const val INVALID_NAME_2 = "John@Doe"
        private const val INVALID_NAME_3 = "Em*%ile"
        private const val INVALID_ADDRESS_1 = "Washington street ^%123"
        private const val INVALID_ADDRESS_2 = "Door $164"
        private const val INVALID_COUNTRY = "No country"
    }
}
