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

import com.openmrs.android_sdk.library.OpenMRSLogger
import com.openmrs.android_sdk.library.OpenmrsAndroid
import org.openmrs.mobile.activities.BasePresenter
import java.io.*

class LogsPresenter(private val mLogsView: LogsContract.View, private val mOpenMRSLogger: OpenMRSLogger) : BasePresenter(), LogsContract.Presenter {

    override fun subscribe() {
        val logsText = getLogs()
        mLogsView.attachLogsToTextView(logsText)
        mLogsView.fabCopyAll(logsText)
    }

    init {
        mLogsView.setPresenter(this)
    }

    private fun getLogs(): String {
        var textLogs = ""
        val filename = (OpenmrsAndroid.getOpenMRSDir()
                + File.separator + mOpenMRSLogger.logFilename)
        try {
            val myFile = File(filename)
            val fIn = FileInputStream(myFile)
            val myReader = BufferedReader(InputStreamReader(fIn))
            while (myReader.readLine() != null) {
                textLogs += myReader.readLine()
            }
            myReader.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return textLogs
    }
}