package org.openmrs.client.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
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

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.FindPatientInDatabaseFragment;
import org.openmrs.client.activities.fragments.FindPatientLastViewedFragment;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.utilities.FontsUtil;

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
                new TabHost(TabHost.IN_DATABASE_TAB_POS, getString(R.string.find_patient_tab_in_database_label)),
                new TabHost(TabHost.LAST_VIEWED_TAB_POS, getString(R.string.find_patient_tab_last_viewed_label))
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

        viewedFragment.updateLastViewedList();

        FindPatientInDatabaseFragment databaseFragment =
                (FindPatientInDatabaseFragment) this.getSupportFragmentManager().
                        getFragments().get(TabHost.IN_DATABASE_TAB_POS);

        databaseFragment.updatePatientsInDatabaseList();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_patients_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView findPatientView;

        mFindPatientMenuItem = menu.findItem(R.id.action_search);
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findPatientView = (SearchView) mFindPatientMenuItem.getActionView();
        } else {
            findPatientView = (SearchView) MenuItemCompat.getActionView(mFindPatientMenuItem);
        }

        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        findPatientView.setSearchableInfo(info);
        return true;
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
                case TabHost.IN_DATABASE_TAB_POS:
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
        public static final int IN_DATABASE_TAB_POS = 0;
        public static final int LAST_VIEWED_TAB_POS = 1;

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
}
