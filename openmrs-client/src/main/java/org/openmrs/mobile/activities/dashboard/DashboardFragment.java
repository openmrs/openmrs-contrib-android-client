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
import org.openmrs.mobile.utilities.ApplicationConstants;
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

    /**
     * @return New instance of SyncedPatientsFragment
     */
    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final String PREFS_NAME = ApplicationConstants.OPENMRS_PREF_FILE;

        SharedPreferences settings2 = getActivity().getSharedPreferences(ApplicationConstants.OPENMRS_PREF_FILE, 0);

        if (settings2.getBoolean("my_first_time", true)) {
            showOverlayTutorial(R.id.findPatientView, getString(R.string.dashboard_search_icon_label),
                getString(R.string.showcase_find_patients), R.style.CustomShowcaseTheme,
                ApplicationConstants.ShowCaseViewConstants.SHOW_FIND_PATIENT, true);
            settings2.edit().putBoolean("my_first_time", false).apply();
        }
    }

    private void showOverlayTutorial(int view, String title, String content, int styleTheme,
                                     int currentViewCount, Boolean showTextBelow) {
        Target viewTarget = new ViewTarget(view, this.getActivity());
        ShowcaseView.Builder builder = new ShowcaseView.Builder(this.getActivity())
            .setTarget(viewTarget)
            .setContentTitle(title)
            .setContentText(content)
            .hideOnTouchOutside()
            .setStyle(styleTheme)
            .setShowcaseEventListener(new OnShowcaseEventListener() {
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                    switch (currentViewCount) {
                        case ApplicationConstants.ShowCaseViewConstants.SHOW_FIND_PATIENT:
                            showOverlayTutorial(R.id.activeVisitsView, getString(R.string.dashboard_visits_icon_label),
                                getString(R.string.showcase_active_visits), R.style.CustomShowcaseTheme,
                                ApplicationConstants.ShowCaseViewConstants.SHOW_ACTIVE_VISITS, true);
                            break;
                        case ApplicationConstants.ShowCaseViewConstants.SHOW_ACTIVE_VISITS:
                            showOverlayTutorial(R.id.registryPatientView, getString(R.string.action_register_patient),
                                getString(R.string.showcase_register_patient), R.style.CustomShowcaseTheme,
                                ApplicationConstants.ShowCaseViewConstants.SHOW_REGISTER_PATIENT, false);
                            break;
                        case ApplicationConstants.ShowCaseViewConstants.SHOW_REGISTER_PATIENT:
                            showOverlayTutorial(R.id.captureVitalsView, getString(R.string.dashboard_forms_icon_label),
                                getString(R.string.showcase_form_entry), R.style.CustomShowcaseTheme,
                                ApplicationConstants.ShowCaseViewConstants.SHOW_FORM_ENTRY, false);
                            break;
                        case ApplicationConstants.ShowCaseViewConstants.SHOW_FORM_ENTRY:
                            showOverlayTutorial(R.id.dashboardProviderManagementView, getString(R.string.action_provider_management),
                                getString(R.string.showcase_manage_providers), R.style.CustomShowcaseThemeExit,
                                ApplicationConstants.ShowCaseViewConstants.SHOW_MANAGE_PROVIDERS, false);
                            break;
                    }
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
            });
        if (showTextBelow) {
            builder.build();
        } else {
            builder.build().forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        if (root != null) {
            initFragmentFields(root);
            setListeners();
        }
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
     * Binds drawable resources to all dashboard buttons
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
     * @param imageView ImageView to bind resource to
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
