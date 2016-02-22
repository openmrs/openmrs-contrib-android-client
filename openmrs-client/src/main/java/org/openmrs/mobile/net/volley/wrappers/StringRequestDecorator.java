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
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.net.BaseManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class StringRequestDecorator extends StringRequest {
    private boolean mSetToEncode;
    private int mMethod;
    private final String mUrl;
    private final Response.Listener<String> mListener;
    private final Response.ErrorListener mErrorListener;
    private final OpenMRS mOpenMRS = OpenMRS.getInstance();
    private final OpenMRSLogger mLogger = mOpenMRS.getOpenMRSLogger();

    public StringRequestDecorator(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, boolean setToEncode) {
        super(method, url, listener, errorListener);
        mMethod = method;
        mUrl = url;
        mListener = listener;
        mErrorListener = errorListener;
        mSetToEncode = setToEncode;
        setShouldCache(false);
    }

    public StringRequestDecorator(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, boolean setToEncode) {
        super(url, listener, errorListener);
        mUrl = url;
        mListener = listener;
        mErrorListener = errorListener;
        mSetToEncode = setToEncode;
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
            StringRequestDecorator strRequest = new StringRequestDecorator(mMethod, mUrl, mListener, mErrorListener, false);
            mOpenMRS.addToRequestQueue(strRequest);
            return;
        }
        super.deliverError(error);
    }

    @Override
    public byte[] getBody() throws  AuthFailureError {
        if (mSetToEncode) {
            return GZIPByteEncoder.encodeByteArray(super.getBody());
        } else {
            return super.getBody();
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        NetworkResponse checkedResponse = GZIPByteEncoder.decompressGZIPResponse(response);
        String responseAsString = null;
        try {
            responseAsString = new String(checkedResponse.data, HttpHeaderParser.parseCharset(checkedResponse.headers));
        } catch (UnsupportedEncodingException e) {
            mLogger.d(e.toString());
        }
        if (null != responseAsString && !responseAsString .isEmpty()) {
            if (responseAsString.contains("<!DOCTYPE html>")) {
                return Response.error(new AuthFailureError(ApplicationConstants.VolleyErrors.AUTHORISATION_FAILURE));
            }
        }
        return super.parseNetworkResponse(checkedResponse);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<String, String>();

        StringBuilder builder = new StringBuilder();
        builder.append(ApplicationConstants.JSESSIONID_PARAM);
        builder.append("=");
        builder.append(mOpenMRS.getSessionToken());
        params.put(ApplicationConstants.COOKIE_PARAM, builder.toString());
        params.put(GZIPByteEncoder.REQUEST_HEADER_PARAM, GZIPByteEncoder.GZIP_HEADER_VALUE);

        return params;
    }
}
