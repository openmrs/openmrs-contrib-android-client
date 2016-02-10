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
import org.openmrs.mobile.utilities.FormsLoaderUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class DownloadFormListener extends GeneralErrorListener implements Response.Listener<String> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final String mDownloadURL;
    private final String mFormName;

    public DownloadFormListener(String downloadURL, String formName) {
        mDownloadURL = downloadURL;
        mFormName = formName;
    }

    @Override
    public void onResponse(String response) {
        mLogger.d(response);
        try {
            writeResponseToFile(mFormName, response);
        } catch (IOException e) {
            mLogger.d(e.toString());
        }
    }

    private void writeResponseToFile(String formName, String response) throws IOException {
        String rootName = formName.replaceAll("[^\\p{L}\\p{Digit}]", " ");
        rootName = rootName.replaceAll("\\p{javaWhitespace}+", " ");
        rootName = rootName.trim();

        String path = OpenMRS.FORMS_PATH + File.separator + rootName + ".xml";
        File file = new File(path);

        // ???
        file.createNewFile();

        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(response);
            bw.close();
            fw.close();
        } catch (IOException e) {
            mLogger.d(e.toString());
        }
        FormsLoaderUtil.saveOrUpdateForm(file);
    }

    public String getDownloadURL() {
        return mDownloadURL;
    }
}
