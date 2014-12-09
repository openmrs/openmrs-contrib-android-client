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

package org.openmrs.client.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.net.volley.wrappers.JsonObjectRequestWrapper;
import org.openmrs.client.utilities.ApplicationConstants;

import java.util.HashMap;
import java.util.Map;

import static org.openmrs.client.utilities.ApplicationConstants.API;


public class UserManager extends BaseManager {

    public UserManager(Context context) {
        super(context);
    }

    public void getUserInformation(String username) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.USER_QUERY + username;

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());
                try {
                    JSONArray resultJSON = response.getJSONArray(RESULTS_KEY);
                    if (resultJSON.length() > 0) {
                        for (int i = 0; i < resultJSON.length(); i++) {
                            getFullInformation(resultJSON.getJSONObject(i).getString(UUID_KEY));
                        }
                    }
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext));
        queue.add(jsObjRequest);
    }

    private void getFullInformation(String userUUID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + ApplicationConstants.API.REST_ENDPOINT + API.USER_DETAILS + userUUID;

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());
                try {
                    Map<String, String> userInfo = new HashMap<String, String>();
                    userInfo.put(ApplicationConstants.UserKeys.USER_PERSON_NAME, response.getJSONObject("person").getString("display"));
                    userInfo.put(ApplicationConstants.UserKeys.USER_UUID, response.getString("uuid"));
                    OpenMRS.getInstance().setCurrentUserInformation(userInfo);
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext));
        queue.add(jsObjRequest);
    }
}
