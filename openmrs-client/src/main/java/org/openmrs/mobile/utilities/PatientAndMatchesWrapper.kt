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
package org.openmrs.mobile.utilities

import com.openmrs.android_sdk.utilities.PatientAndMatchingPatients
import java.io.Serializable
import java.util.*

class PatientAndMatchesWrapper : Serializable {
    var matchingPatients: Queue<PatientAndMatchingPatients>

    constructor() {
        matchingPatients = ArrayDeque()
    }

    constructor(matchingPatients: Queue<PatientAndMatchingPatients>) {
        this.matchingPatients = matchingPatients
    }

    fun addToList(element: PatientAndMatchingPatients) {
        matchingPatients.add(element)
    }

    fun remove(element: PatientAndMatchingPatients) {
        matchingPatients.remove(element)
    }

}