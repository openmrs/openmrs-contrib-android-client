/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.openmrs.mobile.R
import org.openmrs.mobile.api.PatientService
import org.openmrs.mobile.utilities.ToastUtil.notify

class SyncStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        notify(context.getString(R.string.patent_and_form_data_sync_resumed))
        val i = Intent(context, PatientService::class.java)
        context.startService(i)
        val i1 = Intent(context, EncounterService::class.java)
        context.startService(i1)
    }
}