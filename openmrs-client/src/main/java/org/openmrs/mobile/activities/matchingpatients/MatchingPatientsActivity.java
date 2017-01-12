package org.openmrs.mobile.activities.matchingpatients;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.PatientAndMatchesWrapper;
import org.openmrs.mobile.utilities.ToastUtil;

public class MatchingPatientsActivity extends ACBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_patients);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.matching_patients_toolbar_tittle));
            setSupportActionBar(toolbar);
        }

        // Create fragment
        MatchingPatientsFragment matchingPatientsFragment =
                (MatchingPatientsFragment) getSupportFragmentManager().findFragmentById(R.id.matchingPatientsContentFrame);
        if (matchingPatientsFragment == null) {
            matchingPatientsFragment = MatchingPatientsFragment.newInstance();
        }
        if (!matchingPatientsFragment.isAdded()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    matchingPatientsFragment, R.id.matchingPatientsContentFrame);
        }

        if (getIntent().getExtras().getBoolean(ApplicationConstants.BundleKeys.CALCULATED_LOCALLY, false)) {
            showToast(getString(R.string.registration_core_info));
        }

        PatientAndMatchesWrapper patientAndMatchesWrapper = (PatientAndMatchesWrapper) getIntent().getSerializableExtra(ApplicationConstants.BundleKeys.PATIENTS_AND_MATCHES);

        // Create the presenter
        new MatchingPatientsPresenter(matchingPatientsFragment, patientAndMatchesWrapper.getMatchingPatients());
    }

    private void showToast(String message) {
        ToastUtil.notifyLong(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sync", false);
        editor.commit();
    }

}
