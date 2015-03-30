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

package org.openmrs.mobile.listeners.forms;

import com.android.volley.Response;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.net.GeneralErrorListener;

public final class UploadXFormListener extends GeneralErrorListener implements Response.Listener<String> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final String mInstancePath;

    public UploadXFormListener(String instancePath) {
        mInstancePath = instancePath;
    }

    @Override
    public void onResponse(String response) {
        mLogger.d(response);
    }

    public String getInstancePath() {
        return mInstancePath;
    }
}
