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
package org.openmrs.mobile.activities.patientdashboard.details

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.openmrs.mobile.databinding.ActivityPatientPhotoBinding
import java.io.ByteArrayInputStream

class PatientPhotoActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPatientPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        with(supportActionBar!!) {
            title = intent.getStringExtra("name")
            setDisplayHomeAsUpEnabled(true)
        }

        val photo = intent.getByteArrayExtra("photo")
        val inputStream = ByteArrayInputStream(photo)
        val patientPhoto = BitmapFactory.decodeStream(inputStream)

        binding.patientPhoto.setImageBitmap(patientPhoto)
    }
}
