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
    val encounterUuid: String = ""

    @ColumnInfo(name = "type")
    val type: String = ""

    @ColumnInfo(name = "instructions")
    val instructions: String = ""

    @ColumnInfo(name = "frequency")
    val frequency: String = ""

    @ColumnInfo(name = "doseUnits")
    val doseUnits: String = ""

    @ColumnInfo(name = "careSettingName")
    val careSettingName: String = ""

    @ColumnInfo(name = "urgency")
    val urgency: String = ""

    @ColumnInfo(name = "dateStopped")
    val dateStopped: String = ""

    @ColumnInfo(name = "dateActivated")
    val dateActivated: String = ""

    @ColumnInfo(name = "quantity")
    val quantity: String = ""

    @ColumnInfo(name = "drug")
    val drug: String = ""

    @ColumnInfo(name = "dosingInstructions")
    val dosingInstructions: String = ""

    @ColumnInfo(name = "duration")
    val duration: String = ""

    @ColumnInfo(name = "dosingType")
    val dosingType: String = ""

    @ColumnInfo(name = "numberOfRepeats")
    val numberOfRepeats: String = ""

    @ColumnInfo(name = "orderNumber")
    val orderNumber: String = ""

    @ColumnInfo(name = "accessionNumber")
    val accessionNumber: String = ""

    @ColumnInfo(name = "patientUuid")
    val patientUuid: String = ""

    @ColumnInfo(name = "conceptUuid")
    val conceptUuid: String = ""

    @ColumnInfo(name = "action")
    val action: String = ""

    @ColumnInfo(name = "scheduledDate")
    val scheduledDate: String = ""

    @ColumnInfo(name = "autoExpireDate")
    val autoExpireDate: String = ""

    @ColumnInfo(name = "orderer")
    @Embedded(prefix = "orderer_")
    val orderer: OrderResource = OrderResource()

    @ColumnInfo(name = "orderReason")
    val orderReason: String = ""

    @ColumnInfo(name = "orderType")
    @Embedded(prefix = "orderType_")
    val orderType: OrderResource = OrderResource()

    @ColumnInfo(name = "fulfillerStatus")
    val fulfillerStatus: String = ""

    @ColumnInfo(name = "fulfillerComment")
    val fulfillerComment: String = ""

    @ColumnInfo(name = "specimenSource")
    val specimenSource: String = ""
}