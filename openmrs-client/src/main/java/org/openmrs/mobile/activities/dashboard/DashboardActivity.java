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

package org.openmrs.mobile.activities.dashboard;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class DashboardActivity extends ACBaseActivity {

    /*TODO: Permission handling to be coded later, moving to SDK 22 for now.
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Bundle currinstantstate;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Create toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.openmrs_action_logo);
        }

        // Create fragment
        DashboardFragment dashboardFragment =
                (DashboardFragment) getSupportFragmentManager().findFragmentById(R.id.dashboardContentFrame);
        if (dashboardFragment == null) {
            dashboardFragment = DashboardFragment.newInstance();
        }
        if (!dashboardFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    dashboardFragment, R.id.dashboardContentFrame);
        }

        // Create the presenter
        new DashboardPresenter(dashboardFragment);

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

}
