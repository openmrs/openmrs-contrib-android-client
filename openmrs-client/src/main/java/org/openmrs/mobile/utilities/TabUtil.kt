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

import android.util.DisplayMetrics
import android.view.WindowManager
import com.openmrs.android_sdk.library.OpenmrsAndroid
import org.openmrs.mobile.application.OpenMRS
import java.lang.reflect.InvocationTargetException

object TabUtil {
    const val MIN_SCREEN_WIDTH_FOR_FINDPATIENTSACTIVITY = 480
    const val MIN_SCREEN_WIDTH_FOR_PATIENTDASHBOARDACTIVITY = 960
    private val mLogger = OpenmrsAndroid.getOpenMRSLogger()

    @JvmStatic
    fun setHasEmbeddedTabs(inActionBar: Any, windowManager: WindowManager, minScreenWidth: Int) {
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val scaledScreenWidth = (displaymetrics.widthPixels / displaymetrics.density).toInt()
        val inHasEmbeddedTabs = scaledScreenWidth >= minScreenWidth

        // get the ActionBar class
        var actionBarClass: Class<*>? = inActionBar.javaClass
        actionBarClass = if (OpenMRS.getInstance().isRunningKitKatVersionOrHigher) {
            actionBarClass!!.superclass!!.superclass
        } else {
            actionBarClass!!.superclass
        }
        var inActionBar2: Any? = null
        try {
            val actionBarField = actionBarClass!!.getDeclaredField("mActionBar")
            actionBarField.isAccessible = true
            inActionBar2 = actionBarField[inActionBar]
            actionBarClass = inActionBar2.javaClass

        } catch (e: Exception) {
            when (e) {
                is IllegalAccessException, is IllegalArgumentException -> {
                    mLogger.d(e.toString())
                }
            }
            when (e) {
                is NoSuchFieldException -> {
                    inActionBar2 = inActionBar
                    mLogger.d(e.toString())
                }
            }
        }
        try {
            val method = actionBarClass!!.getDeclaredMethod("setHasEmbeddedTabs", Boolean::class.javaObjectType)
            method.isAccessible = true
            method.invoke(inActionBar2, inHasEmbeddedTabs)
        } catch (e: Exception) {
            when (e) {
                is NoSuchMethodException, is InvocationTargetException, is IllegalAccessException, is IllegalArgumentException -> {
                    mLogger.d(e.toString())
                }
            }
        }
    }
}