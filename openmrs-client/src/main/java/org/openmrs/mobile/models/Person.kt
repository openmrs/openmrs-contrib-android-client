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
package org.openmrs.mobile.models

import android.graphics.Bitmap
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.openmrs.mobile.models.typeConverters.PersonAddressConverter
import org.openmrs.mobile.models.typeConverters.PersonAttributeConverter
import org.openmrs.mobile.models.typeConverters.PersonNameConverter
import org.openmrs.mobile.utilities.ImageUtils.resizePhoto
import java.io.Serializable
import java.util.*

open class Person : Resource, Serializable {
    /**
     * @return The names
     */
    /**
     * @param names The names
     */
    @TypeConverters(PersonNameConverter::class)
    @SerializedName("names")
    @Expose
    var names: MutableList<PersonName> = ArrayList()

    /**
     * @return The gender
     */
    /**
     * @param gender The gender
     */
    @SerializedName("gender")
    @Expose
    var gender: String? = null

    /**
     * @return The birthdate
     */
    /**
     * @param birthdate The birthdate
     */
    @SerializedName("birthdate")
    @Expose
    var birthdate: String? = null

    /**
     * @return The birthdateEstimated
     */
    /**
     * @param birthdateEstimated The birthdate
     */
    @SerializedName("birthdateEstimated")
    @Expose
    var birthdateEstimated = false

    /**
     * @return The addresses
     */
    /**
     * @param addresses The addresses
     */
    @TypeConverters(PersonAddressConverter::class)
    @SerializedName("addresses")
    @Expose
    var addresses: MutableList<PersonAddress> = ArrayList()

    /**
     * @return The attributes
     */
    /**
     * @param attributes The attributes
     */
    @TypeConverters(PersonAttributeConverter::class)
    @SerializedName("attributes")
    @Expose
    var attributes: List<PersonAttribute> = ArrayList()

    @SerializedName("dead")
    @Expose
    var isDeceased: Boolean? = null

    @SerializedName("causeOfDeath")
    @Expose
    var causeOfDeath: Resource? = null
    var photo: Bitmap? = null

    constructor()
    constructor(names: MutableList<PersonName>, gender: String?, birthdate: String?, birthdateEstimated: Boolean, addresses: MutableList<PersonAddress>, attributes: List<PersonAttribute>,
                photo: Bitmap?, causeOfDeath: Resource?, dead: Boolean) {
        this.names = names
        this.gender = gender
        this.birthdate = birthdate
        this.birthdateEstimated = birthdateEstimated
        this.addresses = addresses
        this.attributes = attributes
        this.photo = photo
        this.causeOfDeath = causeOfDeath
        isDeceased = dead
    }

    val name: PersonName?
        get() = if (names.isNotEmpty()) {
            names[0]
        } else {
            null
        }

    val address: PersonAddress?
        get() = if (addresses.isNotEmpty()) {
            addresses[0]
        } else {
            null
        }

    val resizedPhoto: Bitmap
        get() = resizePhoto(photo!!)

}