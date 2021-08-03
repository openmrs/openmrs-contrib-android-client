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
package org.openmrs.mobile.activities.introduction

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.*
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.databinding.ActivitySplashBinding
import com.openmrs.android_sdk.utilities.ApplicationConstants

class SplashActivity : ACBaseActivity() {

    private val mHandler = Handler()
    private lateinit var mRunnable: Runnable
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeface = Typeface.createFromAsset(assets, ApplicationConstants.TypeFacePathConstants.MONTSERRAT)
        with(binding) {
            organizationName.typeface = typeface
            organizationName.setText(R.string.organization_name)
            clientName.typeface = typeface
            clientName.setText(R.string.client_name)
        }
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val move = AnimationUtils.loadAnimation(applicationContext, R.anim.splash_screen_logo_anim)
        val set = AnimationSet(true)
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = AccelerateInterpolator()
        fadeIn.duration = 1000
        set.addAnimation(fadeIn)
        set.addAnimation(move)
        binding.logo.startAnimation(set)
        mRunnable = Runnable {
            val intent = Intent(this@SplashActivity, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }
        mHandler.postDelayed(mRunnable, ApplicationConstants.SPLASH_TIMER.toLong())
    }

    override fun onDestroy() {
        mHandler.removeCallbacks(mRunnable)
        super.onDestroy()
    }
}