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
import org.openmrs.mobile.utilities.ApplicationConstants;
import java.io.File;
import static org.openmrs.mobile.utilities.ApplicationConstants.API;


public class UserManager extends BaseManager {

    public void getUserInformation(UserInformationListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String visitURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.USER_QUERY + listener.getUsername();
        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, listener, listener);
        queue.add(jsObjRequest);
    }

    public void getFullInformation(FullInformationListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String visitURL = mOpenMRS.getServerUrl() + ApplicationConstants.API.REST_ENDPOINT + API.USER_DETAILS + File.separator + listener.getUserUUID();
        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, listener, listener);
        queue.add(jsObjRequest);
    }
}
