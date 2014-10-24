package org.openmrs.client.utilities;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;

import org.apache.commons.io.IOUtils;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.openmrs.provider.OpenMRSFormsProviderAPI;
import org.odk.collect.android.utilities.FileUtils;
import org.openmrs.client.application.OpenMRS;

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
    public static final String CAPTURE_VITALS_FORM = "vitals";
    public static final String REGISTRY_PATIENT_FORM = "registration";

    public static final List<String> DEFAULT_FORMS = new ArrayList<String>(
            Arrays.asList(CAPTURE_VITALS_FORM, REGISTRY_PATIENT_FORM)
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
            try {
                inputStream = manager.open(FormsLoaderUtil.ASSET_DIR + formName + FormsLoaderUtil.XML_SUFFIX);
            } catch (IOException e) {
                OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
                OpenMRS.getInstance().getOpenMRSLogger().d("Failed to load form : " + formName);
            }
            FormsLoaderUtil.copyFormFromAssets(formName, inputStream);
        }

    }

    private static Uri copyFormFromAssets(String fileName, InputStream in) {
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
        return saveDefaultFormToDB(form, form.getAbsolutePath());
    }

    private static Uri saveDefaultFormToDB(File formFile, String formFilePath) {
        Cursor cursor = null;
        Uri uri = null;
        boolean isNew;

        try {
            String[] selectionArgs = {
                    formFile.getAbsolutePath()
            };
            String selection = OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH + "=?";
            cursor = Collect.getInstance()
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
                v.put(OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID, formInfo.get(FileUtils.FORMID));
                v.put(OpenMRSFormsProviderAPI.FormsColumns.SUBMISSION_URI, formInfo.get(FileUtils.SUBMISSIONURI));
                v.put(OpenMRSFormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY, formInfo.get(FileUtils.BASE64_RSA_PUBLIC_KEY));
                uri = Collect.getInstance().getContentResolver().insert(OpenMRSFormsProviderAPI.FormsColumns.CONTENT_URI, v);
            } else {
                cursor.moveToFirst();
                uri = Uri.withAppendedPath(OpenMRSFormsProviderAPI.FormsColumns.CONTENT_URI,
                        cursor.getString(cursor.getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns._ID)));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return uri;
    }
}
