/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package com.openmrs.android_sdk.library.databases.entities

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Embedded

@Entity(tableName = "appointments")
class AppointmentEntity{
    
    @ColumnInfo(name = "uuid")
    var uuid: String? = ""
    
    @ColumnInfo(name = "display")
    var display: String? = ""
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @Embedded(prefix = "timeslot_")
    var timeSlot: TimeSlotEntity? = null

    @Embedded(prefix = "visit_")
    var visit: AppointmentVisitEntity? = null

    @Embedded(prefix = "patient_")
    var patient: AppointmentPatientEntity? = null

    @ColumnInfo(name = "status")
    var status: String? = null

    @ColumnInfo(name = "reason")
    var reason: String? = null

    @ColumnInfo(name = "cancelReason")
    var cancelReason: String? = null

    @Embedded(prefix = "type_")
    var appointmentType: AppointmentTypeEntity? = null
}