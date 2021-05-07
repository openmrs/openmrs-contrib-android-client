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
package org.openmrs.mobile.activities.logs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.application.OpenMRSLogger
import java.io.File
import java.io.FileInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileNotFoundException
import java.io.IOException

class LogsViewModel : ViewModel() {

    val openMRSLogger : OpenMRSLogger = OpenMRS.getInstance().openMRSLogger
    val logsText = MutableLiveData<String>()

    fun getLogs() {
        var textLogs = ""
        val filename = (OpenMRS.getInstance().openMRSDir
                + File.separator + openMRSLogger.logFilename)
        try {
            val myFile = File(filename)
            val fIn = FileInputStream(myFile)
            val myReader = BufferedReader(InputStreamReader(fIn))
            while (myReader.readLine() != null) {
                textLogs += myReader.readLine()
            }
            logsText.value = textLogs
            myReader.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}