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

package org.openmrs.mobile.listeners.location;

import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.activities.LoginActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.mappers.LocationMapper;
import org.openmrs.mobile.net.BaseManager;
import org.openmrs.mobile.net.GeneralErrorListener;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

public final class AvailableLocationListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final LoginActivity mCallerActivity;
    private final String mServerUrl;

    public AvailableLocationListener(String serverUrl, LoginActivity callerActivity) {
        mServerUrl = serverUrl;
        mCallerActivity = callerActivity;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (BaseManager.isUserUnauthorized(error.toString())) {
            mCallerActivity.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_NOT_SUPPORTED_BROADCAST));
        } else {
            super.onErrorResponse(error);
        }
        mCallerActivity.setErrorOccurred(true);
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());

        try {
            JSONArray locationResultJSON = response.getJSONArray(BaseManager.RESULTS_KEY);
            List<Location> locationList = new ArrayList<Location>();
            if (locationResultJSON.length() > 0) {
                for (int i = 0; i < locationResultJSON.length(); i++) {
                    locationList.add(LocationMapper.map(locationResultJSON.getJSONObject(i)));
                }
            }
            mCallerActivity.initLoginForm(locationList, mServerUrl);
        } catch (JSONException e) {
            mLogger.d(e.toString());
        }
    }

    public String getServerUrl() {
        return mServerUrl;
    }
}
