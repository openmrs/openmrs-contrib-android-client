package org.openmrs.mobile.utilities

import android.app.Activity
import android.os.Build
import android.os.Process
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.application.OpenMRSLogger
import java.io.*


class ForceClose(private val myContext: Activity) : Thread.UncaughtExceptionHandler {

    private val LINE_SEPARATOR = "\n"

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val stackTrace = StringWriter()
        exception.printStackTrace(PrintWriter(stackTrace))
        val errorReport = StringBuilder()
        errorReport.append("************ CAUSE OF ERROR ************\n\n")
        errorReport.append(stackTrace.toString())
        errorReport.append("\n************ DEVICE INFORMATION ***********\n")
        errorReport.append("Brand: ")
        errorReport.append(Build.BRAND)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Device: ")
        errorReport.append(Build.DEVICE)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Model: ")
        errorReport.append(Build.MODEL)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Id: ")
        errorReport.append(Build.ID)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Product: ")
        errorReport.append(Build.PRODUCT)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("\n************ FIRMWARE ************\n")
        errorReport.append("SDK: ")
        errorReport.append(Build.VERSION.SDK_INT)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Release: ")
        errorReport.append(Build.VERSION.RELEASE)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Incremental: ")
        errorReport.append(Build.VERSION.INCREMENTAL)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("\n************ APP LOGS ************\n")
        errorReport.append(getLogs())
        val i = myContext.packageManager.getLaunchIntentForPackage("org.openmrs.mobile")
        i.putExtra("flag", true)
        i.putExtra("error", errorReport.toString())
        myContext.startActivity(i)
        Process.killProcess(Process.myPid())
        System.exit(10)
    }

    fun getLogs(): String?{
            val mOpenMRSLogger = OpenMRSLogger()
            var textLogs: String? = ""
            val filename = (OpenMRS.getInstance().openMRSDir
                    + File.separator + mOpenMRSLogger.logFilename)
            try {
                val myFile = File(filename)
                val fIn = FileInputStream(myFile)
                val myReader = BufferedReader(InputStreamReader(fIn))
                var aDataRow: String?
                while (myReader.readLine().also { aDataRow = it } != null) {
                    textLogs += aDataRow
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
