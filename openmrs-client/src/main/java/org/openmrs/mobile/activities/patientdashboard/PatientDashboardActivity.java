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

package org.openmrs.mobile.activities.patientdashboard;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.addeditallergy.AddEditAllergyActivity;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.activities.patientdashboard.allergy.PatientAllergyFragment;
import org.openmrs.mobile.activities.patientdashboard.allergy.PatientDashboardAllergyPresenter;
import org.openmrs.mobile.activities.patientdashboard.charts.PatientChartsFragment;
import org.openmrs.mobile.activities.patientdashboard.charts.PatientDashboardChartsPresenter;
import org.openmrs.mobile.activities.patientdashboard.details.PatientDashboardDetailsPresenter;
import org.openmrs.mobile.activities.patientdashboard.details.PatientDetailsFragment;
import org.openmrs.mobile.activities.patientdashboard.diagnosis.PatientDashboardDiagnosisPresenter;
import org.openmrs.mobile.activities.patientdashboard.diagnosis.PatientDiagnosisFragment;
import org.openmrs.mobile.activities.patientdashboard.visits.PatientDashboardVisitsPresenter;
import org.openmrs.mobile.activities.patientdashboard.visits.PatientVisitsFragment;
import org.openmrs.mobile.activities.patientdashboard.vitals.PatientDashboardVitalsPresenter;
import org.openmrs.mobile.activities.patientdashboard.vitals.PatientVitalsFragment;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.TabUtil;
import org.openmrs.mobile.utilities.ThemeUtils;

