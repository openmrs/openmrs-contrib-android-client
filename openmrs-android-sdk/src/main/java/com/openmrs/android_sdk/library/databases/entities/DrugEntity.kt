/* The contents of this file are subject to the OpenMRS Public License
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
package com.openmrs.android_sdk.library.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drugs")
class DrugEntity {

    @PrimaryKey
    var uuid: String = ""

    @ColumnInfo(name = "display")
    var display: String = ""

    @ColumnInfo(name = "description")
    var description: String = ""

    @ColumnInfo(name = "combination")
    var combination: Boolean = false

    @ColumnInfo(name = "maximumDailyDose")
    var maximumDailyDose: Int = 0

    @ColumnInfo(name = "minimumDailyDose")
    var minimumDailyDose: Int = 0

    @Embedded(prefix = "concept_")
    var concept: DrugConceptEntity? = null

    @Embedded(prefix = "dosageForm_")
    var dosageForm: DosageFormEntity? = null

    @ColumnInfo(name = "drugReferenceMaps")
    var drugReferenceMaps: List<String>? = null

    @ColumnInfo(name = "ingredients")
    var ingredients: List<String>? = null

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "retired")
    var retired: Boolean = false

    @ColumnInfo(name = "strength")
    var strength: String = ""

    @ColumnInfo(name = "resourceVersion")
    var resourceVersion: String = ""
}