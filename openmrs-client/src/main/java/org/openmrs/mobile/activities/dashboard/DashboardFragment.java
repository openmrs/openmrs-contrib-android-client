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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.activevisits.ActiveVisitsActivity;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.activities.formentrypatientlist.FormEntryPatientListActivity;
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsActivity;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;

public class DashboardFragment extends ACBaseFragment<DashboardContract.Presenter> implements DashboardContract.View, View.OnClickListener {

    // ImageView Buttons
    private ImageView mFindPatientButton;
    private ImageView mRegistryPatientButton;
    private ImageView mActiveVisitsButton;
    private ImageView mCaptureVitalsButton;
    private RelativeLayout mFindPatientView;
    private RelativeLayout mRegistryPatientView;
    private RelativeLayout mActiveVisitsView;
    private RelativeLayout mCaptureVitalsView;

    private SparseArray<Bitmap> mBitmapCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        if (root != null) {
            initFragmentFields(root);
            setListeners();
        }

        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    private void initFragmentFields(View root) {
        mFindPatientButton = (ImageView) root.findViewById(R.id.findPatientButton);
        mRegistryPatientButton = (ImageView) root.findViewById(R.id.registryPatientButton);
        mActiveVisitsButton = (ImageView) root.findViewById(R.id.activeVisitsButton);
        mCaptureVitalsButton = (ImageView) root.findViewById(R.id.captureVitalsButton);
        mFindPatientView = (RelativeLayout) root.findViewById(R.id.findPatientView);
        mRegistryPatientView = (RelativeLayout) root.findViewById(R.id.registryPatientView);
        mCaptureVitalsView = (RelativeLayout) root.findViewById(R.id.captureVitalsView);
        mActiveVisitsView = (RelativeLayout) root.findViewById(R.id.activeVisitsView);
    }

    private void setListeners() {
        mActiveVisitsView.setOnClickListener(this);
        mRegistryPatientView.setOnClickListener(this);
        mFindPatientView.setOnClickListener(this);
        mCaptureVitalsView.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    /**
     * Binds drawable resources to all dashboard buttons
     * Initially called by this view's presenter
     */
    @Override
    public void bindDrawableResources() {
        bindDrawableResource(mFindPatientButton, R.drawable.ico_search);
        bindDrawableResource(mRegistryPatientButton, R.drawable.ico_registry);
        bindDrawableResource(mActiveVisitsButton, R.drawable.ico_visits);
        bindDrawableResource(mCaptureVitalsButton, R.drawable.ico_vitals);
    }

    /**
     * Binds drawable resource to ImageView
     * @param imageView ImageView to bind resource to
     * @param drawableId id of drawable resource (for example R.id.somePicture);
     */
    private void bindDrawableResource(ImageView imageView, int  drawableId) {
        mBitmapCache = new SparseArray<>();
        if (getView() != null) {
            createImageBitmap(drawableId, imageView.getLayoutParams());
            imageView.setImageBitmap(mBitmapCache.get(drawableId));
        }
    }
    /**
     * Unbinds drawable resources
     */
    private void unbindDrawableResources() {
        if (null != mBitmapCache) {
            for (int i = 0; i < mBitmapCache.size(); i++) {
                Bitmap bitmap = mBitmapCache.valueAt(i);
                bitmap.recycle();
            }
        }
    }
    private void createImageBitmap(Integer key, ViewGroup.LayoutParams layoutParams) {
        if (mBitmapCache.get(key) == null) {
            mBitmapCache.put(key, ImageUtils.decodeBitmapFromResource(getResources(), key,
                    layoutParams.width, layoutParams.height));
        }
    }

    /**
     * Starts new Activity depending on which ImageView triggered it
     */
    private void startNewActivity(Class<? extends ACBaseActivity> clazz){
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    /**
     * @return New instance of SyncedPatientsFragment
     */
    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.findPatientView:
                startNewActivity(SyncedPatientsActivity.class);
                break;
            case R.id.registryPatientView:
                startNewActivity(AddEditPatientActivity.class);
                break;
            case R.id.captureVitalsView:
                startNewActivity(FormEntryPatientListActivity.class);
                break;
            case R.id.activeVisitsView:
                startNewActivity(ActiveVisitsActivity.class);
                break;
            default:
                // Do nothing
                break;
        }
    }
}