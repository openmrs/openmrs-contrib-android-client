/*Circular Viewpager indicator code obtained from:
http://www.androprogrammer.com/2015/06/view-pager-with-circular-indicator.html*/

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.activities.formdisplay;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.openmrs.android_sdk.library.models.Form;
import com.openmrs.android_sdk.library.models.Page;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.FormService;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.bundle.FormFieldsWrapper;
import org.openmrs.mobile.databinding.ActivityFormDisplayBinding;

import java.util.ArrayList;
import java.util.List;

public class FormDisplayActivity extends ACBaseActivity implements FormDisplayContract.View.MainView {
    private ActivityFormDisplayBinding binding = null;
    private ViewPager viewPager;
    private Button nextButton, finishButton;
    private int mDotsCount;
    private ImageView[] mDots;
    private FormDisplayContract.Presenter.MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle bundle = getIntent().getExtras();
        String valuereference = null;
        if (bundle != null) {
            valuereference = (String) bundle.get(ApplicationConstants.BundleKeys.VALUEREFERENCE);
            String formName = (String) bundle.get(ApplicationConstants.BundleKeys.FORM_NAME);
            getSupportActionBar().setTitle(formName + " Form");
        }

        initViewComponents(valuereference);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void onAttachFragment(@NotNull Fragment fragment) {
        attachPresenterToFragment(fragment);
        super.onAttachFragment(fragment);
    }

    private void attachPresenterToFragment(Fragment fragment) {
        if (fragment instanceof FormDisplayPageFragment) {
            Bundle bundle = getIntent().getExtras();
            String valueRef = null;
            ArrayList<FormFieldsWrapper> formFieldsWrappers = null;
            if (bundle != null) {
                valueRef = (String) bundle.get(ApplicationConstants.BundleKeys.VALUEREFERENCE);
                formFieldsWrappers = bundle.getParcelableArrayList(ApplicationConstants.BundleKeys.FORM_FIELDS_LIST_BUNDLE);
            }
            Form form = FormService.getForm(valueRef);
            List<Page> pageList = form.getPages();
            for (Page page : pageList) {
                if (formFieldsWrappers != null) {
                    new FormDisplayPagePresenter((FormDisplayPageFragment) fragment, page, formFieldsWrappers.get(pageList.indexOf(page)));
                } else {
                    new FormDisplayPagePresenter((FormDisplayPageFragment) fragment, pageList.get(getFragmentNumber(fragment)));
                }
            }
        }
    }

    @Override
    public void quitFormEntry() {
        finish();
    }

    @Override
    public void setPresenter(FormDisplayContract.Presenter.MainPresenter presenter) {
        this.presenter = presenter;
    }

    private void initViewComponents(String valueRef) {
        FormPageAdapter formPageAdapter = new FormPageAdapter(getSupportFragmentManager(), valueRef);
        LinearLayout pagerIndicator = binding.viewPagerCountDots;

        nextButton = binding.btnNext;
        finishButton = binding.btnFinish;

        nextButton.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));
        finishButton.setOnClickListener(view -> presenter.createEncounter());
        viewPager = binding.container;

        viewPager.setAdapter(formPageAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mDotsCount; i++) {
                    mDots[i].setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.nonselecteditem_dot));
                }
                mDots[position].setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.selecteditem_dot));

                if (position + 1 == mDotsCount) {
                    nextButton.setVisibility(View.GONE);
                    finishButton.setVisibility(View.VISIBLE);
                } else {
                    nextButton.setVisibility(View.VISIBLE);
                    finishButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // This method is intentionally empty
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // This method is intentionally empty
            }
        });

        presenter = new FormDisplayMainPresenter(this, getIntent().getExtras(), (FormPageAdapter) viewPager.getAdapter());

        // Set page indicators:
        mDotsCount = formPageAdapter.getCount();
        mDots = new ImageView[mDotsCount];
        for (int i = 0; i < mDotsCount; i++) {
            mDots[i] = new ImageView(this);
            mDots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

            pagerIndicator.addView(mDots[i], params);
        }
        mDots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selecteditem_dot));
        if (mDotsCount == 1) {
            nextButton.setVisibility(View.GONE);
            finishButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void enableSubmitButton(boolean enabled) {
        finishButton.setEnabled(enabled);
    }

    @Override
    public void showToast(String errorMessage) {
        ToastUtil.error(errorMessage);
    }

    @Override
    public void showToast() {
        ToastUtil.error(getString(R.string.form_data_will_be_synced_later_error_message));
    }

    @Override
    public void showSuccessfulToast() {
        ToastUtil.success(getString(R.string.form_submitted_successfully));
    }

    private int getFragmentNumber(Fragment fragment) {
        String fragmentTag = fragment.getTag();
        String[] parts = fragmentTag.split(":");
        return Integer.parseInt(parts[3]);
    }
}
