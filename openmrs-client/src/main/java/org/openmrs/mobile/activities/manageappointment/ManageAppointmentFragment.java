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

package org.openmrs.mobile.activities.manageappointment;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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

public class ManageAppointmentFragment extends ACBaseFragment<ManageAppointmentContract.Presenter> implements ManageAppointmentContract.View {
    private RecyclerView mManageAppointmentRecyclerView;
    private TextView mEmptyList;

    //Initialization Progress bar
    private ProgressBar mProgressBar;
    public static ManageAppointmentFragment newInstance() {
        return new ManageAppointmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_manage_appointment, container, false);

        // Patient list config
        mManageAppointmentRecyclerView = (RecyclerView) root.findViewById(R.id.manageAppointmentRecyclerView);
        mManageAppointmentRecyclerView.setHasFixedSize(true);
        mManageAppointmentRecyclerView.setAdapter(new ManageAppointmentRecyclerViewAdapter(this,
                new ArrayList<Result>()));
        mManageAppointmentRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        mManageAppointmentRecyclerView.setLayoutManager(linearLayoutManager);
        mProgressBar =
                (ProgressBar) root.findViewById(R.id.manageAppointmentInitialProgressBar);
        mEmptyList = (TextView) root.findViewById(R.id.emptyManageAppointmentList);
        mEmptyList.setText(getString(R.string.search_visits_no_results));
        mEmptyList.setVisibility(View.INVISIBLE);
        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    @Override
    public void updateAdapter(List<Result> blocks) {
        List<Result> latest = new ArrayList<>();
        Calendar toDayCalendar =Calendar.getInstance();
        Date date1=toDayCalendar.getTime();
        for (int i = 0; i < blocks.size(); i++) {
            Date date = DateUtils.getDateFromString(blocks.get(i).getTimeSlot().getStartDate());
            if (date.compareTo(date1)>0) {
                latest.add(blocks.get(i));
            }
        }

        ManageAppointmentRecyclerViewAdapter adapter = new ManageAppointmentRecyclerViewAdapter(this, latest);
        adapter.notifyDataSetChanged();
        mManageAppointmentRecyclerView.setAdapter(adapter);
    }


    @Override
    public void updateListVisibility(boolean isVisible) {
        mProgressBar.setVisibility(View.GONE);
        if (isVisible) {
            mManageAppointmentRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        } else {
            mManageAppointmentRecyclerView.setVisibility(View.GONE);
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
