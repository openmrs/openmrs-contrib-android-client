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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.openmrs.mobile.activities.listeners.AvailableFormsListListener;
import org.openmrs.mobile.activities.listeners.UploadXFormListener;
import org.openmrs.mobile.activities.listeners.UploadXFormWithMultiPartRequestListener;
import org.openmrs.mobile.activities.listeners.DownloadFormListener;
import org.openmrs.mobile.net.volley.wrappers.MultiPartRequest;
import org.openmrs.mobile.net.volley.wrappers.StringRequestDecorator;
import org.openmrs.mobile.utilities.FileUtils;

import java.io.File;

import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class FormsManager extends BaseManager {
    private StringRequestDecorator mRequestDecorator;
    private static final String AVAILABLE_FORMS_LIST_BASE_URL = getBaseXFormURL() + API.FORM_LIST;
    private static final String UPLOAD_XFORM_BASE_URL = getBaseXFormURL() + API.XFORM_UPLOAD;

    public void getAvailableFormsList(AvailableFormsListListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        mLogger.d(SENDING_REQUEST + AVAILABLE_FORMS_LIST_BASE_URL);

        mRequestDecorator = new StringRequestDecorator(Request.Method.GET,
                AVAILABLE_FORMS_LIST_BASE_URL, listener, listener, DO_GZIP_REQUEST);
        queue.add(mRequestDecorator);
    }

    public void uploadXForm(final UploadXFormListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        mLogger.d(SENDING_REQUEST + UPLOAD_XFORM_BASE_URL);

        mRequestDecorator = new StringRequestDecorator(Request.Method.POST,
                UPLOAD_XFORM_BASE_URL, listener, listener, DO_GZIP_REQUEST) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return FileUtils.fileToByteArray(listener.getInstancePath());
            }
        };
        queue.add(mRequestDecorator);
    }

    public void uploadXFormWithMultiPartRequest(UploadXFormWithMultiPartRequestListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        mLogger.d(SENDING_REQUEST + UPLOAD_XFORM_BASE_URL);

        MultiPartRequest multipartRequest = new MultiPartRequest(UPLOAD_XFORM_BASE_URL,
                listener, listener, new File(listener.getInstancePath()), listener.getPatientUUID(), DO_GZIP_REQUEST);
        queue.add(multipartRequest);
    }

    public void downloadForm(DownloadFormListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String url = getBaseXFormURL() + listener.getDownloadURL();
        mLogger.d(SENDING_REQUEST + url);

        mRequestDecorator = new StringRequestDecorator(Request.Method.GET,
                url, listener, listener, DO_GZIP_REQUEST);
        queue.add(mRequestDecorator);
    }



}
