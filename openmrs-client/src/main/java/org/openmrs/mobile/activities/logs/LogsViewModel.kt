package org.openmrs.mobile.activities.logs

import com.openmrs.android_sdk.library.OpenMRSLogger
import com.openmrs.android_sdk.library.OpenmrsAndroid
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import javax.inject.Inject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

@HiltViewModel
class LogsViewModel @Inject constructor(private val openMRSLogger: OpenMRSLogger) : BaseViewModel<Unit>() {

    val logs: String = readLogsFromFile()

    private fun readLogsFromFile(): String {
        var textLogs = ""
        try {
            val filename = OpenmrsAndroid.getOpenMRSDir() + File.separator + openMRSLogger.logFilename
            val stream = FileInputStream(File(filename))
            val reader = BufferedReader(InputStreamReader(stream))
            while (reader.readLine() != null) textLogs += reader.readLine()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            openMRSLogger.e(e.message)
        }
        return textLogs
    }
}