public class PatientDashboardActivity extends ACBaseActivity {
    private String mId;
    public PatientDashboardContract.PatientDashboardMainPresenter mPresenter;
    public boolean isActionFABOpen = false;
    public static FloatingActionButton additionalActionsFAB, updateFAB, deleteFAB;
    public LinearLayout deleteFabLayout, updateFabLayout;
    public static Resources resources;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);
        viewPager = findViewById(R.id.pager);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        Bundle patientBundle = savedInstanceState;
        if (null != patientBundle) {
            patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        } else {
            patientBundle = getIntent().getExtras();
        }
        mId = String.valueOf(patientBundle.get(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE));
        initViewPager(new PatientDashboardPagerAdapter(getSupportFragmentManager(), this, mId));

        resources = getResources();
        setupUpdateDeleteActionFAB();
    }

    @Override
    public void onAttachFragment(@NotNull Fragment fragment) {
        attachPresenterToFragment(fragment);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, mId);
    }

    @Override
    public void onConfigurationChanged(@NotNull final Configuration config) {
        super.onConfigurationChanged(config);
        TabUtil.setHasEmbeddedTabs(getSupportActionBar(), getWindowManager(), TabUtil.MIN_SCREEN_WIDTH_FOR_PATIENTDASHBOARDACTIVITY);
    }

    @Override
    public void onBackPressed() {
        if (isActionFABOpen) {
            closeFABMenu();
            animateFAB(true);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.patient_dashboard_menu, menu);
        return true;
    }

    private void initViewPager(PatientDashboardPagerAdapter adapter) {
        TabLayout tabHost = findViewById(R.id.tabhost);
        if(ThemeUtils.isDarkModeActivated()) {
            tabHost.setBackgroundColor(getResources().getColor(R.color.black_dark_mode));
        }
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        viewPager.setAdapter(adapter);
        tabHost.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1) {
                    additionalActionsFAB.hide();
                    additionalActionsFAB.setImageResource(R.drawable.ic_add);
                }
                else {
                    additionalActionsFAB.setImageResource(R.drawable.ic_edit_white_24dp);
                }
                additionalActionsFAB.show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void attachPresenterToFragment(Fragment fragment) {
        Bundle patientBundle = getIntent().getExtras();
        String id = String.valueOf(patientBundle.get(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE));
        if (fragment instanceof PatientDetailsFragment) {
            mPresenter = new PatientDashboardDetailsPresenter(id, ((PatientDetailsFragment) fragment));
        } else if (fragment instanceof PatientDiagnosisFragment) {
            mPresenter = new PatientDashboardDiagnosisPresenter(id, ((PatientDiagnosisFragment) fragment));
        } else if (fragment instanceof PatientVisitsFragment) {
            mPresenter = new PatientDashboardVisitsPresenter(id, ((PatientVisitsFragment) fragment));
        } else if (fragment instanceof PatientVitalsFragment) {
            mPresenter = new PatientDashboardVitalsPresenter(id, ((PatientVitalsFragment) fragment));
        } else if (fragment instanceof PatientChartsFragment) {
            mPresenter = new PatientDashboardChartsPresenter(id, ((PatientChartsFragment) fragment));
        } else if (fragment instanceof PatientAllergyFragment) {
            mPresenter = new PatientDashboardAllergyPresenter(id, ((PatientAllergyFragment) fragment));
        }
    }

    public void setupUpdateDeleteActionFAB() {
        additionalActionsFAB = findViewById(R.id.activity_dashboard_action_fab);
        updateFAB = findViewById(R.id.activity_dashboard_update_fab);
        deleteFAB = findViewById(R.id.activity_dashboard_delete_fab);
        updateFabLayout = findViewById(R.id.custom_fab_update_ll);
        deleteFabLayout = findViewById(R.id.custom_fab_delete_ll);

        additionalActionsFAB.setOnClickListener(v -> {
            int position = viewPager.getCurrentItem();
            if(position == 1) {
                Intent intent = new Intent(this, AddEditAllergyActivity.class);
                intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, mPresenter.getPatientId());
                intent.putExtra(ApplicationConstants.BundleKeys.ALLERGY_UUID, ApplicationConstants.EMPTY_STRING);
                startActivity(intent);
            } else {
                animateFAB(isActionFABOpen);
                if (!isActionFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        deleteFAB.setOnClickListener(v -> showDeletePatientDialog());
        updateFAB.setOnClickListener(v -> startPatientUpdateActivity(mPresenter.getPatientId()));
    }

    @Override
    public void showNoInternetConnectionSnackbar() {
        mSnackbar = Snackbar.make(additionalActionsFAB,
            getString(R.string.no_internet_connection_message), Snackbar.LENGTH_INDEFINITE);
        View sbView = mSnackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        mSnackbar.show();
    }

    public void showFABMenu() {
        isActionFABOpen = true;
        deleteFabLayout.setVisibility(View.VISIBLE);
        updateFabLayout.setVisibility(View.VISIBLE);
        deleteFabLayout.animate().translationY(-resources.getDimension(R.dimen.custom_fab_bottom_margin_55));
        updateFabLayout.animate().translationY(-resources.getDimension(R.dimen.custom_fab_bottom_margin_105));
    }

    public void closeFABMenu() {
        isActionFABOpen = false;
        deleteFabLayout.animate().translationY(0);
        updateFabLayout.animate().translationY(0);
        deleteFabLayout.setVisibility(View.GONE);
        updateFabLayout.setVisibility(View.GONE);
    }

    public void startPatientUpdateActivity(long patientId) {
        Intent updatePatient = new Intent(this, AddEditPatientActivity.class);
        updatePatient.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE,
            String.valueOf(patientId));
        startActivity(updatePatient);
    }

    /**
     * This method is called from other Fragments only when they are visible to the user.
     *
     * @param hide To hide the FAB menu depending on the Fragment visible
     */
    @SuppressLint("RestrictedApi")
    public void hideFABs(boolean hide) {
        closeFABMenu();
        if (hide) {
            additionalActionsFAB.setVisibility(View.GONE);
        } else {
            additionalActionsFAB.setVisibility(View.VISIBLE);

            // will animate back the icon back to its original angle instantaneously
            ObjectAnimator.ofFloat(additionalActionsFAB, "rotation", 180f, 0f).setDuration(0).start();
            additionalActionsFAB.setImageDrawable(resources
                .getDrawable(R.drawable.ic_edit_white_24dp));
        }
    }

    private static void animateFAB(boolean isFABClosed) {
        if (!isFABClosed) {
            ObjectAnimator.ofFloat(additionalActionsFAB, "rotation", 0f, 180f).setDuration(500).start();
            final Handler handler = new Handler();
            handler.postDelayed(() -> additionalActionsFAB.setImageDrawable(resources
                .getDrawable(R.drawable.ic_close_white_24dp)), 400);
        } else {
            ObjectAnimator.ofFloat(additionalActionsFAB, "rotation", 180f, 0f).setDuration(500).start();

            final Handler handler = new Handler();
            handler.postDelayed(() -> additionalActionsFAB.setImageDrawable(resources
                .getDrawable(R.drawable.ic_edit_white_24dp)), 400);
        }
    }
}
