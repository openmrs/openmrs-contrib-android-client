package org.openmrs.mobile.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.FindPatientsActivity;
import org.openmrs.mobile.listeners.findPatients.LastViewedPatientListener;
import org.openmrs.mobile.adapters.PatientArrayAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.net.FindPatientsManager;
import org.openmrs.mobile.net.helpers.FindPatientsHelper;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.NetworkUtils;
import java.util.List;

public class FindPatientLastViewedFragment extends ACBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ProgressBar mSpinner;
    private View mFragmentLayout;
    private TextView mEmptyList;
    private ListView mPatientsListView;
    private PatientArrayAdapter mAdapter;
    private static List<Patient> mLastViewedPatientsList;
    private SwipeRefreshLayout mSwipeLayout;
    private static boolean mRefreshing;
    private LastViewedPatientListener mFpmResponseListener;

    public FindPatientLastViewedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!OpenMRS.getInstance().getOnlineMode()) {
            setEmptyList(R.string.online_mode_disable);
        } else if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            setEmptyList(R.string.no_connection_available);
        } else if (mRefreshing) {
            setSpinner();
        } else if (mLastViewedPatientsList != null) {
            updatePatientsData();
        } else {
            updateLastViewedList();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFpmResponseListener = FindPatientsHelper.createLastViewedPatientListener((FindPatientsActivity) activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentLayout = inflater.inflate(R.layout.fragment_last_viewed_patients, null, false);
        mEmptyList = (TextView) mFragmentLayout.findViewById(R.id.emptyPatientListView);
        mPatientsListView = (ListView) mFragmentLayout.findViewById(R.id.patientListView);
        mSpinner = (ProgressBar) mFragmentLayout.findViewById(R.id.patientListViewLoading);

        mSwipeLayout = (SwipeRefreshLayout) mFragmentLayout.findViewById(R.id.swipe_container);
        mSwipeLayout.setEnabled(false);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.light_teal,
                R.color.green,
                R.color.yellow,
                R.color.light_red);

        mPatientsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 && !mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setEnabled(true);
                } else {
                    mSwipeLayout.setEnabled(false);
                }
            }
        });

        FontsUtil.setFont((ViewGroup) mFragmentLayout);
        return mFragmentLayout;
    }

    public void updatePatientsData() {
        if (mLastViewedPatientsList.size() == 0) {
            setEmptyList(R.string.find_patient_no_last_viewed);
        }
        mAdapter = new PatientArrayAdapter(getActivity(), R.layout.find_patients_row, mLastViewedPatientsList);
        mPatientsListView.setAdapter(mAdapter);
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(true);
    }

    public void stopLoader() {
        setEmptyList(R.string.find_patient_no_last_viewed);
    }

    public void updateLastViewedList() {
        if (OpenMRS.getInstance().getOnlineMode()) {
            setRefreshing(true);
            mSwipeLayout.setRefreshing(true);
            mSwipeLayout.setEnabled(false);
            mEmptyList.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mSpinner);
            if (mAdapter != null) {
                mAdapter.clear();
            }
            FindPatientsManager fpm = new FindPatientsManager();
            fpm.getLastViewedPatient(mFpmResponseListener);
        } else {
            mEmptyList.setText(getString(R.string.no_connection));
            mPatientsListView.setEmptyView(mEmptyList);
            mSwipeLayout.setRefreshing(false);
        }
    }

    private void setEmptyList(int resId) {
        mSpinner.setVisibility(View.GONE);
        mEmptyList.setText(getString(resId));
        mPatientsListView.setEmptyView(mEmptyList);
        mPatientsListView.setAdapter(null);
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(true);
    }

    private void setSpinner() {
        mEmptyList.setVisibility(View.GONE);
        mPatientsListView.setEmptyView(mSpinner);
        mSwipeLayout.setRefreshing(true);
        mSwipeLayout.setEnabled(false);
    }

    @Override
    public void onRefresh() {
        if (mSwipeLayout.isEnabled()) {
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

