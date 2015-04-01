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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

public class GeneralErrorListener implements Response.ErrorListener {

    protected GeneralErrorListener() {
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        /* Work always on real context */
        Context mContext = OpenMRS.getInstance().getApplicationContext();

        if (BaseManager.isConnectionTimeout(error.toString())) {
            mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST));
        } else if (BaseManager.isUserUnauthorized(error.toString())) {
            mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_UNAUTHORIZED_BROADCAST));
        } else if (BaseManager.isServerUnavailable(error.toString())) {
            mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_UNAVAILABLE_BROADCAST));
        } else if (BaseManager.isNoInternetConnection(error.toString())) {
            mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST));
        } else if (BaseManager.isServerError(error.toString())) {
            mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_ERROR_BROADCAST));
        } else if (BaseManager.isSocketException(error.toString())) {
            mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SOCKET_EXCEPTION_BROADCAST));
        } else if (BaseManager.isEOFException(error.toString())) {
            mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_ERROR_BROADCAST));

        } else {
            ToastUtil.showShortToast(mContext, ToastUtil.ToastType.ERROR, error.toString());
            OpenMRS.getInstance().getOpenMRSLogger().e(error.toString());
        }
    }
}
