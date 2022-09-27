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
package org.openmrs.mobile.activities.login

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity

@AndroidEntryPoint
class LoginActivity : ACBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.run {
            elevation = 0f
            setTitle(R.string.app_name)
        }

        // Create fragment
        var loginFragment = supportFragmentManager.findFragmentById(R.id.loginContentFrame) as LoginFragment?
        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance()
        }
        if (!loginFragment.isActive) {
            addFragmentToActivity(supportFragmentManager, loginFragment, R.id.loginContentFrame)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = true
}
