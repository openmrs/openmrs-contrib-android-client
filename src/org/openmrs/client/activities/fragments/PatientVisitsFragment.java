package org.openmrs.client.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.PatientVisitsArrayAdapter;
import org.openmrs.client.bundle.PatientDashboardBundle;
import org.openmrs.client.utilities.ApplicationConstants;

public class PatientVisitsFragment extends Fragment {

    private PatientDashboardBundle mPatientBundle;

    public PatientVisitsFragment() {
    }

    public static PatientVisitsFragment newInstance(PatientDashboardBundle patientDashboardBundle) {
        PatientVisitsFragment detailsFragment = new PatientVisitsFragment();
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
        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_visit, null, false);
        ListView visitList = (ListView) fragmentLayout.findViewById(R.id.patientVisitList);
        visitList.setAdapter(new PatientVisitsArrayAdapter(getActivity(), mPatientBundle.getPatientVisits()));
        return fragmentLayout;
    }
}
