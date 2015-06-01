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

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.FindPatientInDatabaseFragment;
import org.openmrs.mobile.activities.fragments.FindPatientLastViewedFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.TabUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindPatientsActivity extends ACBaseActivity implements ActionBar.TabListener {
    private String mQuery;
    private MenuItem mFindPatientMenuItem;

    private ViewPager mViewPager;
    private FindPatientPagerAdapter mFindPatientPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patients);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        List<TabHost> tabHosts = new ArrayList<TabHost>(Arrays.asList(
                new TabHost(TabHost.LAST_VIEWED_TAB_POS, getString(R.string.find_patient_tab_last_viewed_label)),
                new TabHost(TabHost.DOWNLOADED_TAB_POS, getString(R.string.find_patient_tab_downloaded_label))

        ));

        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
        mFindPatientPagerAdapter = new FindPatientPagerAdapter(getSupportFragmentManager(), tabHosts);
        initViewPager();
        handleIntent(getIntent());
    }

    private void initViewPager() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFindPatientPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (TabHost tabHost : mFindPatientPagerAdapter.getTabHosts()) {
            actionBar.addTab(actionBar.newTab()
                    .setText(tabHost.getTabLabel())
                    .setTabListener(this));
        }
        TabUtil.setHasEmbeddedTabs(actionBar, getWindowManager(), TabUtil.MIN_SCREEN_WIDTH_FOR_FINDPATIENTSACTIVITY);
    }

    @Override
    public void onConfigurationChanged(final Configuration config) {
        super.onConfigurationChanged(config);
        TabUtil.setHasEmbeddedTabs(getSupportActionBar(), getWindowManager(), TabUtil.MIN_SCREEN_WIDTH_FOR_FINDPATIENTSACTIVITY);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (mFindPatientMenuItem != null) {
            MenuItemCompat.collapseActionView(mFindPatientMenuItem);
        }
        super.onBackPressed();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            Intent searchIntent = new Intent(this, FindPatientsSearchActivity.class);
            searchIntent.putExtra(SearchManager.QUERY, mQuery);
            startActivityForResult(searchIntent, 1);
            intent.setAction(null);
            if (mFindPatientMenuItem != null) {
                MenuItemCompat.collapseActionView(mFindPatientMenuItem);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FindPatientLastViewedFragment viewedFragment =
                (FindPatientLastViewedFragment) this.getSupportFragmentManager().
                        getFragments().get(TabHost.LAST_VIEWED_TAB_POS);

        viewedFragment.onResume();

        FindPatientInDatabaseFragment databaseFragment =
                (FindPatientInDatabaseFragment) this.getSupportFragmentManager().
                        getFragments().get(TabHost.DOWNLOADED_TAB_POS);

        databaseFragment.updatePatientsInDatabaseList();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_patients_menu, menu);

        mFindPatientMenuItem = menu.findItem(R.id.actionSearch);
        if (!OpenMRS.getInstance().getOnlineMode()) {
            mFindPatientMenuItem.setEnabled(false);
            mFindPatientMenuItem.getIcon().setAlpha(ApplicationConstants.DISABLED_ICON_ALPHA);
        } else {
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView findPatientView;

            if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
                findPatientView = (SearchView) mFindPatientMenuItem.getActionView();
            } else {
                findPatientView = (SearchView) MenuItemCompat.getActionView(mFindPatientMenuItem);
            }

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            findPatientView.setSearchableInfo(info);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSearch:
                if (!OpenMRS.getInstance().getOnlineMode()) {
                    ToastUtil.showShortToast(this, ToastUtil.ToastType.WARNING, R.string.online_mode_disable);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public class FindPatientPagerAdapter extends FragmentPagerAdapter {
        private List<TabHost> mTabHosts;

        public FindPatientPagerAdapter(FragmentManager fm, List<TabHost> tabHosts) {
            super(fm);
            mTabHosts = tabHosts;
        }

        public List<TabHost> getTabHosts() {
            return mTabHosts;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case TabHost.DOWNLOADED_TAB_POS:
                    return new FindPatientInDatabaseFragment();
                case TabHost.LAST_VIEWED_TAB_POS:
                    return new FindPatientLastViewedFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mTabHosts.size();
        }

    }

    public final class TabHost {
        public static final int LAST_VIEWED_TAB_POS = 0;
        public static final int DOWNLOADED_TAB_POS = 1;

        private Integer mTabPosition;
        private String mTabLabel;

        private TabHost(Integer position, String tabLabel) {
            mTabPosition = position;
            mTabLabel = tabLabel;
        }

        public Integer getTabPosition() {
            return mTabPosition;
        }

        public String getTabLabel() {
            return mTabLabel;
        }
    }

    public enum FragmentMethod {
        StopLoader,
        Update
    }
}
