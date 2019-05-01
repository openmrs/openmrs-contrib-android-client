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

package org.openmrs.mobile.net;

import android.content.Intent;

import org.openmrs.mobile.activities.login.LoginActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class AuthorizationManager {

    protected OpenMRS mOpenMRS = OpenMRS.getInstance();

    public boolean isUserNameOrServerEmpty() {
        boolean result = false;
        if (mOpenMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING) ||
                (mOpenMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING))) {
            result = true;
        }
        return result;
    }

    public boolean isUserLoggedIn() {
        return !ApplicationConstants.EMPTY_STRING.equals(mOpenMRS.getSessionToken());
    }

    public void moveToLoginActivity() {
        Intent intent = new Intent(mOpenMRS.getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mOpenMRS.getApplicationContext().startActivity(intent);
    }
}
