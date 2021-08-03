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
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.dashboard.DashboardActivity

class IntroActivity : AppIntro2() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_welcome),
                description = getString(R.string.intro_welcome_desc),
                imageDrawable = R.drawable.openmrs_logo,
                titleColor = Color.BLACK,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.WHITE
        ))
        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_register),
                description = getString(R.string.intro_register_desc),
                imageDrawable = R.drawable.ico_registry,
                backgroundColor = Color.parseColor("#F8793B")
        ))
        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_find),
                description = getString(R.string.intro_find_desc),
                imageDrawable = R.drawable.ico_search,
                backgroundColor = Color.parseColor("#009384")
        ))
        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_monitor),
                description = getString(R.string.intro_monitor_desc),
                imageDrawable = R.drawable.ico_visits,
                backgroundColor = Color.parseColor("#F0A815")
        ))
        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_manage),
                description = getString(R.string.intro_manage_desc),
                imageDrawable = R.drawable.ic_provider_big,
                backgroundColor = Color.parseColor("#F26522")
        ))
        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_location),
                description = getString(R.string.intro_location_desc),
                imageDrawable = R.drawable.ic_location_big,
                backgroundColor = Color.parseColor("#009384")
        ))
        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_settings),
                description = getString(R.string.intro_settings_desc),
                imageDrawable = R.drawable.ic_settings_big,
                backgroundColor = Color.parseColor("#F0A815")
        ))

        if (!OpenmrsAndroid.getFirstTime()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        startActivity(Intent(this, DashboardActivity::class.java))
        OpenmrsAndroid.setUserFirstTime(false)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, DashboardActivity::class.java))
        OpenmrsAndroid.setUserFirstTime(false)
        finish()
    }
}