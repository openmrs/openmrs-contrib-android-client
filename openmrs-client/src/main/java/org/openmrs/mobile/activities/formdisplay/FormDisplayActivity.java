/*Circular Viewpager indicator code obtained from:
http://www.androprogrammer.com/2015/06/view-pager-with-circular-indicator.html*/

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.activities.formdisplay;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.bundle.FormFieldsWrapper;
import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.Page;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FormService;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class FormDisplayActivity extends ACBaseActivity implements FormDisplayContract.View.MainView {

    private ViewPager mViewPager;
    private Button mBtnNext, mBtnFinish;
    private int mDotsCount;
    private ImageView[] mDots;

    private FormDisplayContract.Presenter.MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        String valuereference = null;
        if(bundle!=null) {
            valuereference = (String)bundle.get(ApplicationConstants.BundleKeys.VALUEREFERENCE);
            String formName = (String) bundle.get(ApplicationConstants.BundleKeys.FORM_NAME);
            getSupportActionBar().setTitle(formName + " Form");
        }
        mPresenter = new FormDisplayMainPresenter(this, bundle);

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
        mPresenter.start();
    }

    @Override
    public void onAttachFragment (Fragment fragment) {
        mPresenter = new FormDisplayMainPresenter(this, getIntent().getExtras());
        mPresenter.addFragment(fragment);
        attachPresenterToFragment(fragment);
    }

    private void attachPresenterToFragment(Fragment fragment) {
        if (fragment instanceof FormDisplayPageFragment) {
            Bundle bundle = getIntent().getExtras();
            String valueRef = null;
            ArrayList<FormFieldsWrapper> formFieldsWrappers = null;
            if(bundle!=null) {
                valueRef = (String)bundle.get(ApplicationConstants.BundleKeys.VALUEREFERENCE);
                formFieldsWrappers = bundle.getParcelableArrayList(ApplicationConstants.BundleKeys.FORM_FIELDS_LIST_BUNDLE);
            }
            Form form = FormService.getForm(valueRef);
            List<Page> pageList = form.getPages();
            for (Page page : pageList) {
                if(formFieldsWrappers != null){
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
    public void setPresenter(FormDisplayContract.Presenter presenter) {
        this.mPresenter = ((FormDisplayContract.Presenter.MainPresenter) presenter);
    }

    private void initViewComponents(String valueRef) {
        FormPageAdapter formPageAdapter = new FormPageAdapter(getFragmentManager(), valueRef);
        LinearLayout pagerIndicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        mBtnNext = (Button) findViewById(R.id.btn_next);
        mBtnFinish = (Button) findViewById(R.id.btn_finish);

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            }
        });
        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.createEncounter();
            }
        });
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(formPageAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mDotsCount; i++) {
                    mDots[i].setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.nonselecteditem_dot));
                }
                mDots[position].setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.selecteditem_dot));

                if (position + 1 == mDotsCount) {
                    mBtnNext.setVisibility(View.GONE);
                    mBtnFinish.setVisibility(View.VISIBLE);
                } else {
                    mBtnNext.setVisibility(View.VISIBLE);
                    mBtnFinish.setVisibility(View.GONE);
                }
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

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
        if(mDotsCount ==1) {
            mBtnNext.setVisibility(View.GONE);
            mBtnFinish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void enableSubmitButton(boolean enabled) {
        mBtnFinish.setEnabled(enabled);
    }

    @Override
    public void showToast(String errorMessage) {
        ToastUtil.error(errorMessage);
    }

    private int getFragmentNumber(Fragment fragment) {
        String fragmentTag = fragment.getTag();
        String[] parts = fragmentTag.split(":");
        return Integer.parseInt(parts[3]);
    }

}
