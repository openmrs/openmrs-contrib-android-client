package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.FontsUtil;

public class PatientDetailsFragment extends ACBaseFragment {

    private Patient mPatient;

    public PatientDetailsFragment() {
    }

    public static PatientDetailsFragment newInstance(Patient patient) {
        PatientDetailsFragment detailsFragment = new PatientDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE, patient);
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPatient = (Patient) getArguments().getSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.patient_details_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSynchronize:
                ((PatientDashboardActivity)getActivity()).synchronizePatient();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_details, null, false);

        if (("M").equals(mPatient.getPerson().getGender())) {
            ((TextView) fragmentLayout.findViewById(R.id.patientDetailsGender)).setText("Male");
        } else {
            ((TextView) fragmentLayout.findViewById(R.id.patientDetailsGender)).setText("Female");
        }
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsBirthDate)).setText(DateUtils.convertTime(DateUtils.convertTime(mPatient.getPerson().getBirthdate())));
        if (null != mPatient.getPerson().getAddress()) {
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.addressLayout), R.id.addressDetailsStreet, mPatient.getPerson().getAddress().getAddressString());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.stateLayout), R.id.addressDetailsState, mPatient.getPerson().getAddress().getStateProvince());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.countryLayout), R.id.addressDetailsCountry, mPatient.getPerson().getAddress().getCountry());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.postalCodeLayout), R.id.addressDetailsPostalCode, mPatient.getPerson().getAddress().getPostalCode());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.cityLayout), R.id.addressDetailsCity, mPatient.getPerson().getAddress().getCityVillage());
        }

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

    public void reloadPatientData(Patient patient) {
        mPatient = patient;
    }

}
