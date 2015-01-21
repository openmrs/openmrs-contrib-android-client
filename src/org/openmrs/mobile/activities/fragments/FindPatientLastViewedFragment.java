package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.PatientArrayAdapter;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.net.FindPatientsManager;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.NetworkUtils;

import java.util.List;

public class FindPatientLastViewedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ProgressBar mSpinner;
    private View fragmentLayout;
    private TextView mEmptyList;
    private ListView mPatientsListView;
    private PatientArrayAdapter mAdapter;
    private static List<Patient> mLastViewedPatientsList;
    private SwipeRefreshLayout swipeLayout;
    private static boolean mRefreshing;

    public FindPatientLastViewedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mRefreshing) {
            swipeLayout.setRefreshing(true);
            swipeLayout.setEnabled(false);
            mEmptyList.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mSpinner);
        } else if (mLastViewedPatientsList != null) {
            updatePatientsData();
        } else {
            updateLastViewedList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLayout = inflater.inflate(R.layout.fragment_last_viewed_patients, null, false);
        mEmptyList = (TextView) fragmentLayout.findViewById(R.id.emptyPatientListView);
        mPatientsListView = (ListView) fragmentLayout.findViewById(R.id.patientListView);
        mSpinner = (ProgressBar) fragmentLayout.findViewById(R.id.patientListViewLoading);

        swipeLayout = (SwipeRefreshLayout) fragmentLayout.findViewById(R.id.swipe_container);
        swipeLayout.setEnabled(false);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.light_teal,
                R.color.green,
                R.color.yellow,
                R.color.light_red);

        mPatientsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 && !swipeLayout.isRefreshing()) {
                    swipeLayout.setEnabled(true);
                } else {
                    swipeLayout.setEnabled(false);
                }
            }
        });

        FontsUtil.setFont((ViewGroup) fragmentLayout);
        return fragmentLayout;
    }

    public void updatePatientsData() {
        if (mLastViewedPatientsList.size() == 0) {
            mEmptyList.setText(getString(R.string.find_patient_no_last_viewed));
            mSpinner.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mEmptyList);
        }
        mAdapter = new PatientArrayAdapter(getActivity(), R.layout.find_patients_row, mLastViewedPatientsList);
        mPatientsListView.setAdapter(mAdapter);
        swipeLayout.setRefreshing(false);
        swipeLayout.setEnabled(true);
    }

    public void stopLoader() {
        mEmptyList.setText(getString(R.string.find_patient_no_last_viewed));
        mSpinner.setVisibility(View.GONE);
        mPatientsListView.setEmptyView(mEmptyList);
        swipeLayout.setRefreshing(false);
        swipeLayout.setEnabled(true);
    }

    public void updateLastViewedList() {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            setRefreshing(true);
            swipeLayout.setRefreshing(true);
            swipeLayout.setEnabled(false);
            mEmptyList.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mSpinner);
            if (mAdapter != null) {
                mAdapter.clear();
            }
            FindPatientsManager fpm = new FindPatientsManager(getActivity());
            fpm.getLastViewedPatient();
        } else {
            mEmptyList.setText(getString(R.string.find_patient_no_connection));
            mPatientsListView.setEmptyView(mEmptyList);
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        if (swipeLayout.isEnabled()) {
            updateLastViewedList();
        }
    }

    public static void clearLastViewedPatientList() {
        mLastViewedPatientsList = null;
    }

    public static void setLastViewedPatientList(List<Patient> patientsList) {
        mLastViewedPatientsList = patientsList;
    }

    public static void setRefreshing(boolean refresh) {
        mRefreshing = refresh;
    }
}

