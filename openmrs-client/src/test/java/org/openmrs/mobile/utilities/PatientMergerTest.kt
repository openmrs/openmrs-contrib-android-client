package org.openmrs.mobile.utilities

import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.PersonAddress
import com.openmrs.android_sdk.library.models.PersonName
import com.openmrs.android_sdk.library.models.Resource
import org.junit.Test
import org.junit.Assert.*
import org.openmrs.mobile.test.ACUnitTestBase

class PatientMergerTest : ACUnitTestBase() {

    @Test
    fun nonNullFields_remainSame() {
        val oldPatient = createPatientObject(1)
        val newPatient = createPatientObject(2)

        PatientMerger().mergePatient(oldPatient, newPatient)

        assertEquals(oldPatient.id, newPatient.id)

        assertNotEquals(oldPatient.name, newPatient.name)
        assertNotEquals(oldPatient.address, newPatient.address)
        assertNotEquals(oldPatient.birthdate, newPatient.birthdate)
        assertNotEquals(oldPatient.gender, newPatient.gender)
    }

    @Test
    fun nonNullFields_remainSame_nullFields_changes() {
        val oldPatient = createPatientWithNullFields(1)
        val newPatient = createPatientObject(2)

        PatientMerger().mergePatient(oldPatient, newPatient)

        assertEquals(oldPatient.id, newPatient.id)

        assertNotEquals(oldPatient.name.givenName, newPatient.name.givenName)

        assertEquals(oldPatient.name.middleName, newPatient.name.middleName)
        assertEquals(oldPatient.name.familyName, newPatient.name.familyName)

        assertEquals(oldPatient.address.address1, newPatient.address.address1)
        assertEquals(oldPatient.address.address2, newPatient.address.address2)
        assertEquals(oldPatient.address.stateProvince, newPatient.address.stateProvince)

        assertNotEquals(oldPatient.address.cityVillage, newPatient.address.cityVillage)
        assertNotEquals(oldPatient.address.country, newPatient.address.country)
        assertNotEquals(oldPatient.address.postalCode, newPatient.address.postalCode)

        assertEquals(oldPatient.gender, newPatient.gender)
        assertEquals(oldPatient.birthdate, newPatient.birthdate)
    }

    private fun createPatientObject(id: Long): Patient {
        return Patient(
            id, "", null,
            listOf(createPersonName(id)), "M_$id", "birth_date_$id",
            false,
            listOf(createPersonAddress(id)), null, null,
            Resource(), false
        )
    }

    private fun createPatientWithNullFields(id: Long): Patient {
        val name = PersonName()
        name.givenName = "given_name_$id"
        name.middleName = "null"
        name.familyName = null

        val address = PersonAddress()
        address.address1 = null
        address.address2 = "null"
        address.cityVillage = "city_$id"
        address.stateProvince = null
        address.country = "country_$id"
        address.postalCode = "postal_code_$id"

        return Patient(
            id, "", null,
            listOf(name), "null", null, false,
            listOf(address), null, null, Resource(), false
        )
    }

}