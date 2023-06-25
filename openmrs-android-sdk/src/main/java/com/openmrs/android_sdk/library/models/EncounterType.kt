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

import androidx.room.Entity

/**
 * Encounter type
 *
 * @constructor Create empty Encounter type
 */
@Entity(tableName = "encounterType")
class EncounterType : Resource {

    constructor(display: String?) {
        this.display = display
    }

    constructor(uuid: String?, display: String?, links: List<Link>){
        this.uuid = uuid
        this.display = display
        this.links = links
    }

    companion object {

        const val VITALS = "Vitals"
        const val VISIT_NOTE = "Visit Note"
        const val DISCHARGE = "Discharge"
        const val ADMISSION = "Admission"
        const val ATTACHMENT_UPLOAD = "Attachment Upload"
        const val CHECK_IN = "Check In"
        const val CHECK_OUT = "Check Out"
        const val TRANSFER = "Transfer"
    }
}
