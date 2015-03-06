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
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FileUtils;

import java.io.File;

import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class FormsManager extends BaseManager {
    private StringRequestDecorator mRequestDecorator;

    public void getAvailableFormsList(AvailableFormsListListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String xFormsListURL = mOpenMRS.getServerUrl() + API.XFORM_ENDPOINT + API.FORM_LIST;
        mRequestDecorator = new StringRequestDecorator(Request.Method.GET, xFormsListURL,
                listener, listener);
        queue.add(mRequestDecorator);
    }

    public void uploadXForm(final UploadXFormListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String xFormsListURL = mOpenMRS.getServerUrl() + API.XFORM_ENDPOINT + API.XFORM_UPLOAD;
        mRequestDecorator = new StringRequestDecorator(Request.Method.POST, xFormsListURL,
                listener, listener) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return FileUtils.fileToByteArray(listener.getInstancePath());
            }
        };
        queue.add(mRequestDecorator);
    }

    public void uploadXFormWithMultiPartRequest(UploadXFormWithMultiPartRequestListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String xFormsListURL = mOpenMRS.getServerUrl() + API.XFORM_ENDPOINT + API.XFORM_UPLOAD;
        MultiPartRequest multipartRequest = new MultiPartRequest(xFormsListURL,
                listener, listener, new File(listener.getInstancePath()), listener.getPatientUUID());
        queue.add(multipartRequest);
    }

    public void downloadForm(DownloadFormListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        String xFormsListURL = mOpenMRS.getServerUrl() + ApplicationConstants.API.XFORM_ENDPOINT + listener.getDownloadURL();
        mRequestDecorator = new StringRequestDecorator(Request.Method.GET, xFormsListURL,
                listener, listener);
        queue.add(mRequestDecorator);
    }



}
