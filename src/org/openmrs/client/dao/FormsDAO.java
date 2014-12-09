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

package org.openmrs.client.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import org.odk.collect.android.openmrs.provider.OpenMRSFormsProviderAPI;
import org.openmrs.client.application.OpenMRS;

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

    public Uri getFormURI(String jrFormId) throws IOException {
        if (jrFormId == null || "".equals(jrFormId)) {
            throw new InvalidParameterException("FormId must not be null or empty");
        }
        int id = 1;
        String[] projection = new String[]{OpenMRSFormsProviderAPI.FormsColumns._ID, OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID};
        final Cursor cursor = mContentResolver.query(OpenMRSFormsProviderAPI.FormsColumns.CONTENT_URI, projection, OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID + "=?", new String[]{jrFormId}, null);
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

}
