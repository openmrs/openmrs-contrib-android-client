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

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.openmrs.mobile.utilities.ActiveAndroid.Model
import org.openmrs.mobile.utilities.ActiveAndroid.annotation.Column
import org.openmrs.mobile.utilities.ActiveAndroid.annotation.Table
import java.io.Serializable

@Table(name = "encountertype")
class EncounterType : Model, Serializable {

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

    constructor()

    constructor(display: String) {
        this.display = display
    }

    companion object {

        const val VITALS = "Vitals"
        const val VISIT_NOTE = "Visit Note"
        const val DISCHARGE = "Discharge"
        const val ADMISSION = "Admission"
    }


}
