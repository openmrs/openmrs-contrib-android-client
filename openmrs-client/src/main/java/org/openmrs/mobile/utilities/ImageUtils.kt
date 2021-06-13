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
import org.openmrs.mobile.activities.patientdashboard.details.PatientPhotoActivity
import java.io.ByteArrayOutputStream

object ImageUtils {

    @JvmStatic
    fun showPatientPhoto(context: Context, photo: Bitmap, patientName: String?) {
        val intent = Intent(context, PatientPhotoActivity::class.java)
        val byteArrayOutputStream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream)
        intent.putExtra("photo", byteArrayOutputStream.toByteArray())
        intent.putExtra("name", patientName)
        context.startActivity(intent)
    }
}