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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.net.BaseManager;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiPartRequest extends Request<String> {
    private MultipartEntity entity = new MultipartEntity();
    private static final String FILE_PART_NAME = "xml_submission_file";
    private final OpenMRS mOpenMRS = OpenMRS.getInstance();
    private final OpenMRSLogger mLogger = mOpenMRS.getOpenMRSLogger();
    private final String mUrl;
    private final Response.ErrorListener mErrorListener;
    private final Response.Listener<String> mListener;
    private final File mFilePart;
    private final String mPatientUUID;
    private boolean mSetToEncode;

    public MultiPartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, File file, String patientUUID, boolean setToEncode) {
        super(Method.POST, url, errorListener);
        mUrl = url;
        mErrorListener = errorListener;
        mListener = listener;
        mFilePart = file;
        mPatientUUID = patientUUID;
        mSetToEncode = setToEncode;
        setShouldCache(false);
        buildMultiPartEntity();
        setSocketTimeout();
    }

    private void buildMultiPartEntity() {
        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
    }

    private void setSocketTimeout() {
        int socketTimeout = ApplicationConstants.API.REQUEST_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.setRetryPolicy(policy);
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
            MultiPartRequest mpRequest = new MultiPartRequest(mUrl, mErrorListener, mListener, mFilePart, mPatientUUID, false);
            mOpenMRS.addToRequestQueue(mpRequest);
            return;
        }
        super.deliverError(error);
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream entityBytes = new ByteArrayOutputStream();
        try {
            entity.writeTo(entityBytes);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }

        if (mSetToEncode) {
            return GZIPByteEncoder.encodeByteArray(entityBytes.toByteArray());
        } else {
            return entityBytes.toByteArray();
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success("Uploaded", getCacheEntry());
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();

        StringBuilder builder = new StringBuilder();
        builder.append(ApplicationConstants.JSESSIONID_PARAM);
        builder.append("=");
        builder.append(mOpenMRS.getSessionToken());
        params.put(ApplicationConstants.COOKIE_PARAM, builder.toString());
        params.put(ApplicationConstants.PATIENT_UUID_PARAM, mPatientUUID);
        if (mSetToEncode) {
            /** Sending encoded POST */
            params.put(GZIPByteEncoder.RESPONSE_HEADER_PARAM, GZIPByteEncoder.GZIP_HEADER_VALUE);
        }
        params.put(GZIPByteEncoder.REQUEST_HEADER_PARAM, GZIPByteEncoder.GZIP_HEADER_VALUE);
        return params;
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
