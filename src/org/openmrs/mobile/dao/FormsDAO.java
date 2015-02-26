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

package org.openmrs.mobile.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import org.odk.collect.android.openmrs.provider.OpenMRSFormsProviderAPI;
import org.openmrs.mobile.application.OpenMRS;
import org.odk.collect.android.openmrs.provider.OpenMRSInstanceProviderAPI;
import org.openmrs.mobile.models.FormSubmission;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.io.IOException;
import java.security.InvalidParameterException;

public class FormsDAO {
    private final ContentResolver mContentResolver;

    public FormsDAO() {
        this(OpenMRS.getInstance().getContentResolver());
    }

    public FormsDAO(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }

    public Uri getFormURI(String jrFormName) throws IOException {
        if (jrFormName == null || ApplicationConstants.EMPTY_STRING.equals(jrFormName)) {
            throw new InvalidParameterException("FormId must not be null or empty");
        }
        int id = 1;
        String[] projection = new String[]{OpenMRSFormsProviderAPI.FormsColumns._ID, OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME};
        final Cursor cursor = mContentResolver.query(OpenMRSFormsProviderAPI.FormsColumns.CONTENT_URI, projection, OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME + "=?", new String[]{jrFormName}, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns._ID);
                    id = cursor.getInt(idIndex);
                } else {
                    throw new IOException();
                }
            } finally {
                cursor.close();
            }

        }
        return ContentUris.withAppendedId(OpenMRSFormsProviderAPI.FormsColumns.CONTENT_URI, id);
    }

    public FormSubmission getSurveysSubmissionDataFromFormInstanceId(String instanceId) {
        String where = String.format("%s = ?", OpenMRSInstanceProviderAPI.InstanceColumns._ID);
        String[] whereArgs = new String[]{instanceId};
        FormSubmission formSubmission = null;

        Cursor cursor = mContentResolver.query(OpenMRSInstanceProviderAPI.InstanceColumns.CONTENT_URI, null, where, whereArgs, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int formInstanceIdColumnIndex = cursor.getColumnIndex(OpenMRSInstanceProviderAPI.InstanceColumns._ID);
                    int instanceFilePathColumnIndex = cursor.getColumnIndex(OpenMRSInstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH);

                    Long formInstanceId = cursor.getLong(formInstanceIdColumnIndex);
                    String instanceFilePath = cursor.getString(instanceFilePathColumnIndex);

                    Uri toUpdate = Uri.withAppendedPath(OpenMRSInstanceProviderAPI.InstanceColumns.CONTENT_URI, formInstanceId.toString());

                    formSubmission = new FormSubmission(formInstanceId, instanceFilePath, toUpdate);
                }
            } finally {
                cursor.close();
            }
        }
        return formSubmission;
    }
}
