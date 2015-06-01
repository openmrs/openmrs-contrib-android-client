/**
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

package org.openmrs.mobile.net.helpers;

import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.VisitDashboardActivity;
import org.openmrs.mobile.bundle.FormManagerBundle;
import org.openmrs.mobile.listeners.forms.AvailableFormsListListener;
import org.openmrs.mobile.listeners.forms.DownloadFormListener;
import org.openmrs.mobile.listeners.forms.UploadXFormListener;
import org.openmrs.mobile.listeners.forms.UploadXFormWithMultiPartRequestListener;
import org.openmrs.mobile.net.FormsManager;

public final class FormsHelper {

    private FormsHelper() {
    }

    public static FormManagerBundle createBundle(String filePath, String patientUUID, Long patientID, Long visitID) {
        FormManagerBundle bundle = new FormManagerBundle();
        bundle.putStringField(FormManagerBundle.INSTANCE_PATH_KEY, filePath);
        bundle.putStringField(FormManagerBundle.PATIENT_UUID_KEY, patientUUID);
        bundle.putLongField(FormManagerBundle.PATIENT_ID_KEY, patientID);
        bundle.putLongField(FormManagerBundle.VISIT_ID_KEY, visitID);
        return bundle;
    }

    public static AvailableFormsListListener createAvailableFormsListListener(FormsManager formsManager) {
        return new AvailableFormsListListener(formsManager);
    }

    public static AvailableFormsListListener createAvailableFormsListListener(FormsManager formsManager, ACBaseActivity caller) {
        return new AvailableFormsListListener(formsManager, caller);
    }

    public static UploadXFormListener createUploadXFormListener(String instancePath) {
        return new UploadXFormListener(instancePath);
    }

    public static UploadXFormWithMultiPartRequestListener createUploadXFormWithMultiPartRequestListener(FormManagerBundle bundle) {
        return new UploadXFormWithMultiPartRequestListener(bundle, null);
    }

    public static UploadXFormWithMultiPartRequestListener createUploadXFormWithMultiPartRequestListener(FormManagerBundle bundle, VisitDashboardActivity caller) {
        return new UploadXFormWithMultiPartRequestListener(bundle, caller);
    }

    public static DownloadFormListener createDownloadFormListener(String downloadURL, String formName) {
        return new DownloadFormListener(downloadURL, formName);
    }

}
