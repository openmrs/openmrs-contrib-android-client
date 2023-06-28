/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package com.openmrs.android_sdk.library.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class Appointment(

    @Expose
    var timeSlot: TimeSlot? = null,

    @Expose
    var visit: Visit? = null,

    @Expose
    var patient: Patient? = null,

    @Expose
    var status: String? = null,

    @Expose
    var reason: String? = null,

    @Expose
    var cancelReason: String? = null,

    @Expose
    var appointmentType: AppointmentType? = null

): Resource(), Serializable {

    enum class AppointmentStatusType {
        SCHEDULED, ACTIVE, CANCELLED, MISSED, COMPLETED
    }

    enum class AppointmentStatus(
        val status: String,
        val type: AppointmentStatusType
    ) {
        SCHEDULED("Scheduled", AppointmentStatusType.SCHEDULED),
        RESCHEDULED("Rescheduled", AppointmentStatusType.SCHEDULED),
        WALKIN("Walk-In", AppointmentStatusType.ACTIVE),
        WAITING("Waiting", AppointmentStatusType.ACTIVE),
        INCONSULTATION("In-Consultation", AppointmentStatusType.ACTIVE),
        CANCELLED("Cancelled", AppointmentStatusType.CANCELLED),
        CANCELLED_AND_NEEDS_RESCHEDULE("Cancelled and Needs Reschedule", AppointmentStatusType.CANCELLED),
        MISSED("Missed", AppointmentStatusType.MISSED),
        COMPLETED("Completed", AppointmentStatusType.COMPLETED);
    }
}