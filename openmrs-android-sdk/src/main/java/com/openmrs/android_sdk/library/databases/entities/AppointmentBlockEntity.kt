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
