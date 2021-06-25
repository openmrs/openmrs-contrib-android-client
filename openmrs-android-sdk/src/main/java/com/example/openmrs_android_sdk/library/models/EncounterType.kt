/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package com.example.openmrs_android_sdk.library.models

import androidx.room.Entity

@Entity(tableName = "encounterType")
class EncounterType : Resource {

    constructor(display: String?) {
        this.display = display
    }

    companion object {

        const val VITALS = "Vitals"
        const val VISIT_NOTE = "Visit Note"
        const val DISCHARGE = "Discharge"
        const val ADMISSION = "Admission"
    }
}
