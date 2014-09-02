package org.openmrs.client.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.bundle.PatientDashboardBundle;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.DateUtils;

public class PatientDetailsFragment extends Fragment {

    private PatientDashboardBundle mPatientBundle;

    public PatientDetailsFragment() {
    }

    public static PatientDetailsFragment newInstance(PatientDashboardBundle patientDashboardBundle) {
        PatientDetailsFragment detailsFragment = new PatientDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE, patientDashboardBundle);
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPatientBundle = (PatientDashboardBundle) getArguments().getSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_details, null, false);
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsDisplayName)).setText(mPatientBundle.getPatient().getDisplay());
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsIdentifier)).setText(mPatientBundle.getPatient().getIdentifier());
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsGender)).setText(mPatientBundle.getPatient().getGender());
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsBirthDate)).setText(DateUtils.convertTime(mPatientBundle.getPatient().getBirthDate()));
        ((TextView) fragmentLayout.findViewById(R.id.addressDetailsStreet)).setText(mPatientBundle.getPatient().getAddress().toString());
        ((TextView) fragmentLayout.findViewById(R.id.addressDetailsPostalCode)).setText(mPatientBundle.getPatient().getAddress().getPostalCode());
        ((TextView) fragmentLayout.findViewById(R.id.addressDetailsCity)).setText(mPatientBundle.getPatient().getAddress().getCityVillage());
        ((TextView) fragmentLayout.findViewById(R.id.addressDetailsState)).setText(mPatientBundle.getPatient().getAddress().getState());
        ((TextView) fragmentLayout.findViewById(R.id.addressDetailsCountry)).setText(mPatientBundle.getPatient().getAddress().getCountry());
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsPhone)).setText(mPatientBundle.getPatient().getPhoneNumber());
        return fragmentLayout;
    }
}
