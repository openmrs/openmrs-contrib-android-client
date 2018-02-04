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
package org.openmrs.mobile.activities.appointmentscheduling;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.appointmentrequests.AppointmentRequestsActivity;
import org.openmrs.mobile.activities.dailyappointments.DailyAppointmentsActivity;
import org.openmrs.mobile.activities.manageappointment.ManageAppointmentActivity;
import org.openmrs.mobile.activities.manageappointmentblocks.ManageAppointmentBlocksActivity;
import org.openmrs.mobile.activities.manageservice.ManageServiceActivity;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;
public class AppointmentSchedulingFragment extends ACBaseFragment<AppointmentSchedulingContract.Presenter> implements AppointmentSchedulingContract.View, View.OnClickListener {
    private ImageView mManageServiceButton;
    private ImageView mManageAppointmentBlocksButton;
    private ImageView mManageAppointmentButton;
    private ImageView mDailyAppointmentsButton;
    private ImageView mAppointmentRequestsButton;
    private RelativeLayout mManageServiceView;
    private RelativeLayout mManageAppointmentBlocksView;
    private RelativeLayout mManageAppointmentView;
    private RelativeLayout mDailyAppointmentsView;
    private RelativeLayout mAppointmentRequestsView;
    private SparseArray<Bitmap> mBitmapCache;


    public static AppointmentSchedulingFragment newInstance() {
        return new AppointmentSchedulingFragment();
    }

    // ImageView Buttons
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_appointment_scheduling, container, false);

        if (root != null) {
            initFragmentFields(root);
            setListeners();
        }

        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    private void initFragmentFields(View root) {
        mManageServiceButton = (ImageView) root.findViewById(R.id.manageServiceButton);
        mManageAppointmentBlocksButton = (ImageView) root.findViewById(R.id.manageAppointmentBlocksButton);
        mManageAppointmentButton = (ImageView) root.findViewById(R.id.manageAppointmentButton);
        mDailyAppointmentsButton = (ImageView) root.findViewById(R.id.dailyAppointmentsButton);
        mAppointmentRequestsButton = (ImageView) root.findViewById(R.id.appointmentRequestsButton);
        mManageServiceView = (RelativeLayout) root.findViewById(R.id.manageServiceView);
        mManageAppointmentBlocksView = (RelativeLayout) root.findViewById(R.id.manageAppointmentBlocksView);
        mManageAppointmentView = (RelativeLayout) root.findViewById(R.id.manageAppointmentView);
        mDailyAppointmentsView = (RelativeLayout) root.findViewById(R.id.dailyAppointmentsView);
        mAppointmentRequestsView = (RelativeLayout) root.findViewById(R.id.appointmentRequestsView);
    }


    private void setListeners() {
        mManageServiceView.setOnClickListener(this);
        mManageAppointmentBlocksView.setOnClickListener(this);
        mDailyAppointmentsView.setOnClickListener(this);
        mManageAppointmentView.setOnClickListener(this);
        mAppointmentRequestsView.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    @Override
    public void bindDrawableResources() {
        bindDrawableResource(mManageServiceButton, R.drawable.ico_visits);
        bindDrawableResource(mManageAppointmentBlocksButton, R.drawable.ico_visits);
        bindDrawableResource(mManageAppointmentButton, R.drawable.ico_visits);
        bindDrawableResource(mDailyAppointmentsButton, R.drawable.ico_visits);
        bindDrawableResource(mAppointmentRequestsButton, R.drawable.ico_visits);
    }

    private void bindDrawableResource(ImageView imageView, int drawableId) {
        mBitmapCache = new SparseArray<>();
        if (getView() != null) {
            createImageBitmap(drawableId, imageView.getLayoutParams());
            imageView.setImageBitmap(mBitmapCache.get(drawableId));
        }
    }


    private void unbindDrawableResources() {
        if (null != mBitmapCache) {
            for (int i = 0; i < mBitmapCache.size(); i++) {
                Bitmap bitmap = mBitmapCache.valueAt(i);
                bitmap.recycle();
            }
        }
    }

    private void createImageBitmap(Integer key, ViewGroup.LayoutParams layoutParams) {
        if (mBitmapCache.get(key) == null) {
            mBitmapCache.put(key, ImageUtils.decodeBitmapFromResource(getResources(), key,
                    layoutParams.width, layoutParams.height));
        }
    }

    private void startNewActivity(Class<? extends ACBaseActivity> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    /**
     * @return New instance of SyncedPatientsFragment
     */


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manageServiceView:
                startNewActivity(ManageServiceActivity.class);
                break;
            case R.id.manageAppointmentBlocksView:
                startNewActivity(ManageAppointmentBlocksActivity.class);
                break;
            case R.id.manageAppointmentView:
                startNewActivity(ManageAppointmentActivity.class);
                break;
            case R.id.dailyAppointmentsView:
                startNewActivity(DailyAppointmentsActivity.class);
                break;
            case R.id.appointmentRequestsView:
                startNewActivity(AppointmentRequestsActivity.class);
                break;
            default:
                // Do nothing
                break;
        }
    }

}