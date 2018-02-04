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
package org.openmrs.mobile.activities.formappointmentrequest;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.servicestypemodel.Services;
import org.openmrs.mobile.models.timeblocks.Result;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class FormAppointmentRequestFragment extends ACBaseFragment<FormAppointmentRequestContract.Presenter> implements FormAppointmentRequestContract.View {private RecyclerView mFormAppointmentRequestRecyclerView;
    private Spinner mServiceTypes;
    private String service;

    public static FormAppointmentRequestFragment newInstance() {
        return new FormAppointmentRequestFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_appointment_request, container, false);
        String uuid = this.getActivity().getIntent().getStringExtra("Uuid");
        service = this.getActivity().getIntent().getStringExtra("Service");

        TextView datetime = (TextView) root.findViewById(R.id.date2);
        datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCalender(datetime);

            }


        });

        TextView datetimeend = (TextView) root.findViewById(R.id.date);
        datetimeend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCalender(datetimeend);

            }


        });


        Button mAppointmentRequestDelete = (Button) root.findViewById(R.id.Delete);
        mAppointmentRequestDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteAppointmentRequest(uuid);

            }
        });

        Button mAppointmentRequestSave = (Button) root.findViewById(R.id.Save);
        mAppointmentRequestSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getTimeBlocks(service);

            }
        });
        mFormAppointmentRequestRecyclerView = (RecyclerView) root.findViewById(R.id.formAppointmentRequestRecyclerView);
        mFormAppointmentRequestRecyclerView.setHasFixedSize(true);
        mFormAppointmentRequestRecyclerView.setAdapter(new FormAppointmentRequestRecyclerViewAdapter(this,
                new ArrayList<Result>()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        mFormAppointmentRequestRecyclerView.setLayoutManager(linearLayoutManager);


        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));

        return root;
    }

    @Override
    public void getCalender(TextView datetime) {
        Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(super.getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

    }

    public void endActivity() {
        this.getActivity().finish();
    }

    @Override
    public void fillServiceTypeDropDown(List<Services> services) {
        mServiceTypes = (Spinner) this.getActivity().findViewById(R.id.serviceDropdown);

        List<String> items = getSerivceStringList(services);
        final ServiceArrayAdapter adapter = new ServiceArrayAdapter(this.getActivity(), items);
        mServiceTypes.setAdapter(adapter);

    }


    private List<String> getSerivceStringList(List<Services> services) {
        List<String> list = new ArrayList<String>();
        list.add(service);
        for (int i = 0; i < services.size(); i++) {
            list.add(services.get(i).getDisplay());
        }
        return list;
    }

    @Override
    public void createDialog(List<Result> blocks, String service) {
        ScrollView scroll = (ScrollView) this.getActivity().findViewById(R.id.scrollView);
        scroll.setVisibility(View.GONE);
        RelativeLayout recycle = (RelativeLayout) this.getActivity().findViewById(R.id.recyclerView);
        recycle.setVisibility(View.VISIBLE);
        updateAdapter(blocks);

    }

    @Override
    public void updateAdapter(List<Result> blocks) {
        List<Result> latest = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < blocks.size(); i++) {
            Long Date = DateUtils.convertTime(blocks.get(i).getStartDate());
            if (c.getTimeInMillis()<Date) {
                latest.add(blocks.get(i));
            }
        }
        List<Result> res = new ArrayList<Result>();
        for (int i = 0; i < latest.size(); i++) {
            for (int j = 0; j < latest.get(i).getAppointmentBlock().getTypes().size(); j++) {
                if (latest.get(i).getAppointmentBlock().getTypes().get(j).getDisplay().equals(service)) {
                    res.add(latest.get(i));
                }
            }
        }
        if (res.isEmpty()){
            Snackbar.make(this.getView(),"No Block Found",100);
        }
        else {
            FormAppointmentRequestRecyclerViewAdapter adapter = new FormAppointmentRequestRecyclerViewAdapter(this, res);
            adapter.notifyDataSetChanged();
            mFormAppointmentRequestRecyclerView.setAdapter(adapter);
        }
    }

}
