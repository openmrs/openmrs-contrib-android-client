package org.openmrs.mobile.activities.editpatient;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.utilities.ApplicationConstants;

import static org.openmrs.mobile.R.id.editPatientContentFrame;

public class EditPatientActivity extends ACBaseActivity{

    public EditPatientContract.Presenter mPresenter;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_edit_patient);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        EditPatientFragment editPatientFragment =
                (EditPatientFragment) getSupportFragmentManager().findFragmentById(R.id.editPatientContentFrame);
        if (editPatientFragment == null) {
            editPatientFragment = EditPatientFragment.newInstance();
        }
        if (!editPatientFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    editPatientFragment, editPatientContentFrame);
        }

        Bundle patientBundle = savedInstanceState;
        if (null != patientBundle) {
            patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        } else {
            patientBundle = getIntent().getExtras();
        }
        mId = String.valueOf(patientBundle.get(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE));


        // Create the mPresenter
        mPresenter = new EditPatientPresenter(mId, editPatientFragment);
    }

}
