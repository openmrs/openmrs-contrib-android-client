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

import android.content.Context;
import android.util.Base64;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.utilities.ApplicationConstants;
import java.io.UnsupportedEncodingException;

public class BaseManager {
    public static final String SENDING_REQUEST = "Sending request to : ";
    public static final String RESULTS_KEY = "results";
    public static final boolean DO_GZIP_REQUEST = false;
    protected OpenMRS mOpenMRS = OpenMRS.getInstance();
    protected OpenMRSLogger mLogger = mOpenMRS.getOpenMRSLogger();
    protected boolean mOnlineMode = OpenMRS.getInstance().getOnlineMode();

    public static String getBaseRestURL() {
        return OpenMRS.getInstance().getServerUrl() + ApplicationConstants.API.REST_ENDPOINT;
    }

    public static String getBaseXFormURL() {
        return OpenMRS.getInstance().getServerUrl() + ApplicationConstants.API.XFORM_ENDPOINT;
    }

    /* It's called everytime to be sure
     * that context is always actual */
    public static Context getCurrentContext()  {
        return OpenMRS.getInstance().getApplicationContext();
    }

    protected static boolean isConnectionTimeout(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.CONNECTION_TIMEOUT);
    }

    public static boolean isUserUnauthorized(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.AUTHORISATION_FAILURE);
    }

    protected static boolean isNoInternetConnection(String errorMessage) {
        return (errorMessage.contains(ApplicationConstants.VolleyErrors.NO_CONNECTION)
                && errorMessage.contains(ApplicationConstants.VolleyErrors.UNKNOWN_HOST));
    }

    protected static boolean isServerUnavailable(String errorMessage) {
        return (errorMessage.contains(ApplicationConstants.VolleyErrors.NO_CONNECTION)
                && errorMessage.contains(ApplicationConstants.VolleyErrors.CONNECT_EXCEPTION));
    }

    protected static boolean isServerError(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.SERVER_ERROR);
    }

    protected static boolean isSocketException(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.SOCKET_EXCEPTION);
    }

    protected static boolean isEOFException(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.EOF_EXCEPTION);
    }

    protected static void encodeAuthorizationToken(String username, String password) {
        String auth = null;
        try {
            auth = "Basic " + Base64.encodeToString(String.format("%s:%s", username, password).getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        }
        OpenMRS.getInstance().setAuthorizationToken(auth);
    }
}
