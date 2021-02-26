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
package org.openmrs.mobile.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import org.openmrs.mobile.models.Resource

@Entity(tableName = "patients")
class PatientEntity : Resource() {
    @ColumnInfo(name = "synced")
    var isSynced = false

    @ColumnInfo(name = "identifier")
    var identifier: String? = null

    @ColumnInfo(name = "givenName")
    var givenName: String? = null

    @ColumnInfo(name = "middleName")
    var middleName: String? = null

    @ColumnInfo(name = "familyName")
    var familyName: String? = null

    @ColumnInfo(name = "gender")
    var gender: String? = null

    @ColumnInfo(name = "birthDate")
    var birthDate: String? = null

    @ColumnInfo(name = "deathDate")
    var deathDate: String? = null

    @ColumnInfo(name = "causeOfDeath")
    var causeOfDeath: String? = null

    @ColumnInfo(name = "age")
    var age: String? = null

    @ColumnInfo(name = "photo")
    var photo: ByteArray? = null

    @ColumnInfo(name = "address1")
    var address_1: String? = null

    @ColumnInfo(name = "address2")
    var address_2: String? = null

    @ColumnInfo(name = "city")
    var city: String? = null

    @ColumnInfo(name = "state")
    var state: String? = null

    @ColumnInfo(name = "country")
    var country: String? = null

    @ColumnInfo(name = "postalCode")
    var postalCode: String? = null

    @ColumnInfo(name = "dead")
    var deceased: String? = null

    @ColumnInfo(name = "encounters")
    var encounters: String? = null
}