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
import com.google.gson.annotations.SerializedName

/**
 * Concept answers
 *
 * <p> More on concepts https://rest.openmrs.org/#concepts </p>
 * @constructor Create empty Concept answers
 */
class ConceptAnswers : Resource() {

    override var id: Long? = null

    @SerializedName("conceptClass")
    @Expose
    var conceptClass: ConceptClass? = null

    @SerializedName("answers")
    @Expose
    var answers: List<Resource> = ArrayList()
}
