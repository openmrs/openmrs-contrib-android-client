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

import android.content.Intent;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.openmrs.mobile.activities.LoginActivity;
import org.openmrs.mobile.activities.listeners.LoginListener;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;
import org.openmrs.mobile.utilities.ApplicationConstants;
import java.util.HashMap;
import java.util.Map;
import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class AuthorizationManager extends BaseManager {
    private static String sLoginURL;

    public void login(LoginListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        encodeAuthorizationToken(listener.getUsername(), listener.getPassword());
        sLoginURL = listener.getServerURL() + API.REST_ENDPOINT + API.AUTHORISATION_END_POINT;
        mLogger.i(SENDING_REQUEST + sLoginURL);
        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                sLoginURL, null, listener, listener, DO_GZIP_REQUEST) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(ApplicationConstants.AUTHORIZATION_PARAM, mOpenMRS.getAuthorizationToken());
                return params;
            }
        };
        queue.add(jsObjRequest);
    }

    private boolean isUsernameNotEmptyOrNotSameUser(String username) {
        boolean result = false;
        if (!mOpenMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING)
                && !mOpenMRS.getUsername().equals(username)) {
            result = true;
        }
        return result;
    }

    private boolean isServerURLNotEmptyOrNorSameURL(String serverURL) {
        boolean result = false;
        if (!mOpenMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING)
                && !mOpenMRS.getServerUrl().equals(serverURL)) {
            result = true;
        }
        return result;
    }

    public boolean isDBCleaningRequired(String username, String serverURL) {
        boolean result = false;
        if (isUsernameNotEmptyOrNotSameUser(username) || isServerURLNotEmptyOrNorSameURL(serverURL)) {
            result = true;
        }
        return result;
    }

    public boolean isUserLoggedIn() {
        return !ApplicationConstants.EMPTY_STRING.equals(mOpenMRS.getSessionToken());
    }

    public void moveToLoginActivity() {
        Intent intent = new Intent(getCurrentContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getCurrentContext().startActivity(intent);
    }
}
