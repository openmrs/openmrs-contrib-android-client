/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.models

import com.google.gson.annotations.Expose

class Location : Resource {

    @Expose
    override var id: Long? = null

    @Expose
    var name: String? = null

    @Expose
    var parentLocationUuid: String? = null

    @Expose
    var description: String? = null

    @Expose
    var address2: String? = null

    @Expose
    var address1: String? = null

    @Expose
    var cityVillage: String? = null

    @Expose
    var stateProvince: String? = null

    @Expose
    var country: String? = null

    @Expose
    var postalCode: String? = null


    constructor()

    constructor(display: String) {
        this.display = display
    }

    constructor(id: Long?, name: String, parentLocationUuid: String, description: String, address2: String, address1: String, cityVillage: String, stateProvince: String, country: String, postalCode: String) {
        this.id = id
        this.name = name
        this.parentLocationUuid = parentLocationUuid
        this.description = description
        this.address2 = address2
        this.address1 = address1
        this.cityVillage = cityVillage
        this.stateProvince = stateProvince
        this.country = country
        this.postalCode = postalCode
    }
}
