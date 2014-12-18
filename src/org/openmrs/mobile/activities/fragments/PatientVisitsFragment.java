package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.PatientVisitsArrayAdapter;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class PatientVisitsFragment extends Fragment {

    private List<Visit> mPatientVisits;

    public PatientVisitsFragment() {
    }

    public static PatientVisitsFragment newInstance(Long patientID) {
        PatientVisitsFragment detailsFragment = new PatientVisitsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE, patientID);
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPatientVisits = new VisitDAO().getVisitsByPatientID(getArguments().getLong(ApplicationConstants.BundleKeys.PATIENT_BUNDLE));

        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_visit, null, false);
        ListView visitList = (ListView) fragmentLayout.findViewById(R.id.patientVisitList);
        visitList.setAdapter(new PatientVisitsArrayAdapter(getActivity(), mPatientVisits));
        return fragmentLayout;
    }
}
