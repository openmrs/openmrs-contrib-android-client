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
import android.content.Intent;
import android.util.Base64;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.io.UnsupportedEncodingException;

public class BaseManager {
    public static final String RESULTS_KEY = "results";
    protected static final String UUID_KEY = "uuid";

    protected Context mContext;
    protected OpenMRS mOpenMRS = OpenMRS.getInstance();
    protected OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();

    public BaseManager(Context context) {
        this.mContext = context;
    }

    protected boolean isConnectionTimeout(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.CONNECTION_TIMEOUT);
    }

    protected boolean isUserUnauthorized(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.AUTHORISATION_FAILURE);
    }

    protected boolean isNoInternetConnection(String errorMessage) {
        return (errorMessage.contains(ApplicationConstants.VolleyErrors.NO_CONNECTION)
                && errorMessage.contains(ApplicationConstants.VolleyErrors.UNKNOWN_HOST));
    }

    protected boolean isServerUnavailable(String errorMessage) {
        return (errorMessage.contains(ApplicationConstants.VolleyErrors.NO_CONNECTION)
                && errorMessage.contains(ApplicationConstants.VolleyErrors.CONNECT_EXCEPTION));
    }

    protected boolean isServerError(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.SERVER_ERROR);
    }

    protected boolean isSocketException(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.SOCKET_EXCEPTION);
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

    private abstract class GeneralErrorListener implements Response.ErrorListener {
        private Context mContext;

        protected GeneralErrorListener(Context context) {
            this.mContext = context;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (isConnectionTimeout(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST));
            } else if (isUserUnauthorized(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_UNAUTHORIZED_BROADCAST));
            } else if (isServerUnavailable(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_UNAVAILABLE_BROADCAST));
            } else if (isNoInternetConnection(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST));
            } else if (isServerError(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_ERROR_BROADCAST));
            } else if (isSocketException(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SOCKET_EXCEPTION_BROADCAST));

            } else {
                ToastUtil.showShortToast(mContext, ToastUtil.ToastType.ERROR, error.toString());
                logger.e(error.toString());
            }
        }
    }

    public class GeneralErrorListenerImpl extends GeneralErrorListener {

        public GeneralErrorListenerImpl(Context context) {
            super(context);
        }
    }
}
