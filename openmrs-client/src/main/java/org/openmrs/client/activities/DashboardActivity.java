package org.openmrs.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.client.R;
import org.openmrs.client.adapters.ScreenSlidePagerAdapter;
import org.openmrs.client.net.AuthorizationManager;

public class DashboardActivity extends ACBaseActivity {

    private AuthorizationManager mAuthorizationManager;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuthorizationManager = new AuthorizationManager(getApplicationContext());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        initializeSQLCipher();
    }

    private void initializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mAuthorizationManager.isUserLoggedIn()) {
            mAuthorizationManager.moveToLoginActivity();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
