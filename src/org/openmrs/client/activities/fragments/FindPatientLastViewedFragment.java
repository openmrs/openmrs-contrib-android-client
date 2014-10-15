package org.openmrs.client.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.PatientArrayAdapter;
import org.openmrs.client.models.Patient;
import org.openmrs.client.net.FindPatientsManager;
import org.openmrs.client.utilities.FontsUtil;
import org.openmrs.client.utilities.NetworkUtils;
import org.openmrs.client.utilities.PatientCacheHelper;

import java.util.List;

public class FindPatientLastViewedFragment extends Fragment {

    private ProgressBar mSpinner;
    private View fragmentLayout;
    private static int lastSearchId;
    private TextView mEmptyList;
    private ListView mPatientsListView;
    private static PatientArrayAdapter mAdapter;

    public FindPatientLastViewedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLayout = inflater.inflate(R.layout.fragment_find_patients, null, false);
        mEmptyList = (TextView) fragmentLayout.findViewById(R.id.emptyPatientListView);
        mPatientsListView = (ListView) fragmentLayout.findViewById(R.id.patientListView);
        mSpinner = (ProgressBar) fragmentLayout.findViewById(R.id.patientListViewLoading);

        updateLastViewedList();
        FontsUtil.setFont((ViewGroup) fragmentLayout);
        return fragmentLayout;
    }

    public void updatePatientsData(int searchId) {
        if (lastSearchId == searchId) {
            List<Patient> patientsList = PatientCacheHelper.getCachedPatients();
            if (patientsList.size() == 0) {
                mEmptyList.setText(getString(R.string.find_patient_no_last_viewed));
                mSpinner.setVisibility(View.GONE);
                mPatientsListView.setEmptyView(mEmptyList);
            }
            mAdapter = new PatientArrayAdapter(getActivity(), R.layout.find_patients_row, patientsList);
            mPatientsListView.setAdapter(mAdapter);
        }
    }

    public void stopLoader(int searchId) {
        if (lastSearchId == searchId) {
            mEmptyList.setText(getString(R.string.find_patient_no_last_viewed));
            mSpinner.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mEmptyList);
        }
    }

    public void updateLastViewedList() {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            mEmptyList.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mSpinner);
            if (mAdapter != null) {
                mAdapter.clear();
            }
            lastSearchId++;
            PatientCacheHelper.clearCache();
            PatientCacheHelper.setId(lastSearchId);
            FindPatientsManager fpm = new FindPatientsManager(getActivity());
            fpm.getLastViewedPatient(lastSearchId);
        } else {
            mEmptyList.setText(getString(R.string.find_patient_no_connection));
            mPatientsListView.setEmptyView(mEmptyList);
        }
    }
}

