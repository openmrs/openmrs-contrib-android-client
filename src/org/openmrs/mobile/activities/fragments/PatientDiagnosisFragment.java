package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.R;

import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;

public class PatientDiagnosisFragment extends ACBaseFragment {

    public PatientDiagnosisFragment() {
    }

    public static PatientDiagnosisFragment newInstance(Long patientID) {
        PatientDiagnosisFragment detailsFragment = new PatientDiagnosisFragment();
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
        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_diagnosis, null, false);
        FontsUtil.setFont(fragmentLayout, FontsUtil.OpenFonts.OPEN_SANS_SEMIBOLD);
        return fragmentLayout;
    }

}
