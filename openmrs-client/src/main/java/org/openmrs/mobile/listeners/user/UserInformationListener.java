/**
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

package org.openmrs.mobile.listeners.user;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.net.BaseManager;
import org.openmrs.mobile.net.GeneralErrorListener;
import org.openmrs.mobile.net.UserManager;
import org.openmrs.mobile.net.helpers.UserHelper;

public final class UserInformationListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private static final String UUID_KEY = "uuid";
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final UserManager mCallerManager;
    private final String mUsername;

    public UserInformationListener(String username, UserManager callerManager) {
        mUsername = username;
        mCallerManager = callerManager;
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());
        try {
            JSONArray resultJSON = response.getJSONArray(BaseManager.RESULTS_KEY);
            if (resultJSON.length() > 0) {
                for (int i = 0; i < resultJSON.length(); i++) {
                    mCallerManager.getFullInformation(
                            UserHelper.createFullInformationListener(resultJSON.getJSONObject(i).getString(UUID_KEY)));
                }
            }
        } catch (JSONException e) {
            mLogger.d(e.toString());
        }
    }

    public String getUsername() {
        return mUsername;
    }
}
