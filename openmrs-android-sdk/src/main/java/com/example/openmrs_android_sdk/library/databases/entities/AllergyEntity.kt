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

package com.example.openmrs_android_sdk.library.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.openmrs_android_sdk.library.models.AllergyReaction
import com.example.openmrs_android_sdk.library.models.typeConverters.AllergyReactionConverter

@Entity(tableName = "allergy")
class AllergyEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    @ColumnInfo(name = "uuid")
    var uuid: String? = null

    @ColumnInfo(name = "patientId")
    var patientId: String? = null

    @ColumnInfo(name = "comment")
    var comment: String? = null

    @ColumnInfo(name = "severity_display")
    var severityDisplay: String? = null

    @ColumnInfo(name = "severity_uuid")
    var severityUUID: String? = null

    @ColumnInfo(name = "allergen_display")
    var allergenDisplay: String? = null

    @ColumnInfo(name = "allergen_uuid")
    var allergenUUID: String? = null

    @ColumnInfo(name = "allergen_type")
    var allergenType: String? = null

    @TypeConverters(AllergyReactionConverter::class)
    @ColumnInfo(name = "allergy_reactions")
    var allergyReactions: List<AllergyReaction>? = null
}