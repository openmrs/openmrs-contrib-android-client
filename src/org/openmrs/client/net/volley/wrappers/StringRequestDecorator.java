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

package org.openmrs.client.net.volley.wrappers;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.utilities.ApplicationConstants;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class StringRequestDecorator extends StringRequest {
    private int mStatusCode;

    public StringRequestDecorator(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public StringRequestDecorator(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        mStatusCode = response.statusCode;
        String responseAsString = null;
        try {
            responseAsString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        }
        if (null != responseAsString && !responseAsString .isEmpty()) {
            if (responseAsString.contains("<!DOCTYPE html>")) {
                return Response.error(new AuthFailureError(ApplicationConstants.VolleyErrors.AUTHORISATION_FAILURE));
            }
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<String, String>();

        StringBuilder builder = new StringBuilder();
        builder.append(ApplicationConstants.JSESSIONID_PARAM);
        builder.append("=");
        builder.append(OpenMRS.getInstance().getSessionToken());
        params.put(ApplicationConstants.COOKIE_PARAM, builder.toString());

        return params;
    }

    public int getStatusCode() {
        return mStatusCode;
    }
}
