/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models

import androidx.room.ColumnInfo
import androidx.room.Entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

@Entity(tableName = "provider_table")
class Provider : Resource() {

    @ColumnInfo(name = "id")
    override var id: Long? = null

    @ColumnInfo(name = "person")
    @SerializedName("person")
    @Expose
    var person: Person? = null

    @ColumnInfo(name = "identifier")
    @SerializedName("identifier")
    @Expose
    var identifier: String? = null

    @ColumnInfo(name = "attributes")
    @SerializedName("attributes")
    @Expose
    var attributes: List<Any> = ArrayList()

    @ColumnInfo(name = "retired")
    @SerializedName("retired")
    @Expose
    var retired: Boolean? = null

    @ColumnInfo(name = "resourceVersion")
    @SerializedName("resourceVersion")
    @Expose
    var resourceVersion: String? = null

}
