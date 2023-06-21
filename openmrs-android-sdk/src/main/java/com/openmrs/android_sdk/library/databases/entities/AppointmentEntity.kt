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

    @Embedded(prefix = "patient-")
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