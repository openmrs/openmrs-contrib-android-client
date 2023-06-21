package com.openmrs.android_sdk.library.databases.entities

import androidx.room.ColumnInfo

class AppointmentTypeEntity {

    @ColumnInfo(name = "uuid")
    var uuid: String? = ""

    @ColumnInfo(name = "display")
    var display: String? = ""

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "duration")
    var duration: Int? = null

    @ColumnInfo(name = "confidential")
    var confidential: Boolean = false
}
