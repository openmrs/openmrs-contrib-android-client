/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.models

import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.openmrs.mobile.utilities.StringUtils.isBlank
import org.openmrs.mobile.utilities.StringUtils.notNull
import java.io.Serializable
import java.util.*

class Patient : Person, Serializable {
    override var id: Long? = null
    var encounters = ""

    /**
     * @return The identifiers
     */
    /**
     * @param identifiers The identifiers
     */
    @SerializedName("identifiers")
    @Expose
    var identifiers: MutableList<PatientIdentifier>? = ArrayList()

    constructor()
    constructor(id: Long?, encounters: String, identifiers: MutableList<PatientIdentifier>) {
        this.id = id
        this.encounters = encounters
        this.identifiers = identifiers
    }

    /*Constructor to initialize values of current class as well as parent class*/
    constructor(id: Long?, encounters: String, identifiers: List<PatientIdentifier>,
                names: MutableList<PersonName>, gender: String?, birthdate: String?, birthdateEstimated: Boolean, addresses: MutableList<PersonAddress>, attributes: MutableList<PersonAttribute>,
                photo: Bitmap?, causeOfDeath: Resource?, dead: Boolean) : super(names, gender, birthdate, birthdateEstimated, addresses, attributes, photo, causeOfDeath, dead) {
        this.id = id
        this.encounters = encounters
        this.identifiers = identifiers.toMutableList()
    }

    @get:SerializedName("person")
    val person: Person
        get() = Person(names, gender, birthdate, birthdateEstimated, addresses, attributes, photo, causeOfDeath, isDeceased!!)

    private val updatedPerson: PersonUpdate
        get() = PersonUpdate(names, gender, birthdate, birthdateEstimated, addresses, attributes, photo, causeOfDeath!!.uuid, isDeceased!!)

    val patientDto: PatientDto
        get() {
            val patientDto = PatientDto()
            patientDto.person = person
            patientDto.setIdentifiers(identifiers!!)
            return patientDto
        }

    val updatedPatientDto: PatientDtoUpdate
        get() {
            val patientDtoUpdate = PatientDtoUpdate()
            patientDtoUpdate.setIdentifiers(identifiers)
            patientDtoUpdate.person = updatedPerson
            return patientDtoUpdate
        }

    val identifier: PatientIdentifier?
        get() = if (identifiers!!.isNotEmpty()) {
            identifiers!![0]
        } else {
            null
        }

    //Keeping it this way until the synced flag can be made to work
    val isSynced: Boolean
        get() = !isBlank(uuid)
    //Keeping it this way until the synced flag can be made to work

    fun addEncounters(encid: Long) {
        encounters += "$encid,"
    }

    fun toMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = HashMap()
        puToMapIfNotNull(map, "givenname", name!!.givenName)
        puToMapIfNotNull(map, "middlename", name!!.middleName)
        puToMapIfNotNull(map, "familyname", name!!.familyName)
        puToMapIfNotNull(map, "gender", gender)
        puToMapIfNotNull(map, "birthdate", birthdate)
        puToMapIfNotNull(map, "address1", address!!.address1)
        puToMapIfNotNull(map, "address2", address!!.address2)
        puToMapIfNotNull(map, "city", address!!.cityVillage)
        puToMapIfNotNull(map, "state", address!!.stateProvince)
        puToMapIfNotNull(map, "postalcode", address!!.postalCode)
        puToMapIfNotNull(map, "country", address!!.country)
        return map
    }

    private fun puToMapIfNotNull(map: MutableMap<String, String?>, key: String, value: String?) {
        if (notNull(value)) {
            map[key] = value
        }
    }
}