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

import org.openmrs.mobile.listeners.forms.AvailableFormsListListener;
import org.openmrs.mobile.listeners.forms.UploadXFormListener;
import org.openmrs.mobile.listeners.forms.UploadXFormWithMultiPartRequestListener;
import org.openmrs.mobile.listeners.forms.DownloadFormListener;
import org.openmrs.mobile.models.OfflineRequest;
import org.openmrs.mobile.net.volley.wrappers.MultiPartRequest;
import org.openmrs.mobile.net.volley.wrappers.StringRequestDecorator;
import org.openmrs.mobile.utilities.FileUtils;

import java.io.File;

import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class FormsManager extends BaseManager {
    private StringRequestDecorator mRequestDecorator;
    private String mAvailableFormsListBaseUrl = getBaseXFormURL() + API.FORM_LIST;
    private String mUploadXformBaseUrl = getBaseXFormURL() + API.XFORM_UPLOAD;

    public void getAvailableFormsList(AvailableFormsListListener listener) {
        mLogger.d(SENDING_REQUEST + mAvailableFormsListBaseUrl);

        mRequestDecorator = new StringRequestDecorator(Request.Method.GET,
                mAvailableFormsListBaseUrl, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(mRequestDecorator);
    }

    public void uploadXForm(final UploadXFormListener listener) {
        mLogger.d(SENDING_REQUEST + mUploadXformBaseUrl);

        mRequestDecorator = new StringRequestDecorator(Request.Method.POST,
                mUploadXformBaseUrl, listener, listener, DO_GZIP_REQUEST) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return FileUtils.fileToByteArray(listener.getInstancePath());
            }
        };
        mOpenMRS.addToRequestQueue(mRequestDecorator);
    }

    public void uploadXFormWithMultiPartRequest(UploadXFormWithMultiPartRequestListener listener) {
        if (mOnlineMode) {
                mLogger.d(SENDING_REQUEST + mUploadXformBaseUrl);

            MultiPartRequest multipartRequest = new MultiPartRequest(mUploadXformBaseUrl,
                    listener, listener, new File(listener.getInstancePath()), listener.getPatientUUID(), DO_GZIP_REQUEST);
            mOpenMRS.addToRequestQueue(multipartRequest);
        } else {
            listener.offlineAction();
            OfflineRequest offlineRequest = new OfflineRequest(MultiPartRequest.class.getName(), mUploadXformBaseUrl, listener.getInstancePath(), listener.getPatientUUID(), listener.getVisitID());
            mOpenMRS.addToRequestQueue(offlineRequest);
        }
    }

    public void downloadForm(DownloadFormListener listener) {
        String url = getBaseXFormURL() + listener.getDownloadURL();
        mLogger.d(SENDING_REQUEST + url);

        mRequestDecorator = new StringRequestDecorator(Request.Method.GET,
                url, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(mRequestDecorator);
    }
}
