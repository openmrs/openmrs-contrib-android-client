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
import org.openmrs.mobile.R;
import org.openmrs.mobile.bundle.FormManagerBundle;
import org.openmrs.mobile.intefaces.VisitDashboardCallbackListener;
import org.openmrs.mobile.net.BaseManager;
import org.openmrs.mobile.net.GeneralErrorListener;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.utilities.ToastUtil;

public final class UploadXFormWithMultiPartRequestListener extends GeneralErrorListener implements Response.Listener<String> {
    private final String mInstancePath;
    private final String mPatientUUID;
    private final String mVisitUUID;
    private final Long mPatientID;
    private VisitDashboardCallbackListener mCallbackListener;

    private UploadXFormWithMultiPartRequestListener(FormManagerBundle bundle) {
        mInstancePath = bundle.getInstancePath();
        mPatientUUID = bundle.getPatientUuid();
        mVisitUUID = bundle.getVisitUuid();
        mPatientID = bundle.getPatientId();
        mCallbackListener = null;
    }

    public UploadXFormWithMultiPartRequestListener(FormManagerBundle bundle, VisitDashboardCallbackListener callbackListener) {
        this(bundle);
        mCallbackListener = callbackListener;
    }

    @Override
    public void onResponse(String response) {
        ToastUtil.showLongToast(BaseManager.getCurrentContext(),
                ToastUtil.ToastType.SUCCESS,
                BaseManager.getCurrentContext().getString(R.string.forms_sent_successfully));
        new VisitsManager().findVisitByUUID(
                VisitsHelper.createFindVisitCallbacksListener(mPatientID, mVisitUUID, mCallbackListener));
    }

    public String getInstancePath() {
        return mInstancePath;
    }

    public String getPatientUUID() {
        return mPatientUUID;
    }
}
