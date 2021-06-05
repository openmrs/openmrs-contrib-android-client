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

package com.example.openmrs_android_sdk.library.models

import android.graphics.Bitmap
import android.util.Base64

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.ByteArrayOutputStream
import java.io.Serializable

class PatientPhoto : Resource(), Serializable {
    @SerializedName("person")
    @Expose
    private var person: Person? = null

    @SerializedName("base64EncodedImage")
    @Expose
    private var base64EncodedImage: String? = null

    fun setPhoto(image: Bitmap) {
        val byteArray = bitmapToByteArray(image)
        base64EncodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun setPerson(person: Person) {
        this.person = person
    }

    private fun bitmapToByteArray(image: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}
