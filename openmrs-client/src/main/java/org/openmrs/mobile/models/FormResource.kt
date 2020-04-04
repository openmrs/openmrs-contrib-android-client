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

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import org.openmrs.mobile.utilities.ActiveAndroid.Model
import org.openmrs.mobile.utilities.ActiveAndroid.annotation.Column
import org.openmrs.mobile.utilities.ActiveAndroid.annotation.Table
import java.io.Serializable

@Table(name = "formresource")
class FormResource : Model(), Serializable {

    @Column(name = "name")
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("resources")
    @Expose
    var resources: List<FormResource> = ArrayList()

    @Column(name = "resources")
    private var resourcelist: String? = null

    @Column(name = "valueReference")
    @SerializedName("valueReference")
    @Expose
    var valueReference: String? = null

    @Column(name = "uuid")
    @SerializedName("uuid")
    @Expose
    var uuid: String? = null

    @Column(name = "display")
    @SerializedName("display")
    @Expose
    var display: String? = null

    @Column(name = "links")
    @SerializedName("links")
    @Expose
    var links: List<Link> = ArrayList()

    private val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    private val formResourceListType = object : TypeToken<List<FormResource>>() {

    }.type

    val resourceList: List<FormResource>?
        get() = gson.fromJson<List<FormResource>>(this.resourcelist, formResourceListType)

    fun setResourcelist() {
        this.resourcelist = gson.toJson(resources, formResourceListType)
    }

}
