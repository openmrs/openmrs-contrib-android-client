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

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import org.openmrs.mobile.models.Resource

@Entity(tableName = "visits")
class VisitEntity : Resource() {
    @NonNull
    @ColumnInfo(name = "patient_id")
    var patientKeyID: Long = 0

    @ColumnInfo(name = "visit_type")
    var visitType: String? = null

    @ColumnInfo(name = "visit_place")
    var visitPlace: String? = null

    @ColumnInfo(name = "start_date")
    lateinit var startDate: String

    @ColumnInfo(name = "stop_date")
    var stopDate: String? = null

    fun isStartDate(): String {
        return startDate
    }

}
