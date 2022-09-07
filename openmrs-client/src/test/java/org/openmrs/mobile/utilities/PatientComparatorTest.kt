package org.openmrs.mobile.utilities

import android.graphics.Bitmap
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.PatientIdentifier
import com.openmrs.android_sdk.library.models.PersonAddress
import com.openmrs.android_sdk.library.models.PersonAttribute
import com.openmrs.android_sdk.library.models.PersonName
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.utilities.PatientComparator
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PatientComparatorTest {

    private val patientList = mutableListOf<Patient>()
    private val givenNameList = mutableListOf<String>()
    private val middleNameList = mutableListOf<String>()
    private val familyNameList = mutableListOf<String>()
    private val genderList = mutableListOf<String>()
    private val birthdateList = mutableListOf<String>()
    private val address1List = mutableListOf<String>()
    private val address2List = mutableListOf<String>()
    private val cityVillageList = mutableListOf<String>()
    private val stateProvinceList = mutableListOf<String>()
    private val countryList = mutableListOf<String>()
    private val postalCodeList = mutableListOf<String>()

    @Before
    fun setup() {
        givenNameList.addAll(listOf("Michael", "Dwight", "Jim", "Stanley", "Kelly"))
        middleNameList.addAll(listOf("a", "aa", "aaa", "aaaa", "aaaaa"))
        familyNameList.addAll(listOf("Scott", "Schrute", "Halpert", "Hudson", "Kapoor"))
        genderList.addAll(listOf("m", "f", "m", "f", "m"))
        birthdateList.addAll(listOf("15-07-1965", "16-08-1966", "17-09-1967", "18-010-1968", "19-11-1969"))
        address1List.addAll(listOf("b", "bb", "bbb", "bbbb", "bbbbb"))
        address2List.addAll(listOf("c", "cc", "ccc", "cccc", "ccccc"))
        cityVillageList.addAll(listOf("d", "dd", "ddd", "dddd", "ddddd"))
        stateProvinceList.addAll(listOf("e", "ee", "eee", "eeee", "eeeee"))
        countryList.addAll(listOf("India", "Germany", "USA", "UK", "Australia"))
        postalCodeList.addAll(listOf("1111", "2222", "3333", "4444", "5555"))

        for (i in 0..4) {
            val givenName = givenNameList[i]
            val middleName = middleNameList[i]
            val familyName = familyNameList[i]
            val gender = genderList[i]
            val birthdate = birthdateList[i]
            val address1 = address1List[i]
            val address2 = address2List[i]
            val cityVillage = cityVillageList[i]
            val stateProvince = stateProvinceList[i]
            val country = countryList[i]
            val postalcode = postalCodeList[i]

            val personName = PersonName()
            personName.givenName = givenName
            personName.middleName = middleName
            personName.familyName = familyName

            val personAddress = PersonAddress()
            personAddress.address1 = address1
            personAddress.address2 = address2
            personAddress.stateProvince = stateProvince
            personAddress.cityVillage = cityVillage
            personAddress.country = country
            personAddress.postalCode = postalcode

            val id: Long = 1
            val identifiers = PatientIdentifier()
            val attributes = PersonAttribute()
            val cod = Resource()
            val photo: Bitmap? = null

            val existingPatient = Patient(id, "", mutableListOf(identifiers), mutableListOf(personName), gender, birthdate, true, mutableListOf(personAddress), mutableListOf(attributes), photo, cod, false)
            patientList.add(existingPatient)
        }
    }

    @After
    fun destroy() {
        patientList.clear()
        givenNameList.clear()
        middleNameList.clear()
        familyNameList.clear()
        genderList.clear()
        birthdateList.clear()
        address1List.clear()
        address2List.clear()
        cityVillageList.clear()
        stateProvinceList.clear()
        countryList.clear()
        postalCodeList.clear()
    }

    @Test
    fun patientMatchesWithExistingPatient_returnsPatient() {
        val givenName = "Michael"
        val middleName = "a"
        val familyName = "Scott"
        val address1 = "b"
        val address2 = "c"
        val cityVillage = "d"
        val stateProvince = "e"
        val country = "India"
        val postalCode = "1111"
        val gender = "m"
        val birthdate = "15-07-1965"

        val personName = PersonName()
        personName.givenName = givenName
        personName.middleName = middleName
        personName.familyName = familyName

        val personAddress = PersonAddress()
        personAddress.address1 = address1
        personAddress.address2 = address2
        personAddress.stateProvince = stateProvince
        personAddress.cityVillage = cityVillage
        personAddress.country = country
        personAddress.postalCode = postalCode

        val id: Long = 1
        val identifiers = PatientIdentifier()
        val attributes = PersonAttribute()
        val cod = Resource()
        val photo: Bitmap? = null

        val patientToCheck = Patient(id, "", mutableListOf(identifiers), mutableListOf(personName), gender, birthdate, true, mutableListOf(personAddress), mutableListOf(attributes), photo, cod, false)
        val result = PatientComparator().findSimilarPatient(patientList, patientToCheck)
        val expected = mutableListOf<Patient>()
        expected.add(patientToCheck)

        assertEquals(expected[0].name.givenName, result[0].name.givenName)
        assertEquals(expected[0].name.familyName, result[0].name.familyName)
        assertEquals(expected[0].name.middleName, result[0].name.middleName)
        assertEquals(expected[0].address.address1, result[0].address.address1)
        assertEquals(expected[0].address.address2, result[0].address.address2)
        assertEquals(expected[0].address.country, result[0].address.country)
        assertEquals(expected[0].address.stateProvince, result[0].address.stateProvince)
        assertEquals(expected[0].address.cityVillage, result[0].address.cityVillage)
        assertEquals(expected[0].address.postalCode, result[0].address.postalCode)
    }

    @Test
    fun noMatchWithExistingPatients_returnsEmptyList() {
        val givenName = "doesn't exist"
        val middleName = "doesn't exist"
        val familyName = "doesn't exist"
        val address1 = "doesn't exist"
        val address2 = "doesn't exist"
        val cityVillage = "doesn't exist"
        val stateProvince = "doesn't exist"
        val country = "doesn't exist"
        val postalCode = "doesn't exist"
        val gender = "doesn't exist"
        val birthdate = "doesn't exist"

        val personName = PersonName()
        personName.givenName = givenName
        personName.middleName = middleName
        personName.familyName = familyName

        val personAddress = PersonAddress()
        personAddress.address1 = address1
        personAddress.address2 = address2
        personAddress.stateProvince = stateProvince
        personAddress.cityVillage = cityVillage
        personAddress.country = country
        personAddress.postalCode = postalCode

        val id: Long = 1
        val identifiers = PatientIdentifier()
        val attributes = PersonAttribute()
        val cod = Resource()
        val photo: Bitmap? = null

        val patientToCheck = Patient(id, "", mutableListOf(identifiers), mutableListOf(personName), gender, birthdate, true, mutableListOf(personAddress), mutableListOf(attributes), photo, cod, false)
        val result = PatientComparator().findSimilarPatient(patientList, patientToCheck)
        val expected = mutableListOf<Patient>()
        expected.add(patientToCheck)

        assertEquals(0, result.size)
    }
}
