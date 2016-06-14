package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.PatientRecyclerViewAdapter;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class FindPatientInDatabaseFragment extends ACBaseFragment {

    private View fragmentLayout;
    private RecyclerView patientRecyclerView;
    private TextView emptyList;
    public final static int FIND_PATIENT_IN_DB_FM_ID = 1;

    public FindPatientInDatabaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePatientsInDatabaseList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLayout = inflater.inflate(R.layout.fragment_find_patients, null, false);

        patientRecyclerView = (RecyclerView) fragmentLayout.findViewById(R.id.patientRecyclerView);
        patientRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        patientRecyclerView.setLayoutManager(linearLayoutManager);

        emptyList = (TextView) fragmentLayout.findViewById(R.id.emptyPatientList);
        emptyList.setText(getString(R.string.search_patient_no_results));
        emptyList.setVisibility(View.VISIBLE);
        patientRecyclerView.setVisibility(View.GONE);

        updatePatientsInDatabaseList();
        FontsUtil.setFont((ViewGroup) fragmentLayout);
        return fragmentLayout;
    }

    public void updatePatientsInDatabaseList() {
        List<Patient> mPatientList = new PatientDAO().getAllPatients();
        if (mPatientList.isEmpty()){
            patientRecyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }
        else {
            patientRecyclerView.setAdapter(new PatientRecyclerViewAdapter(getActivity(),
                    mPatientList, FIND_PATIENT_IN_DB_FM_ID));
            patientRecyclerView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        }
    }

}

