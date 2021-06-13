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
package com.example.openmrs_android_sdk.utilities

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.openmrs_android_sdk.R
import com.example.openmrs_android_sdk.library.OpenmrsAndroid
import com.example.openmrs_android_sdk.utilities.ImageUtils.decodeBitmapFromResource

object ToastUtil {
    private val logger =  OpenmrsAndroid.getOpenMRSLogger();
    private val toastQueue: MutableList<ToastThread> = ArrayList()
    private var isAppVisible = true
    @JvmStatic
    fun setAppVisible(appVisible: Boolean) {
        isAppVisible = appVisible
    }

    @JvmStatic
    fun notifyLong(message: String) {
        showToast(OpenmrsAndroid.getInstance()!!, ToastType.NOTICE, message, Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun notify(message: String) {
        showToast(OpenmrsAndroid.getInstance()!!, ToastType.NOTICE, message, Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun success(message: String) {
        showToast(OpenmrsAndroid.getInstance()!!, ToastType.SUCCESS, message, Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun error(message: String) {
        showToast(OpenmrsAndroid.getInstance()!!, ToastType.ERROR, message, Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun showShortToast(context: Context, type: ToastType, textId: Int) {
        showToast(context, type, context.resources.getString(textId), Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun showLongToast(context: Context, type: ToastType, textId: Int) {
        showToast(context, type, context.resources.getString(textId), Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun showShortToast(context: Context, type: ToastType, text: String) {
        showToast(context, type, text, Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun showLongToast(context: Context, type: ToastType, text: String) {
        showToast(context, type, text, Toast.LENGTH_LONG)
    }

    @JvmStatic
    private fun showToast(context: Context, type: ToastType,
                          text: String, duration: Int) {
        if (!isAppVisible) {
            return
        }
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastRoot = inflater.inflate(R.layout.toast, null)
        val bitmap: Bitmap
        val toastImage = toastRoot.findViewById<ImageView>(R.id.toastImage)
        val toastText = toastRoot.findViewById<TextView>(R.id.toastText)
        val toastLayout = toastRoot.findViewById<LinearLayout>(R.id.toastLayout)
        toastText.text = text
        bitmap = decodeBitmapFromResource(
                context.resources,
                getImageResId(type),
                toastImage.layoutParams.width,
                toastImage.layoutParams.height)
        toastImage.setImageBitmap(bitmap)
        when (type) {
            ToastType.ERROR, ToastType.WARNING -> toastLayout.setBackgroundResource(R.drawable.toast_border_warning)
            ToastType.NOTICE -> toastLayout.setBackgroundResource(R.drawable.toast_border_notice)
            ToastType.SUCCESS -> toastLayout.setBackgroundResource(R.drawable.toast_border_success)
        }
        logger.d("Decode bitmap: $bitmap")
        val toast = Toast(context)
        toast.view = toastRoot
        toast.duration = duration
        toast.show()
        val thread = ToastThread(duration)
        if (toastQueue.size == 0) {
            thread.start()
        }
        toastQueue.add(thread)
    }

    @JvmStatic
    private fun getImageResId(type: ToastType): Int {
        var toastTypeImageId = 0
        when (type) {
            ToastType.ERROR, ToastType.WARNING -> toastTypeImageId = R.drawable.toast_warning
            ToastType.NOTICE -> toastTypeImageId = R.drawable.toast_notice
            ToastType.SUCCESS -> toastTypeImageId = R.drawable.toast_success
        }
        return toastTypeImageId
    }


    enum class ToastType {
        ERROR, NOTICE, SUCCESS, WARNING
    }

    private class ToastThread(private val mDuration: Int) : Thread() {
        override fun run() {
            try {
                if (mDuration == Toast.LENGTH_SHORT) {
                    sleep(2000)
                } else {
                    sleep(3500)
                }
                toastQueue.removeAt(0)
                if (toastQueue.size > 0) {
                    toastQueue[0].run()
                }
            } catch (e: Exception) {
                logger.e(e.toString())
            }
        }

    }
}