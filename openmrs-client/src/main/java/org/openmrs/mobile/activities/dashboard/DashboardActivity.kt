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

package org.openmrs.mobile.activities.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.activities.community.contact.AboutActivity
import org.openmrs.mobile.activities.settings.SettingsActivity
import org.openmrs.mobile.dao.LocationDAO
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ToastUtil
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class DashboardActivity : ACBaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    /*TODO: Permission handling to be coded later, moving to SDK 22 for now.
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Bundle currinstantstate;
    */


    private var doubleBackToExitPressedOnce: Boolean = false
    private var handler: Handler? = Handler()
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navController: NavController

    private var runnable = object : Runnable {
        override fun run() {
            doubleBackToExitPressedOnce = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        /*TODO: Permission handling to be coded later, moving to SDK 22 for now.
        currinstantstate=savedInstanceState;
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        else {
            // Pre-Marshmallow
        }
        */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Create toolbar
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.elevation = 0f
            actionBar.setDisplayHomeAsUpEnabled(false)
            actionBar.setDisplayUseLogoEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setLogo(R.drawable.openmrs_action_logo)
            actionBar.setTitle(R.string.organization_name)
        }
        bottomNavigation = findViewById(R.id.bottom_nav)
        bottomNavigation.itemIconTintList = null
        bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    /*TODO: Permission handling to be coded later, moving to SDK 22 for now.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    super.onCreate(currinstantstate);
                    setContentView(R.layout.activity_dashboard);
                    FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));

                } else {
                    // Permission Denied
                    Toast.makeText(DashboardActivity.this, "Permission Denied, Exiting", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

    override fun onResume() {
        super.onResume()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.dashboard_nav_host_fragment) as NavHostFragment
        val dashboardFragment: DashboardFragment? = navHostFragment.childFragmentManager.primaryNavigationFragment as DashboardFragment?
        if (dashboardFragment != null) {
            DashboardPresenter(dashboardFragment)
        }
        navController = navHostFragment.navController
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        ToastUtil.notify(getString(R.string.dashboard_exit_toast_message))
        handler?.postDelayed(runnable, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacks(runnable)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.syncedPatientsActivity -> {
                navController.navigate(R.id.action_dashboardFragment_to_syncedPatientsActivity)
                return true
            }
            // TODO: 04-Apr-21 Replace actionAbout with Profile Dashboard
            R.id.actionAbout -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
            R.id.actionLocation -> {
                if (locationList.isNotEmpty()) {
                    locationList.clear()
                }
                val observableList = LocationDAO().locations
                observableList.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(getLocationList())
                return true
            }
            R.id.actionSettings -> {
                startActivityForResult(
                    Intent(this, SettingsActivity::class.java),
                    ApplicationConstants.RequestCodes.START_SETTINGS_REQ_CODE
                )
                return true
            }
        }
        return false
    }

}