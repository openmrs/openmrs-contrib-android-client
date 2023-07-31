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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openmrs.android_sdk.library.models.OrderResource

@Entity(tableName = "orders")
class OrderEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "uuid")
    var uuid: String? = ""

    @ColumnInfo(name = "display")
    var display: String? = ""

    @ColumnInfo(name = "encounterUuid")
    var encounterUuid: String = ""

    @ColumnInfo(name = "type")
    var type: String = ""

    @ColumnInfo(name = "instructions")
    var instructions: String = ""

    @ColumnInfo(name = "frequency")
    var frequency: String = ""

    @ColumnInfo(name = "doseUnits")
    var doseUnits: String = ""

    @ColumnInfo(name = "careSettingName")
    var careSettingName: String = ""

    @ColumnInfo(name = "urgency")
    var urgency: String = ""

    @ColumnInfo(name = "dateStopped")
    var dateStopped: String = ""

    @ColumnInfo(name = "dateActivated")
    var dateActivated: String = ""

    @ColumnInfo(name = "quantity")
    var quantity: String = ""

    @ColumnInfo(name = "drug")
    var drug: String = ""

    @ColumnInfo(name = "dosingInstructions")
    var dosingInstructions: String = ""

    @ColumnInfo(name = "duration")
    var duration: String = ""

    @ColumnInfo(name = "dosingType")
    var dosingType: String = ""

    @ColumnInfo(name = "numberOfRepeats")
    var numberOfRepeats: String = ""

    @ColumnInfo(name = "orderNumber")
    var orderNumber: String = ""

    @ColumnInfo(name = "accessionNumber")
    var accessionNumber: String = ""

    @ColumnInfo(name = "patientUuid")
    var patientUuid: String = ""

    @ColumnInfo(name = "conceptUuid")
    var conceptUuid: String = ""

    @ColumnInfo(name = "action")
    var action: String = ""

    @ColumnInfo(name = "scheduledDate")
    var scheduledDate: String = ""

    @ColumnInfo(name = "autoExpireDate")
    var autoExpireDate: String = ""

    @Embedded(prefix = "orderer_")
    var orderer: OrderResource = OrderResource()

    @ColumnInfo(name = "orderReason")
    var orderReason: String = ""

    @Embedded(prefix = "orderType_")
    var orderType: OrderResource = OrderResource()

    @ColumnInfo(name = "fulfillerStatus")
    var fulfillerStatus: String = ""

    @ColumnInfo(name = "fulfillerComment")
    var fulfillerComment: String = ""

    @ColumnInfo(name = "specimenSource")
    var specimenSource: String = ""
}