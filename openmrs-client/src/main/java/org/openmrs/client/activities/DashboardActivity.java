package org.openmrs.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

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

        Button findPatientButton = (Button) this.findViewById(R.id.button_find_patient);
        findPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFindPatients();
            }
        });
    }

    private void openFindPatients() {
        Intent i = new Intent(this, FindPatientsActivity.class);
        startActivity(i);
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
