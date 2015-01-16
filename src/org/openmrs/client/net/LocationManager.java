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
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.models.Location;
import org.openmrs.client.models.mappers.LocationMapper;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.net.volley.wrappers.JsonObjectRequestWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class LocationManager extends BaseManager {
    private static final String LOCATION_QUERY = "location?tag=Login%20Location&v=full";

    public LocationManager(Context context) {
        super(context);
    }

    public void getAvailableLocation(final String serverUrl) {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        String locationURL = serverUrl + API.REST_ENDPOINT + LOCATION_QUERY;
        logger.d("Sending request to : " + locationURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                locationURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                try {
                    JSONArray locationResultJSON = response.getJSONArray(RESULTS_KEY);
                    List<Location> locationList = new ArrayList<Location>();
                    if (locationResultJSON.length() > 0) {
                        for (int i = 0; i < locationResultJSON.length(); i++) {
                            locationList.add(LocationMapper.map(locationResultJSON.getJSONObject(i)));
                        }
                    }
                    ((LoginActivity) mContext).initLoginForm(locationList, serverUrl);
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (isUserUnauthorized(error.toString())) {
                            mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_NOT_SUPPORTED_BROADCAST));
                        } else {
                            super.onErrorResponse(error);
                        }
                        ((LoginActivity) mContext).setErrorOccurred(true);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        queue.add(jsObjRequest);
    }
}
