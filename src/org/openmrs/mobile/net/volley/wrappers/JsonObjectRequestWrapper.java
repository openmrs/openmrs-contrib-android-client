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

package org.openmrs.mobile.net.volley.wrappers;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.net.BaseManager;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @see com.android.volley.toolbox.JsonObjectRequest
 * Wrapper class for JsonObjectRequest
 * getHeaders method conatins Authorization Token
 */
public class JsonObjectRequestWrapper extends JsonObjectRequest {
    /**
     * mSetToEncode is responsible only for
     * sending GZIP'ed POST requests,
     * not reading responses!
     */
    private final boolean mSetToEncode;
    private int mMethod;
    private final String mUrl;
    private final JSONObject mJsonRequest;
    private final Response.Listener<JSONObject> mListener;
    private final Response.ErrorListener mErrorListener;
    private final OpenMRS mOpenMRS = OpenMRS.getInstance();
    private final OpenMRSLogger mLogger = mOpenMRS.getOpenMRSLogger();

    public JsonObjectRequestWrapper(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, boolean setToEncode) {
        super(method, url, jsonRequest, listener, errorListener);
        mSetToEncode = setToEncode;
        mMethod = method;
        mUrl = url;
        mJsonRequest = jsonRequest;
        mListener = listener;
        mErrorListener = errorListener;
        setSocketTimeout();
    }

    public JsonObjectRequestWrapper(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, boolean setToEncode) {
        super(url, jsonRequest, listener, errorListener);
        mSetToEncode = setToEncode;
        mUrl = url;
        mJsonRequest = jsonRequest;
        mListener = listener;
        mErrorListener = errorListener;
        setSocketTimeout();
    }

    private void setSocketTimeout() {
        int socketTimeout = ApplicationConstants.API.REQUEST_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.setRetryPolicy(policy);
        setShouldCache(false);
    }

    /**
     * If it's set to compress POST request
     * into GZIP, and server will not be able
     * to read it, RETRY to send it as raw file.
     */
    @Override
    public void deliverError(VolleyError error) {
        if (mSetToEncode) {
            mLogger.e("Server cannot read GZIP file! Retrying to send raw file.");
            mLogger.i(BaseManager.SENDING_REQUEST + mUrl);
            JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(mMethod, mUrl, mJsonRequest, mListener, mErrorListener, false);
            mOpenMRS.addToRequestQueue(jsObjRequest);
            return;
        }
        super.deliverError(error);
    }

    @Override
    public byte[] getBody() {
        if (mSetToEncode) {
            return GZIPByteEncoder.encodeByteArray(super.getBody());
        } else {
            return super.getBody();
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();

        StringBuilder builder = new StringBuilder();
        builder.append(ApplicationConstants.JSESSIONID_PARAM);
        builder.append("=");
        builder.append(mOpenMRS.getSessionToken());
        params.put(ApplicationConstants.COOKIE_PARAM, builder.toString());
        params.put(GZIPByteEncoder.REQUEST_HEADER_PARAM, GZIPByteEncoder.GZIP_HEADER_VALUE);
        if (mSetToEncode) {
            params.put(GZIPByteEncoder.RESPONSE_HEADER_PARAM, GZIPByteEncoder.GZIP_HEADER_VALUE);
        }
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        /***
         http://stackoverflow.com/a/24422376/584369

         Temporary disabled but should be managed in reasonable way.
         */
        NetworkResponse checkedResponse = GZIPByteEncoder.decompressGZIPResponse(response);
        if (checkedResponse.data.length > 10000) {
            setShouldCache(false);
        }
        return super.parseNetworkResponse(checkedResponse);
    }
}
