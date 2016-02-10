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



import org.openmrs.mobile.application.OpenMRS;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtils {

    private FileUtils() {

    }

    public static byte[] fileToByteArray(String path) {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream ios = null;
        int read = 0;
        try {
            ios = new FileInputStream(path);
            while ((read = ios.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (FileNotFoundException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        } catch (IOException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        } finally {
            try {
                if (ios != null) {
                    ios.close();
                }
                out.close();
            } catch (IOException e) {
                OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
            }
        }

        return out.toByteArray();
    }
}
