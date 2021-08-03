/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.R;
import com.openmrs.android_sdk.library.api.services.EncounterService;
import org.openmrs.mobile.services.PatientService;

public class SyncStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ToastUtil.notify(context.getString(R.string.patent_and_form_data_sync_resumed));
        Intent i = new Intent(context, PatientService.class);
        context.startService(i);
        Intent i1 = new Intent(context, EncounterService.class);
        context.startService(i1);
    }
}
