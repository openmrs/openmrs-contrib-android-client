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

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.utilities.ApplicationConstants.INTENT_KEY_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.INTENT_KEY_PHOTO
import com.openmrs.android_sdk.utilities.ImageUtils
import org.openmrs.mobile.activities.patientdashboard.details.PatientPhotoActivity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

object ImageUtils {

    @JvmStatic
    fun showPatientPhoto(context: Context, photo: Bitmap, patientName: String?) {
        val intent = Intent(context, PatientPhotoActivity::class.java)
        val byteArrayOutputStream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream)
        intent.putExtra(INTENT_KEY_PHOTO, byteArrayOutputStream.toByteArray())
        intent.putExtra(INTENT_KEY_NAME, patientName)
        context.startActivity(intent)
    }

    fun createUniqueImageFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return timeStamp + "_" + ".jpg"
    }

    fun getResizedPortraitImage(imagePath: String): Bitmap {
        var portraitImg: Bitmap
        val options = BitmapFactory.Options().apply { inSampleSize = 4 }
        val photo = BitmapFactory.decodeFile(imagePath, options)
        val rotateAngle: Float
        try {
            val orientation = ExifInterface(imagePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            rotateAngle = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                else -> 0f
            }
            portraitImg = ImageUtils.rotateImage(photo, rotateAngle)
        } catch (e: IOException) {
            OpenmrsAndroid.getOpenMRSLogger().e(e.message)
            portraitImg = photo
        }
        return ImageUtils.resizePhoto(portraitImg)
    }
}
