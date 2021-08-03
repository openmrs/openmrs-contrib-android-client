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
package org.openmrs.mobile.utilities

import com.openmrs.android_sdk.library.OpenmrsAndroid
import java.io.*

object FileUtils {
    fun fileToByteArray(path: String?): ByteArray {
        val buffer = ByteArray(4096)
        val out = ByteArrayOutputStream()
        var ios: InputStream? = null
        var read: Int
        try {
            ios = FileInputStream(path)
            while (ios.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException, is IOException -> {
                    OpenmrsAndroid.getOpenMRSLogger().d(e.toString())
                }
            }
        } finally {
            try {
                ios?.close()
                out.close()
            } catch (e: IOException) {
                OpenmrsAndroid.getOpenMRSLogger().d(e.toString())
            }
        }
        return out.toByteArray()
    }
}