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
package org.openmrs.mobile.activities.formaddeditservice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.utilities.FontsUtil;

public class FormAddEditServiceFragment extends ACBaseFragment<FormAddEditServiceContract.Presenter> implements FormAddEditServiceContract.View  {
    public static FormAddEditServiceFragment newInstance() {
        return new FormAddEditServiceFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_add_edit_service, container, false);
        String ServiceName = this.getActivity().getIntent().getStringExtra("service_name");
        String ServiceDuration = this.getActivity().getIntent().getStringExtra("service_duration");
        String ServiceDescription = this.getActivity().getIntent().getStringExtra("service_description");
        String edit_or_new= this.getActivity().getIntent().getStringExtra("edit_or_new");
        String Uuid = this.getActivity().getIntent().getStringExtra("service_uuid");

        EditText mServiceName = (EditText) root.findViewById(R.id.serviceName);
        mServiceName.setText(ServiceName);
        EditText mServiceDuration = (EditText) root.findViewById(R.id.serviceDuration);
        mServiceDuration.setText(ServiceDuration);
        EditText mServiceDescription = (EditText) root.findViewById(R.id.serviceDiscription);
        Button mServiceSave = (Button) root.findViewById(R.id.Save);
        Button mServiceDelete = (Button) root.findViewById(R.id.Delete);
        switch (edit_or_new) {
            case "new_form":

                mServiceDelete.setVisibility(View.GONE);
                mServiceSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.addServiceTypes(mServiceName.getText(),mServiceDescription.getText(),mServiceDuration.getText());

                    }
                });
                break;
            case "edit_form":
                mServiceSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.editServiceTypes(mServiceName.getText(),mServiceDescription.getText(),mServiceDuration.getText(),Uuid);

                    }
                });
                mServiceDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.deleteServiceTypes(Uuid);

                    }
                });
                break;
            default:
                // Do nothing
                break;
        }
        if (null != ServiceDescription) {
            mServiceDescription.setText(ServiceDescription);
        }
        else {
            mServiceDescription.setText("No Description");
        }
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    public void endActivity(){
        getActivity().finish();
    }
}
