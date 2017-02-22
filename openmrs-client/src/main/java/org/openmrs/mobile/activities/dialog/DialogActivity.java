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

package org.openmrs.mobile.activities.dialog;

import android.content.Intent;
import android.os.Bundle;

import org.openmrs.mobile.activities.ACBaseActivity;

import static org.openmrs.mobile.utilities.ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST;

public class DialogActivity extends ACBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (ACTION_AUTH_FAILED_BROADCAST.equals(intent.getAction())) {
            showAuthenticationFailedDialog();
        }
    }

}
