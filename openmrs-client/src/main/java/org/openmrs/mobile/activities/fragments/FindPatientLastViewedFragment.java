/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.FindPatientsActivity;
import org.openmrs.mobile.adapters.PatientRecyclerViewAdapter;
import org.openmrs.mobile.listeners.findPatients.LastViewedPatientListener;
import org.openmrs.mobile.net.FindPatientsManager;
import org.openmrs.mobile.net.helpers.FindPatientsHelper;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.NetworkUtils;
import java.util.List;

public class FindPatientLastViewedFragment extends ACBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ProgressBar mSpinner;
    private View mFragmentLayout;
    private TextView mEmptyList;
    private RecyclerView patientsRecyclerView;
    private PatientRecyclerViewAdapter mAdapter;
    private static List<Patient> mLastViewedPatientsList;
    private SwipeRefreshLayout mSwipeLayout;
    private static boolean mRefreshing;
    private boolean mIsConnectionAvailable;
    private LastViewedPatientListener mFpmResponseListener;

    public static final int FIND_PATIENT_LAST_VIEWED_FM_ID = 0;

    public FindPatientLastViewedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsConnectionAvailable = checkIfConnectionIsAvailable();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mRefreshing) {
            mSwipeLayout.setRefreshing(true);
            mSwipeLayout.setEnabled(false);
            mEmptyList.setVisibility(View.GONE);
            patientsRecyclerView.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
        } else if (mLastViewedPatientsList != null) {
            if (mIsConnectionAvailable) {
                    FindPatientsManager fpm = new FindPatientsManager();
                    fpm.getLastViewedPatient(mFpmResponseListener);
            }
            else {
                updatePatientsData();
            }
        } else {
            if (mIsConnectionAvailable) {
                FindPatientsManager fpm = new FindPatientsManager();
                fpm.getLastViewedPatient(mFpmResponseListener);
            }
            else {
                updateLastViewedList();
            }
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
        mEmptyList = (TextView) mFragmentLayout.findViewById(R.id.emptyPatientList);
        mEmptyList.setVisibility(View.VISIBLE);

        patientsRecyclerView = (RecyclerView) mFragmentLayout.findViewById(R.id.patientRecyclerView);
        patientsRecyclerView.setVisibility(View.GONE);
        patientsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        patientsRecyclerView.setLayoutManager(linearLayoutManager);

        mSpinner = (ProgressBar) mFragmentLayout.findViewById(R.id.patientRecyclerViewLoading);

        mSwipeLayout = (SwipeRefreshLayout) mFragmentLayout.findViewById(R.id.swipe_container);
        mSwipeLayout.setEnabled(false);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.light_teal,
                R.color.green,
                R.color.yellow,
                R.color.light_red);

        patientsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,
                                   int dy) {
                if (dy > 0 && !mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setEnabled(true);
                } else {
                    mSwipeLayout.setEnabled(false);
                }
            }
        });

        FontsUtil.setFont((ViewGroup) mFragmentLayout);
        //registerForContextMenu(mPatientsListView);
        return mFragmentLayout;
    }

    public void updatePatientsData() {
        if (mLastViewedPatientsList.isEmpty()) {
            mEmptyList.setText(getString(R.string.find_patient_no_last_viewed));
            mSpinner.setVisibility(View.GONE);
            patientsRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
        }
        else {
            mAdapter = new PatientRecyclerViewAdapter(getActivity(), mLastViewedPatientsList, FIND_PATIENT_LAST_VIEWED_FM_ID);
            patientsRecyclerView.setAdapter(mAdapter);
            patientsRecyclerView.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.GONE);
            mSwipeLayout.setRefreshing(false);
            mSwipeLayout.setEnabled(true);
        }
    }

    public void stopLoader() {
        mEmptyList.setText(getString(R.string.find_patient_no_last_viewed));
        mSpinner.setVisibility(View.GONE);
        patientsRecyclerView.setVisibility(View.GONE);
        mEmptyList.setVisibility(View.VISIBLE);
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(true);
    }


    public void updateLastViewedList() {
        if (NetworkUtils.isOnline()) {
            setRefreshing(true);
            mSwipeLayout.setRefreshing(true);
            mSwipeLayout.setEnabled(false);
            mEmptyList.setVisibility(View.GONE);
            patientsRecyclerView.setVisibility(View.GONE);
            mSpinner.setVisibility(View.GONE);
            if (mAdapter != null) {
                mAdapter.clear();
            }
            FindPatientsManager fpm = new FindPatientsManager();
            fpm.getLastViewedPatient(mFpmResponseListener);
        } else {
            mEmptyList.setText(getString(R.string.find_patient_no_connection));
            patientsRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
            mSwipeLayout.setRefreshing(false);
        }
    }

    public boolean checkIfConnectionIsAvailable() {
        return NetworkUtils.isOnline();
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

