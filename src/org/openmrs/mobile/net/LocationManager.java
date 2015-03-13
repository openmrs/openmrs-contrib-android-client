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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.openmrs.mobile.activities.listeners.AvailableLocationListener;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;


import java.util.HashMap;
import java.util.Map;

import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class LocationManager extends BaseManager {
    private static final String LOCATION_QUERY = "location?tag=Login%20Location&v=full";

    public void getAvailableLocation(AvailableLocationListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String locationURL = listener.getServerUrl() + API.REST_ENDPOINT + LOCATION_QUERY;
        mLogger.d("Sending request to : " + locationURL);
        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                locationURL, null, listener, listener) {

            /* Override is needed
            * because we put session object into header */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return new HashMap<String, String>();
            }
        };
        queue.add(jsObjRequest);
    }
}
