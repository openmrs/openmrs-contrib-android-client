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
import org.openmrs.client.utilities.StringUtils;
import org.openmrs.client.utilities.FontsUtil;

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
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsIdentifier)).setText("#" + mPatientBundle.getPatient().getIdentifier());
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsGender)).setText(mPatientBundle.getPatient().getGender());
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsBirthDate)).setText(DateUtils.convertTime(mPatientBundle.getPatient().getBirthDate()));
        if (null != mPatientBundle.getPatient().getAddress()) {
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.addressLayout), R.id.addressDetailsStreet, mPatientBundle.getPatient().getAddress().toString());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.stateLayout), R.id.addressDetailsState, mPatientBundle.getPatient().getAddress().getState());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.countryLayout), R.id.addressDetailsCountry, mPatientBundle.getPatient().getAddress().getCountry());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.postalCodeLayout), R.id.addressDetailsPostalCode, mPatientBundle.getPatient().getAddress().getPostalCode());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.cityLayout), R.id.addressDetailsCity, mPatientBundle.getPatient().getAddress().getCityVillage());
        }
        showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.phoneNumberLayout), R.id.patientDetailsPhone, mPatientBundle.getPatient().getPhoneNumber());

        FontsUtil.setFont((ViewGroup) fragmentLayout);
        return fragmentLayout;
    }

    private void showAddressDetailsViewElement(View detailsLayout, int detailsViewId, String detailsText) {
        if (StringUtils.notNull(detailsText) && StringUtils.notEmpty(detailsText)) {
            ((TextView) detailsLayout.findViewById(detailsViewId)).setText(detailsText);
        } else {
            detailsLayout.setVisibility(View.GONE);
        }
    }
}
