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

package org.openmrs.mobile.listeners.authorization;

import android.content.Intent;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.openmrs.provider.OpenMRSFormsProviderAPI;
import org.odk.collect.android.openmrs.provider.OpenMRSInstanceProviderAPI;
import org.openmrs.mobile.activities.LoginActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.bundle.AuthorizationManagerBundle;
import org.openmrs.mobile.databases.OpenMRSSQLiteOpenHelper;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.net.FormsManager;
import org.openmrs.mobile.net.GeneralErrorListener;
import org.openmrs.mobile.net.UserManager;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.FormsHelper;
import org.openmrs.mobile.net.helpers.UserHelper;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.utilities.ApplicationConstants;

public final class LoginListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private static final String SESSION_ID_KEY = "sessionId";
    private static final String AUTHENTICATION_KEY = "authenticated";
    private final OpenMRS mOpenMRS = OpenMRS.getInstance();
    private final OpenMRSLogger mLogger = mOpenMRS.getOpenMRSLogger();
    private final AuthorizationManager mCallerManager;
    private final LoginActivity mCallerActivity;
    private final String mUsername;
    private final String mPassword;
    private final String mServerURL;

    public LoginListener(AuthorizationManagerBundle bundle, LoginActivity callerActivity) {
        mUsername = bundle.getUsername();
        mPassword = bundle.getPassword();
        mServerURL = bundle.getURL();
        mCallerActivity = callerActivity;
        mCallerManager = mCallerActivity.getAuthorizationManager();
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());
        try {
            String sessionToken = response.getString(SESSION_ID_KEY);
            Boolean isAuthenticated = Boolean.parseBoolean(response.getString(AUTHENTICATION_KEY));

            if (isAuthenticated) {
                if (mCallerManager.isDBCleaningRequired(mUsername, mServerURL)) {
                    mOpenMRS.deleteDatabase(OpenMRSSQLiteOpenHelper.DATABASE_NAME);
                    OpenMRS.getInstance()
                            .getContentResolver()
                            .delete(OpenMRSFormsProviderAPI.FormsColumns.CONTENT_URI, null, null);
                    OpenMRS.getInstance()
                            .getContentResolver()
                            .delete(OpenMRSInstanceProviderAPI.InstanceColumns.CONTENT_URI, null, null);

                    setData(sessionToken);
                } else if (mCallerManager.isUserNameOrServerEmpty()) {
                    setData(sessionToken);
                } else {
                    mOpenMRS.setSessionToken(sessionToken);
                }
                new VisitsManager().getVisitType(
                        VisitsHelper.createVisitTypeListener());
                UserManager userManager = new UserManager();
                userManager.getUserInformation(
                        UserHelper.createUserInformationListener(mUsername, userManager));
                mCallerActivity.saveLocationsToDatabase();
                mCallerActivity.finish();
            } else {
                mCallerActivity.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST));
            }
        } catch (JSONException e) {
            mLogger.d(e.toString());
        }
    }

    private void setData(String sessionToken) {
        mOpenMRS.setServerUrl(mServerURL);
        mOpenMRS.setSessionToken(sessionToken);
        mOpenMRS.setUsername(mUsername);

        FormsManager formsManager = new FormsManager();
        formsManager.getAvailableFormsList(
                FormsHelper.createAvailableFormsListListener(formsManager));
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getServerURL() {
        return mServerURL;
    }
}
