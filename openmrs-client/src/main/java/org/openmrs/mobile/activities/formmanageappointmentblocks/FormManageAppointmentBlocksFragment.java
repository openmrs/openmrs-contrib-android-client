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
package org.openmrs.mobile.activities.formmanageappointmentblocks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.provider.Result;
import org.openmrs.mobile.models.servicestypemodel.Services;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FormManageAppointmentBlocksFragment extends ACBaseFragment<FormManageAppointmentBlocksContract.Presenter> implements FormManageAppointmentBlocksContract.View {
    private Spinner mDropdownLocation;
    private Spinner mDropdownProvider;
    private List<Result> providerList;
    private List<Location> locationList;
    private List<String> info = new ArrayList<>();
    private int edit_new = 0;
    private List<Services> serviceList = new ArrayList<>();

    public static FormManageAppointmentBlocksFragment newInstance() {
        return new FormManageAppointmentBlocksFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_appointment_blocks, container, false);
        edit_new  = this.getActivity().getIntent().getIntExtra("edit_or_new", 0);
        Button mAppointmentBlocksDelete = (Button) root.findViewById(R.id.Delete);

        switch (edit_new) {
            case 0:
                mAppointmentBlocksDelete.setVisibility(View.INVISIBLE);
                break;
            case 1:
                String uuid = this.getActivity().getIntent().getStringExtra("blocks_uuid");
                List<String> test = (List<String>) this.getActivity().getIntent().getSerializableExtra("test");
                List<String> test2 = (List<String>) this.getActivity().getIntent().getSerializableExtra("test2");
                info = test;
                TextView mAppointmentBlocksServiceType = (TextView) root.findViewById(R.id.service);
                mAppointmentBlocksServiceType.setText(test2.toString());
                TextView startDate = (TextView) root.findViewById(R.id.date);
                startDate.setText(test.get(2));
                TextView endDate = (TextView) root.findViewById(R.id.date2);
                endDate.setText(test.get(3));
                mAppointmentBlocksDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.deleteAppointmentBlocks(uuid);
                    }


                });
                break;
                default:
                    mAppointmentBlocksDelete.setVisibility(View.INVISIBLE);
                    break;

        }

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

        TextView mAppointmentBlocksServiceType = (TextView) root.findViewById(R.id.service);
        mAppointmentBlocksServiceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getServiceTypes();
            }
        });

        Button mAppointmentBlocksSave = (Button) root.findViewById(R.id.Save);
        mAppointmentBlocksSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addAppointmentBlocks(mDropdownLocation,
                        locationList,
                        mDropdownProvider,
                        providerList,
                        datetime,
                        datetimeend,
                        mAppointmentBlocksServiceType,
                        serviceList
                );

            }
        });

        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    public void endActivity() {
        this.getActivity().finish();
    }

    @Override
    public void fillLocationDropDown(List<Location> location) {
        mDropdownLocation = (Spinner) this.getActivity().findViewById(R.id.location);
        locationList = location;
        List<String> items = getLocationStringList(location);
        final LocationArrayAdapter adapter = new LocationArrayAdapter(this.getActivity(), items);
        mDropdownLocation.setAdapter(adapter);
    }

    @Override
    public void getCalender(TextView datetime) {
        Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(FormManageAppointmentBlocksFragment.super.getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(FormManageAppointmentBlocksFragment.super.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        datetime.setText(date.getTime().toString());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

    }

    @Override
    public void fillProviderDropDown(List<Result> provider) {
        mDropdownProvider = (Spinner) this.getActivity().findViewById(R.id.provider);
        providerList = provider;
        List<String> items = getProviderStringList(provider);
        final LocationArrayAdapter adapter = new LocationArrayAdapter(this.getActivity(), items);
        mDropdownProvider.setAdapter(adapter);
    }

    private List<String> getLocationStringList(List<Location> locationList) {
        List<String> list = new ArrayList<String>();
        switch (edit_new) {
            case 0:
                list.add(getString(R.string.login_location_select));
                for (int i = 0; i < locationList.size(); i++) {
                    list.add(locationList.get(i).getDisplay());

                }
                break;
            case 1:
                list.add(info.get(0));
                for (int i = 0; i < locationList.size(); i++) {
                    list.add(locationList.get(i).getDisplay());

                }
                break;
            default:
                list.add(getString(R.string.login_location_select));
                for (int i = 0; i < locationList.size(); i++) {
                    list.add(locationList.get(i).getDisplay());

                }
                break;
        }
        return list;
    }

    private List<String> getProviderStringList(List<Result> providerList) {
        List<String> list = new ArrayList<String>();

        switch (edit_new) {
            case 0:
                list.add("Set Provider");
                for (int i = 0; i < providerList.size(); i++) {
                    list.add(providerList.get(i).getDisplay());

                }
                break;
            case 1:
                list.add(info.get(1));
                for (int i = 0; i < providerList.size(); i++) {
                    list.add(providerList.get(i).getDisplay());

                }
                break;
            default:
            list.add("Set Provider");
            for (int i = 0; i < providerList.size(); i++) {
                list.add(providerList.get(i).getDisplay());

            }
            break;
        }
        return list;
    }

    @Override
    public void createDialog(List<Services> services) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
        List<CharSequence> list = new ArrayList<CharSequence>() {
        };

        for (int i = 0; i < services.size(); i++) {
            list.add(services.get(i).getDisplay());

        }
        final CharSequence[] dialogList = list.toArray(new CharSequence[list.size()]);

        builder.setTitle("Select ")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(dialogList, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }

                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<String> list = new ArrayList<String>();
                        for (int i = 0; i < mSelectedItems.size(); i++) {
                            list.add(services.get((Integer) mSelectedItems.get(i)).getDisplay());
                            serviceList.add(services.get((Integer) mSelectedItems.get(i)));

                        }
                        TextView mAppointmentBlocksServiceType = (TextView) getActivity().findViewById(R.id.service);
                        mAppointmentBlocksServiceType.setText(list.toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //This method is left blank intentionally.
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }


}