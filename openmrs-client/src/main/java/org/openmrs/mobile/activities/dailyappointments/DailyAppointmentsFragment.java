package org.openmrs.mobile.activities.dailyappointments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import java.util.Calendar;

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

public class DailyAppointmentsFragment extends ACBaseFragment<DailyAppointmentsContract.Presenter> implements DailyAppointmentsContract.View {


    private EditText eddob;


    public static DailyAppointmentsFragment newInstance() {
        return new DailyAppointmentsFragment();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_daily_appointments, container, false);
        resolveViews(root);
        return root;
    }

    private void resolveViews(View v) {

        eddob = (EditText) v.findViewById(R.id.dob);


    }

    private void addListeners() {
        Log.d("tag", "fabjkbkbnkjbkjf");
        eddob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("tag", "fabjkbkbnkjbkjf");
                int cYear;
                int cMonth;
                int cDay;


                Calendar currentDate = Calendar.getInstance();
                cYear = currentDate.get(Calendar.YEAR);
                cMonth = currentDate.get(Calendar.MONTH);
                cDay = currentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker = new DatePickerDialog(DailyAppointmentsFragment.this.getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                int adjustedMonth = selectedmonth + 1;
                                eddob.setText(selectedday + "/" + adjustedMonth + "/" + selectedyear);

                            }
                        }, cYear, cMonth, cDay);
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });

    }

}
