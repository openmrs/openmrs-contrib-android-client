
/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.dailyappointments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.appointment.Result;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyAppointmentsFragment extends ACBaseFragment<DailyAppointmentsContract.Presenter> implements DailyAppointmentsContract.View {
    private RecyclerView mDailyAppointmentsRecyclerView;
    private TextView mEmptyList;

    //Initialization Progress bar
    private ProgressBar mProgressBar;
    public static DailyAppointmentsFragment newInstance() {
        return new DailyAppointmentsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_daily_appointments, container, false);

        // Patient list config
        mDailyAppointmentsRecyclerView = (RecyclerView) root.findViewById(R.id.dailyAppointmentsRecyclerView);
        mDailyAppointmentsRecyclerView.setHasFixedSize(true);
        mDailyAppointmentsRecyclerView.setAdapter(new DailyAppointmentsRecyclerViewAdapter(this,
                new ArrayList<Result>()));
        mDailyAppointmentsRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        mDailyAppointmentsRecyclerView.setLayoutManager(linearLayoutManager);
        mProgressBar =
                (ProgressBar) root.findViewById(R.id.dailyAppointmentsInitialProgressBar);
        mEmptyList = (TextView) root.findViewById(R.id.emptyDailyAppointmentsList);
        mEmptyList.setText(getString(R.string.search_visits_no_results));
        mEmptyList.setVisibility(View.INVISIBLE);
        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    @Override
    public void updateAdapter(List<Result> blocks) {
        List<Result> res=new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yy ");
        String strDate = mdformat.format(calendar.getTime());
        for (int i=0;i<blocks.size();i++) {

            Date date = DateUtils.getDateFromString(blocks.get(i).getTimeSlot().getStartDate());
            String blockDate = mdformat.format(date);
            if(blockDate.equals(strDate)){
                res.add(blocks.get(i));
            }
        }
        DailyAppointmentsRecyclerViewAdapter adapter = new DailyAppointmentsRecyclerViewAdapter(this,res);
        adapter.notifyDataSetChanged();
        mDailyAppointmentsRecyclerView.setAdapter(adapter);
    }


    @Override
    public void updateListVisibility(boolean isVisible) {
        mProgressBar.setVisibility(View.GONE);
        if (isVisible) {
            mDailyAppointmentsRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        } else {
            mDailyAppointmentsRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
            mEmptyList.setText(getString(R.string.search_patient_no_results));
        }
    }

    @Override
    public void setEmptyListText(int stringId) {
        mEmptyList.setText(getString(stringId));
    }

    @Override
    public void setEmptyListText(int stringId, String query) {
        mEmptyList.setText(getString(stringId, query));
    }
    @Override
    public void openDialog(Result result) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
        View promptView = layoutInflater.inflate(R.layout.dialog_manage_appointment, null);

        final AlertDialog alertD = new AlertDialog.Builder(this.getActivity()).create();
        Button cancel = (Button) promptView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               alertD.cancel();

            }
        });
        Button checkin = (Button) promptView.findViewById(R.id.checkin);

        checkin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mPresenter.setAppointmentStatus(result,"Missed");

            }
        });
        alertD.setView(promptView);

        alertD.show();
    }
}
