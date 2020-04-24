package org.openmrs.mobile.utilities

import android.util.DisplayMetrics
import android.view.WindowManager
import org.openmrs.mobile.application.OpenMRS
import java.lang.Boolean
import java.lang.reflect.InvocationTargetException


object TabUtil {

    const val MIN_SCREEN_WIDTH_FOR_FINDPATIENTSACTIVITY = 480
    const val MIN_SCREEN_WIDTH_FOR_PATIENTDASHBOARDACTIVITY = 960
    private val mLogger = OpenMRS.getInstance().openMRSLogger

    @JvmStatic
    fun setHasEmbeddedTabs(inActionBar: Any, windowManager: WindowManager, minScreenWidth: Int) {
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val scaledScreenWidth = (displaymetrics.widthPixels / displaymetrics.density).toInt()
        val inHasEmbeddedTabs = scaledScreenWidth >= minScreenWidth
        // get the ActionBar class
        var actionBarClass: Class<*> = inActionBar.javaClass
        actionBarClass = if (OpenMRS.getInstance().isRunningKitKatVersionOrHigher) {
            actionBarClass.superclass.superclass
        } else {
            actionBarClass.superclass
        }
        var inActionBar2: Any? = null
        try {
            val actionBarField = actionBarClass.getDeclaredField("mActionBar")
            actionBarField.isAccessible = true
            inActionBar2 = actionBarField[inActionBar]
            actionBarClass = inActionBar2.javaClass
        } catch (e: IllegalAccessException) {
            mLogger.d(e.toString())
        } catch (e: IllegalArgumentException) {
            mLogger.d(e.toString())
        } catch (e: NoSuchFieldException) {
            inActionBar2 = inActionBar
            mLogger.d(e.toString())
        }
        try {
            val method = actionBarClass.getDeclaredMethod("setHasEmbeddedTabs", *arrayOf<Class<*>>(Boolean.TYPE))
            method.isAccessible = true
            method.invoke(inActionBar2, *arrayOf<Any>(inHasEmbeddedTabs))
        } catch (e: NoSuchMethodException) {
            mLogger.d(e.toString())
        } catch (e: InvocationTargetException) {
            mLogger.d(e.toString())
        } catch (e: IllegalAccessException) {
            mLogger.d(e.toString())
        } catch (e: IllegalArgumentException) {
            mLogger.d(e.toString())
        }
    }
}
