package com.openmrs.android_sdk.library.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded

class TimeSlotEntity {

    @ColumnInfo(name = "uuid")
    var uuid: String? = ""

    @ColumnInfo(name = "display")
    var display: String? = ""

    @Embedded(prefix = "appointment_block")
    var appointmentBlock: AppointmentBlockEntity? = null

    @ColumnInfo(name = "startDate")
    var startDate: String? = null

    @ColumnInfo(name = "endDate")
    var endDate: String? = null
}
