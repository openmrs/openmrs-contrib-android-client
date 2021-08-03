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


import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Resource
 *
 * <p> More on Resources https://rest.openmrs.org/#resources </p>
 * @constructor Create empty Resource
 */
open class Resource : Serializable {


    @SerializedName("uuid")
    @ColumnInfo(name = "uuid")
    @Expose
    open var uuid: String? = ""


    @ColumnInfo(name = "display")
    @SerializedName("display")
    @Expose
    open var display: String? = ""


    @Ignore
    @SerializedName("links")
    @Expose
    open var links: List<Link> = ArrayList()


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    open var id: Long? = null

    constructor()

    constructor(uuid: String, display: String, links: List<Link>, id: Long) {
        this.uuid = uuid
        this.display = display
        this.links = links
        this.id = id
    }


}
