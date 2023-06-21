package com.openmrs.android_sdk.library.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded

class AppointmentBlockEntity {

    @ColumnInfo(name = "uuid")
    var uuid: String? = ""

    @ColumnInfo(name = "display")
    var display: String? = ""

    @ColumnInfo(name = "startDate")
    var startDate: String? = null

    @ColumnInfo(name = "endDate")
    var endDate: String? = null

    @Embedded(prefix = "provider_")
    var provider: AppointmentProviderEntity? = null

    @Embedded(prefix = "location_")
    var location: AppointmentLocationEntity? = null

    @ColumnInfo(name = "types")
    var types: List<String>? = null
}
