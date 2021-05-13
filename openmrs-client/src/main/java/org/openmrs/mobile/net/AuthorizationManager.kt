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

package org.openmrs.mobile.net

import android.content.Intent

import org.openmrs.mobile.activities.login.LoginActivity
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.utilities.ApplicationConstants

open class AuthorizationManager {
    protected val  mOpenMRS: OpenMRS = OpenMRS.getInstance()

    fun  isUserNameOrServerEmpty(): Boolean {
        var result = false
        if (mOpenMRS.username.equals(ApplicationConstants.EMPTY_STRING) ||
            (mOpenMRS.serverUrl.equals(ApplicationConstants.EMPTY_STRING))) {
            result = true
        }
        return result
    }

    fun isUserLoggedIn(): Boolean {
        return ApplicationConstants.EMPTY_STRING != mOpenMRS.sessionToken
    }

    fun moveToLoginActivity() {
        val intent = Intent(mOpenMRS.applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        mOpenMRS.applicationContext.startActivity(intent)
    }
}
