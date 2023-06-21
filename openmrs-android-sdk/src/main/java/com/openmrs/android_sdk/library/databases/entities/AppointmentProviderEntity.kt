package com.openmrs.android_sdk.library.databases.entities

import androidx.room.ColumnInfo

class AppointmentProviderEntity {
    @ColumnInfo(name = "uuid")
    var uuid: String? = ""

    @ColumnInfo(name = "display")
    var display: String? = ""
}
