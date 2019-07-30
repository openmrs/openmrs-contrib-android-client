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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.activevisits.ActiveVisitsActivity;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.activities.formentrypatientlist.FormEntryPatientListActivity;
import org.openmrs.mobile.activities.providermanagerdashboard.ProviderManagerDashboardActivity;
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsActivity;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;
import org.openmrs.mobile.utilities.ThemeUtils;

public class DashboardFragment extends ACBaseFragment<DashboardContract.Presenter> implements DashboardContract.View, View.OnClickListener {

    // ImageView Buttons
    private ImageView mFindPatientButton;
    private ImageView mRegistryPatientButton;
    private ImageView mActiveVisitsButton;
    private ImageView mCaptureVitalsButton;
    private ImageView mProviderManagementButton;
    private RelativeLayout mFindPatientView;
    private RelativeLayout mRegistryPatientView;
    private RelativeLayout mActiveVisitsView;
    private RelativeLayout mCaptureVitalsView;
    private RelativeLayout mProviderManagementView;

    private SparseArray<Bitmap> mBitmapCache;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final String PREFS_NAME = "OpenMRSPrefFile";

        SharedPreferences settings2 = getActivity().getSharedPreferences(PREFS_NAME, 0);

        if (settings2.getBoolean("my_first_time", true)) {
            showOverlayTutorialOne();
            settings2.edit().putBoolean("my_first_time", false).apply();
        }

    }

    private void showOverlayTutorialOne() {
        Target viewTarget = new ViewTarget(R.id.findPatientView, this.getActivity());
        new ShowcaseView.Builder(this.getActivity())
                .setTarget(viewTarget)
                .setContentTitle("Find Patients")
                .setContentText("Click here to search through all the patients")
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        showOverlayTutorialTwo();
                        showcaseView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        //This method is intentionally left blank
                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                        //This method is intentionally left blank
                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
                        //This method is intentionally left blank

                    }
                })
                .build();
    }

    private void showOverlayTutorialTwo() {
        Target viewTarget = new ViewTarget(R.id.activeVisitsView, this.getActivity());
        new ShowcaseView.Builder(this.getActivity())
                .setTarget(viewTarget)
                .setContentTitle("Active Visits")
                .setContentText("Click here to get the list of all the currently active visits")
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        showOverlayTutorialThree();
                        showcaseView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        //This method is intentionally left blank
                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                        //This method is intentionally left blank
                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
                        //This method is intentionally left blank
                    }
                })
                .build();
    }

    private void showOverlayTutorialThree() {
        Target viewTarget = new ViewTarget(R.id.registryPatientView, this.getActivity());
        new ShowcaseView.Builder(this.getActivity())
                .setTarget(viewTarget)
                .setContentTitle("Register Patient")
                .setContentText("Click here to register a new patient")
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        showOverlayTutorialFour();
                        showcaseView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        //This method is intentionally left blank
                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                        //This method is intentionally left blank
                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
                        //This method is intentionally left blank
                    }
                })
                .build();
    }

    private void showOverlayTutorialFour() {
        Target viewTarget = new ViewTarget(R.id.captureVitalsView, this.getActivity());
        new ShowcaseView.Builder(this.getActivity())
                .setTarget(viewTarget)
                .setContentTitle("Form Entry")
                .setContentText("Click here to capture vitals for a patient on a visit")
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseThemeExit)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        showcaseView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        //This method is intentionally left blank
                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                        //This method is intentionally left blank
                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
                        //This method is intentionally left blank
                    }
                })
                .build();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        if (root != null) {
            initFragmentFields(root);
            setListeners();
        }

        // Font config
        FontsUtil.setFont(this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    private void initFragmentFields(View root) {
        mFindPatientButton = root.findViewById(R.id.findPatientButton);
        mRegistryPatientButton = root.findViewById(R.id.registryPatientButton);
        mActiveVisitsButton = root.findViewById(R.id.activeVisitsButton);
        mCaptureVitalsButton = root.findViewById(R.id.captureVitalsButton);
        mProviderManagementButton = root.findViewById(R.id.dashboardProviderManagementButton);
        mFindPatientView = root.findViewById(R.id.findPatientView);
        mRegistryPatientView = root.findViewById(R.id.registryPatientView);
        mCaptureVitalsView = root.findViewById(R.id.captureVitalsView);
        mActiveVisitsView = root.findViewById(R.id.activeVisitsView);
        mProviderManagementView = root.findViewById(R.id.dashboardProviderManagementView);
    }

    private void setListeners() {
        mActiveVisitsView.setOnClickListener(this);
        mRegistryPatientView.setOnClickListener(this);
        mFindPatientView.setOnClickListener(this);
        mCaptureVitalsView.setOnClickListener(this);
        mProviderManagementView.setOnClickListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    /**
     * Binds drawable resources to all ashboard buttons
     * Initially called by this view's presenter
     */
    @Override
    public void bindDrawableResources() {
        bindDrawableResource(mFindPatientButton, R.drawable.ico_search);
        bindDrawableResource(mRegistryPatientButton, R.drawable.ico_registry);
        bindDrawableResource(mActiveVisitsButton, R.drawable.ico_visits);
        bindDrawableResource(mCaptureVitalsButton, R.drawable.ico_vitals);

        if (ThemeUtils.isDarkModeActivated()) {
            changeColorOfDashboardIcons();
        }
    }

    /**
     * Binds drawable resource to ImageView
     *
     * @param imageView  ImageView to bind resource to
     * @param drawableId id of drawable resource (for example R.id.somePicture);
     */
    private void bindDrawableResource(ImageView imageView, int drawableId) {
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
    private void startNewActivity(Class<? extends ACBaseActivity> clazz) {
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
        switch (v.getId()) {
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
            case R.id.dashboardProviderManagementView:
                startNewActivity(ProviderManagerDashboardActivity.class);
                break;
            default:
                // Do nothing
                break;
        }
    }

    private void changeColorOfDashboardIcons() {
        final int greenColorResId = R.color.green;
        ImageUtils.changeImageViewTint(getContext(), mActiveVisitsButton, greenColorResId);
        ImageUtils.changeImageViewTint(getContext(), mCaptureVitalsButton, greenColorResId);
        ImageUtils.changeImageViewTint(getContext(), mFindPatientButton, greenColorResId);
        ImageUtils.changeImageViewTint(getContext(), mRegistryPatientButton, greenColorResId);
        ImageUtils.changeImageViewTint(getContext(), mProviderManagementButton, greenColorResId);

    }
}