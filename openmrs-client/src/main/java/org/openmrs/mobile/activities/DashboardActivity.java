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

package org.openmrs.mobile.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsActivity;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;

public class DashboardActivity extends ACBaseActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    Bundle currinstantstate;
    private SparseArray<Bitmap> mBitmapCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        currinstantstate=savedInstanceState;
//        if (Build.VERSION.SDK_INT >= 23) {
//            // Marshmallow+
//            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
//            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
//                        REQUEST_CODE_ASK_PERMISSIONS);
//                return;
//            }
//        }
////        } else {
////            // Pre-Marshmallow
////        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.openmrs_action_logo);
        }


    }

    //Permission handling to be coded later, moving to SDK 22 for now.
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission Granted
//                    super.onCreate(currinstantstate);
//                    setContentView(R.layout.activity_dashboard);
//                    FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
//
//                } else {
//                    // Permission Denied
//                    Toast.makeText(DashboardActivity.this, "Permission Denied, Exiting", Toast.LENGTH_SHORT)
//                            .show();
//                    finish();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    public void onFindPatientCallback(View v) {
        Intent i = new Intent(this, SyncedPatientsActivity.class);
        startActivity(i);
    }

    public void onRegisterPatientCallback(View v) {
        Intent i = new Intent(this, RegisterPatientActivity.class);
        startActivity(i);
    }

    public void onActiveVisitsCallback(View v) {
        Intent intent = new Intent(this, FindActiveVisitsActivity.class);
        startActivity(intent);
    }

    public void onFormsCallback(View v) {
        Intent intent = new Intent(this, PatientListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        bindDrawableResources();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    private void bindDrawableResources() {
        mBitmapCache = new SparseArray<Bitmap>();
        ImageView findPatientImageButton = (ImageView) findViewById(R.id.findPatientButton);
        ImageView registryPatientImageButton = (ImageView) findViewById(R.id.registryPatientButton);
        ImageView activeVisitsImageButton = (ImageView) findViewById(R.id.activeVisitsButton);
        ImageView captureVitalsImageButton = (ImageView) findViewById(R.id.captureVitalsButton);
        createImageBitmap(R.drawable.ico_search, findPatientImageButton.getLayoutParams());
        createImageBitmap(R.drawable.ico_registry, registryPatientImageButton.getLayoutParams());
        createImageBitmap(R.drawable.ico_visits, activeVisitsImageButton.getLayoutParams());
        createImageBitmap(R.drawable.ico_vitals, captureVitalsImageButton.getLayoutParams());
        findPatientImageButton.setImageBitmap(mBitmapCache.get(R.drawable.ico_search));
        registryPatientImageButton.setImageBitmap(mBitmapCache.get(R.drawable.ico_registry));
        activeVisitsImageButton.setImageBitmap(mBitmapCache.get(R.drawable.ico_visits));
        captureVitalsImageButton.setImageBitmap(mBitmapCache.get(R.drawable.ico_vitals));
    }

    private void createImageBitmap(Integer key, ViewGroup.LayoutParams layoutParams) {
        if (mBitmapCache.get(key) == null) {
            mBitmapCache.put(key, ImageUtils.decodeBitmapFromResource(getResources(), key,
                    layoutParams.width, layoutParams.height));
        }
    }

    private void unbindDrawableResources() {
        if (null != mBitmapCache) {
            for (int i = 0; i < mBitmapCache.size(); i++) {
                Bitmap bitmap = mBitmapCache.valueAt(i);
                bitmap.recycle();
            }
        }
    }
}
