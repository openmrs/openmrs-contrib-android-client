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

import android.os.Bundle
import android.os.Handler
import androidx.navigation.fragment.NavHostFragment
import com.openmrs.android_sdk.utilities.ToastUtil
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity


class DashboardActivity : ACBaseActivity() {

    /*TODO: Permission handling to be coded later, moving to SDK 22 for now.
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Bundle currinstantstate;
    */


    private var doubleBackToExitPressedOnce: Boolean = false
    private var handler: Handler? = Handler()

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
            actionBar.setTitle(R.string.app_name)
        }
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
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            return;
        }
        this.doubleBackToExitPressedOnce = true;
        ToastUtil.notify(getString(R.string.dashboard_exit_toast_message));
        handler?.postDelayed(runnable, 2000);
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacks(runnable)
    }
}