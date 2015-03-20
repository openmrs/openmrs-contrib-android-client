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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.openmrs.mobile.activities.listeners.FullInformationListener;
import org.openmrs.mobile.activities.listeners.UserInformationListener;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;

import java.io.File;
import static org.openmrs.mobile.utilities.ApplicationConstants.API;


public class UserManager extends BaseManager {
    private static final String FULL_INFORMATION_BASE_URL = getBaseRestURL() + API.USER_DETAILS + File.separator;
    private static final String USER_INFORMATION_BASE_URL = getBaseRestURL() + API.USER_QUERY;

    public void getFullInformation(FullInformationListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String url = FULL_INFORMATION_BASE_URL + listener.getUserUUID();
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        queue.add(jsObjRequest);
    }

    public void getUserInformation(UserInformationListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String url = USER_INFORMATION_BASE_URL + listener.getUsername();
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        queue.add(jsObjRequest);
    }
}
