package com.openmrs.android_sdk.library.models

import com.google.gson.annotations.Expose
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import java.io.Serializable

class ProviderSchedule(

    @Expose
    private var startDate: String? = null,

    @Expose
    private var endDate: String? = null,

    @Expose
    private var startTime: String? = null,

    @Expose
    private var endTime: String? = null,

    @Expose
    private var provider: Provider? = null,

    @Expose
    private var location: LocationEntity? = null,

    @Expose
    private var types: Set<AppointmentType>? = null

): Resource(), Serializable