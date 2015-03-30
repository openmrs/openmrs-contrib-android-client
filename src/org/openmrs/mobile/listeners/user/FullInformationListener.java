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

package org.openmrs.mobile.listeners.user;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.net.GeneralErrorListener;
import org.openmrs.mobile.utilities.ApplicationConstants;
import java.util.HashMap;
import java.util.Map;

public final class FullInformationListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private String mUserUUID;

    public FullInformationListener(String userUUID) {
        mUserUUID = userUUID;
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());
        try {
            Map<String, String> userInfo = new HashMap<String, String>();
            userInfo.put(ApplicationConstants.UserKeys.USER_PERSON_NAME, response.getJSONObject("person").getString("display"));
            userInfo.put(ApplicationConstants.UserKeys.USER_UUID, response.getString("uuid"));
            OpenMRS.getInstance().setCurrentUserInformation(userInfo);
        } catch (JSONException e) {
            mLogger.d(e.toString());
        }
    }

    public String getUserUUID() {
        return mUserUUID;
    }
}
