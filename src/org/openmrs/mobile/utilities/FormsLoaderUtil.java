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

package org.openmrs.mobile.utilities;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;

import org.apache.commons.io.IOUtils;
import org.odk.collect.android.openmrs.provider.OpenMRSFormsProviderAPI;
import org.odk.collect.android.utilities.FileUtils;
import org.openmrs.mobile.application.OpenMRS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This should be used only with default forms from assets
 */
public final class FormsLoaderUtil {
    public static final String XML_SUFFIX = ".xml";
    public static final String ASSET_DIR = "openmrs-forms" + File.separator;
    public static final String CAPTURE_VITALS_FORM_NAME = "Vitals XForm";

    public static final List<String> DEFAULT_FORMS = new ArrayList<String>(
            Arrays.asList(CAPTURE_VITALS_FORM_NAME)
    );

    private FormsLoaderUtil() {
    }

    /**
     * Loads pre-defined forms, generated with OpenMRS XForm module,
     * from assets and saves them in database
     * @param manager
     */
    public static void loadDefaultForms(AssetManager manager) {
        InputStream inputStream = null;
        for (String formName : DEFAULT_FORMS) {
            String formNameWithExtension = formName + FormsLoaderUtil.XML_SUFFIX;
            if (OpenMRS.getInstance().getDefaultFormLoadID(formName).isEmpty()) {
                try {
                    inputStream = manager.open(FormsLoaderUtil.ASSET_DIR + formNameWithExtension);
                } catch (IOException e) {
                    OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
                    OpenMRS.getInstance().getOpenMRSLogger().d("Failed to load form : " + formName);
                }
                OpenMRS.getInstance().setDefaultFormLoadID(formName, FormsLoaderUtil.copyFormFromAssets(formNameWithExtension, inputStream));
            }
        }

    }

    private static String copyFormFromAssets(String fileName, InputStream in) {
        File form = new File(OpenMRS.FORMS_PATH, fileName);
        try {
            form.createNewFile();
            FileOutputStream out = new FileOutputStream(form);
            IOUtils.copy(in, out);
        } catch (FileNotFoundException fnfx) {
            OpenMRS.getInstance().getOpenMRSLogger().d(fnfx.toString());
        } catch (IOException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        }
        return saveOrUpdateForm(form);
    }

    public static String saveOrUpdateForm(File formFile) {
        Cursor cursor = null;
        boolean isNew;
        String formID = ApplicationConstants.EMPTY_STRING;
        String formFilePath = formFile.getAbsolutePath();
        try {
            String[] selectionArgs = {
                    formFile.getAbsolutePath()
            };
            String selection = OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH + "=?";
            cursor = OpenMRS.getInstance()
                    .getContentResolver()
                    .query(OpenMRSFormsProviderAPI.FormsColumns.CONTENT_URI, null, selection, selectionArgs,
                            null);

            isNew = cursor.getCount() <= 0;
            if (isNew) {
                ContentValues v = new ContentValues();
                v.put(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH, formFilePath);

                HashMap<String, String> formInfo = FileUtils.parseXML(formFile);

                v.put(OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME, formInfo.get(FileUtils.TITLE));
                v.put(OpenMRSFormsProviderAPI.FormsColumns.JR_VERSION, formInfo.get(FileUtils.VERSION));
                formID = formInfo.get(FileUtils.FORMID);
                v.put(OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID, formID);
                v.put(OpenMRSFormsProviderAPI.FormsColumns.SUBMISSION_URI, formInfo.get(FileUtils.SUBMISSIONURI));
                OpenMRS.getInstance().getContentResolver().insert(OpenMRSFormsProviderAPI.FormsColumns.CONTENT_URI, v);
            } else {
                cursor.moveToFirst();
                formID = cursor.getString(cursor.getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns._ID));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return formID;
    }
}
