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
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.FindActiveVisitsActivity;
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsActivity;
import org.openmrs.mobile.activities.PatientListActivity;
import org.openmrs.mobile.activities.RegisterPatientActivity;
import org.openmrs.mobile.activities.fragments.ACBaseFragment;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class DashboardFragment extends ACBaseFragment implements DashboardContract.View {

    // Presenter
    private DashboardContract.Presenter mPresenter;

    // ImageView Buttons
    private ImageView mFindPatientButton;
    private ImageView mRegistryPatientButton;
    private ImageView mActiveVisitsButton;
    private ImageView mCaptureVitalsButton;

    private SparseArray<Bitmap> mBitmapCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // ImageView Button config
        if (root != null) {
            mFindPatientButton = (ImageView) root.findViewById(R.id.findPatientButton);
            mRegistryPatientButton = (ImageView) root.findViewById(R.id.registryPatientButton);
            mActiveVisitsButton = (ImageView) root.findViewById(R.id.activeVisitsButton);
            mCaptureVitalsButton = (ImageView) root.findViewById(R.id.captureVitalsButton);
        }

        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
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
        bindDrawableResourceAndSetListeners(mFindPatientButton, R.drawable.ico_search);
        bindDrawableResourceAndSetListeners(mRegistryPatientButton, R.drawable.ico_registry);
        bindDrawableResourceAndSetListeners(mActiveVisitsButton, R.drawable.ico_visits);
        bindDrawableResourceAndSetListeners(mCaptureVitalsButton, R.drawable.ico_vitals);
    }

    /**
     * Binds drawable resource to ImageView and sets its listener
     * @param imageView ImageView to bind resource to
     * @param drawableId id of drawable resource (for example R.id.somePicture);
     */
    private void bindDrawableResourceAndSetListeners(ImageView imageView, int  drawableId) {
        mBitmapCache = new SparseArray<>();
        if (getView() != null) {
            createImageBitmap(drawableId, imageView.getLayoutParams());
            imageView.setImageBitmap(mBitmapCache.get(drawableId));
        }
        setButtonListener(imageView);
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
     * @return state of fragment
     */
    @Override
    public boolean isActive() {
        return isAdded();
    }

    /**
     * Sets presenter for this view
     */
    @Override
    public void setPresenter(@NonNull DashboardContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    /**
     * Sets listeners for ImageView Buttons
     */
    private void setButtonListener(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.findPatientButton:
                        startNewActivity(SyncedPatientsActivity.class);
                        break;
                    case R.id.registryPatientButton:
                        startNewActivity(RegisterPatientActivity.class);
                        break;
                    case R.id.activeVisitsButton:
                        startNewActivity(FindActiveVisitsActivity.class);
                        break;
                    case R.id.captureVitalsButton:
                        startNewActivity(PatientListActivity.class);
                        break;
                }
            }
        });
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
}