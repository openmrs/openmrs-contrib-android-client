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
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.dashboard.DashboardActivity
import org.openmrs.mobile.application.OpenMRS

class IntroActivity : AppIntro2() {
    private var mOpenMRS = OpenMRS.getInstance()

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_welcome), "", getString(R.string.intro_welcome_desc), "", R.drawable.openmrs_logo, Color.parseColor("#ffffff"), Color.parseColor("#000000"), Color.parseColor("#000000")))
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_register), getString(R.string.intro_register_desc), R.drawable.ico_registry, Color.parseColor("#F26522")))
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_find), getString(R.string.intro_find_desc), R.drawable.ico_search, Color.parseColor("#009384")))
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_monitor), getString(R.string.intro_monitor_desc), R.drawable.ico_visits, Color.parseColor("#F0A815")))
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_manage), getString(R.string.intro_manage_desc), R.drawable.ic_provider_big, Color.parseColor("#F26522")))
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_location), getString(R.string.intro_location_desc), R.drawable.ic_location_big, Color.parseColor("#009384")))
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_settings), getString(R.string.intro_settings_desc), R.drawable.ic_settings_big, Color.parseColor("#F0A815")))

        if (!mOpenMRS.firstTime) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    override fun onSkipPressed(currentFragment: Fragment) {
        super.onSkipPressed(currentFragment)
        startActivity(Intent(this, DashboardActivity::class.java))
        mOpenMRS.setUserFirstTime(false)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, DashboardActivity::class.java))
        mOpenMRS.setUserFirstTime(false)
        finish()
    }
}