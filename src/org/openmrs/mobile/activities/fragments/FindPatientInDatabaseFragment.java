package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.PatientArrayAdapter;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class FindPatientInDatabaseFragment extends ACBaseFragment {

    private View fragmentLayout;
    private ListView visitList;

    public FindPatientInDatabaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLayout = inflater.inflate(R.layout.fragment_find_patients, null, false);
        visitList = (ListView) fragmentLayout.findViewById(R.id.patientListView);

        TextView emptyList = (TextView) fragmentLayout.findViewById(R.id.emptyPatientListView);
        emptyList.setText(getString(R.string.search_patient_no_results));
        visitList.setEmptyView(emptyList);

        updatePatientsInDatabaseList();

        FontsUtil.setFont((ViewGroup) fragmentLayout);
        return fragmentLayout;
    }

    public void updatePatientsInDatabaseList() {
        List<Patient> mPatientList = new PatientDAO().getAllPatients();
        visitList.setAdapter(new PatientArrayAdapter(getActivity(), R.layout.find_patients_row, mPatientList));
    }

}

