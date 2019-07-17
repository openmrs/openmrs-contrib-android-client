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

package org.openmrs.mobile.activities.dialog;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.activities.addeditpatient.SimilarPatientsRecyclerViewAdapter;
import org.openmrs.mobile.activities.login.LoginActivity;
import org.openmrs.mobile.activities.login.LoginFragment;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.visits.PatientVisitsFragment;
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsActivity;
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * General class for creating dialog fragment instances
 */
public class CustomFragmentDialog extends DialogFragment {
    private static final int TYPED_DIMENSION_VALUE = 10;

    public enum OnClickAction {
        SET_URL, SHOW_URL_DIALOG, DISMISS_URL_DIALOG, DISMISS, LOGOUT, FINISH, INTERNET, UNAUTHORIZED, END_VISIT,
        START_VISIT, LOGIN, REGISTER_PATIENT, CANCEL_REGISTERING, DELETE_PATIENT,MULTI_DELETE_PATIENT, SELECT_LOCATION
    }

    protected LayoutInflater mInflater;
    protected LinearLayout mFieldsLayout;
    protected RecyclerView mRecyclerView;
    protected ListView locationListView;

    protected TextView mTextView;
    protected TextView mTitleTextView;

    private Button mLeftButton;
    private Button mRightButton;

    protected EditText mEditText;

    private CustomDialogBundle mCustomDialogBundle;

    private ArrayList<Patient> itemsToDelete = new ArrayList<>();

    protected final OpenMRS mOpenMRS = OpenMRS.getInstance();

    public static CustomFragmentDialog newInstance(CustomDialogBundle customDialogBundle) {
        CustomFragmentDialog dialog = new CustomFragmentDialog();
        dialog.setRetainInstance(true);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.BundleKeys.CUSTOM_DIALOG_BUNDLE, customDialogBundle);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomDialogBundle = (CustomDialogBundle) getArguments().getSerializable(ApplicationConstants.BundleKeys.CUSTOM_DIALOG_BUNDLE);
        if (mCustomDialogBundle.hasLoadingBar()) {
            this.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.LoadingDialogTheme_DialogTheme);
        } else if(mCustomDialogBundle.hasPatientList()) {
            this.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.SimilarPatients_DialogTheme);
        } else {
            this.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
        }
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mInflater = inflater;
        View dialogLayout = mInflater.inflate(R.layout.fragment_dialog_layout, null, false);
        this.mFieldsLayout = dialogLayout.findViewById(R.id.dialogForm);
        this.setRightButton(dialogLayout);
        this.setLeftButton(dialogLayout);
        getDialog().setCanceledOnTouchOutside(false);
        buildDialog();
        FontsUtil.setFont((ViewGroup) dialogLayout);
        return dialogLayout;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isDialogAvailable()) {
            this.setBorderless();
            this.setOnBackListener();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isDialogAvailable()) {
            if(mCustomDialogBundle.hasLoadingBar()||mCustomDialogBundle.hasProgressDialog())
                this.setProgressDialogWidth();
            else{
                this.setBorderless();
            }
            this.setOnBackListener();
        }
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        if (null == manager.findFragmentByTag(tag)) {
            manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();

    }

    public final void setOnBackListener() {
        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && getActivity().getClass().equals(LoginActivity.class)) {
                if (OpenMRS.getInstance().getServerUrl().equals(ApplicationConstants.EMPTY_STRING)) {
                    OpenMRS.getInstance().getOpenMRSLogger().d("Exit application");
                    getActivity().onBackPressed();
                    dismiss();
                } else {
                    ((LoginFragment) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.loginContentFrame))
                            .hideURLDialog();
                    dismiss();
                }
            }
            return false;
        });
    }

    public final void setBorderless() {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int marginWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TYPED_DIMENSION_VALUE,
                OpenMRS.getInstance().getResources().getDisplayMetrics());

        DisplayMetrics display = this.getResources().getDisplayMetrics();
        int width = display.widthPixels;

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width - 2 * marginWidth;

        getDialog().getWindow().setAttributes(params);
    }

    public final void setProgressDialogWidth() {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int marginWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TYPED_DIMENSION_VALUE,
                OpenMRS.getInstance().getResources().getDisplayMetrics());

        DisplayMetrics display = this.getResources().getDisplayMetrics();
        int width = display.widthPixels;

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (width * 5 / 6) - (2 * marginWidth);

        getDialog().getWindow().setAttributes(params);
    }


    private void buildDialog() {
        if (null != mCustomDialogBundle.getTitleViewMessage()) {
            mTitleTextView = addTitleBar(mCustomDialogBundle.getTitleViewMessage());
        }
        if (null != mCustomDialogBundle.getEditTextViewMessage()) {
            mEditText = addEditTextField(mCustomDialogBundle.getEditTextViewMessage());
        }
        if (null != mCustomDialogBundle.getTextViewMessage()) {
            mTextView = addTextField(mCustomDialogBundle.getTextViewMessage());
        }
        if (null != mCustomDialogBundle.getLeftButtonAction()) {
            setLeftButton(mCustomDialogBundle.getLeftButtonText());
            mLeftButton.setOnClickListener(onClickActionSolver(mCustomDialogBundle.getLeftButtonAction()));
        }
        if (null != mCustomDialogBundle.getRightButtonAction()) {
            setRightButton(mCustomDialogBundle.getRightButtonText());
            mRightButton.setOnClickListener(onClickActionSolver(mCustomDialogBundle.getRightButtonAction()));
        }
        if (mCustomDialogBundle.hasLoadingBar()) {
            addProgressBar(mCustomDialogBundle.getTitleViewMessage());
            this.setCancelable(false);
        }
        if (mCustomDialogBundle.hasProgressDialog()) {
            addProgressBar(mCustomDialogBundle.getProgressViewMessage());
            this.setCancelable(false);
        }
        if(null != mCustomDialogBundle.getPatientsList()){
            mRecyclerView = addRecycleView(mCustomDialogBundle.getPatientsList(), mCustomDialogBundle.getNewPatient());
        }
        if(null != mCustomDialogBundle.getLocationList()){
          addSingleChoiceItemsListView(mCustomDialogBundle.getLocationList());
        }
        if(null != mCustomDialogBundle.getSelectedItems()){
            itemsToDelete = mCustomDialogBundle.getSelectedItems();
        }
    }

    private RecyclerView addRecycleView(List<Patient> patientsList, Patient newPatient) {
        LinearLayout field = (LinearLayout) mInflater.inflate(R.layout.openmrs_recycle_view, null);
        RecyclerView recyclerView = field.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SimilarPatientsRecyclerViewAdapter((getActivity()), patientsList, newPatient));
        mFieldsLayout.addView(field);
        recyclerView.setHasFixedSize(true);
        return recyclerView;
    }

    private void addSingleChoiceItemsListView(List<String>locationList) {
        LinearLayout field = (LinearLayout) mInflater.inflate(R.layout.openmrs_single_choice_list_view, null);
        locationListView = field.findViewById(R.id.singleChoiceListView);
        locationListView.setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.row_single_checked_layout, locationList));
        locationListView.setItemChecked(locationList.indexOf(mOpenMRS.getLocation()),true);
        mFieldsLayout.addView(field);

    }

    public EditText addEditTextField(String defaultMessage) {
        LinearLayout field = (LinearLayout) mInflater.inflate(R.layout.openmrs_edit_text_field, null);
        EditText editText = field.findViewById(R.id.openmrsEditText);
        if (null != defaultMessage) {
            editText.setText(defaultMessage);
        }
        mFieldsLayout.addView(field);
        return editText;
    }

    public TextView addTextField(String message) {
        LinearLayout field = (LinearLayout) mInflater.inflate(R.layout.openmrs_text_view_field, null);
        TextView textView = field.findViewById(R.id.openmrsTextView);
        textView.setText(message);
        textView.setSingleLine(false);
        
        if(null != mCustomDialogBundle.getLocationList()){
            textView.setTextSize(18);
        }
        mFieldsLayout.addView(field, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    public TextView addTitleBar(String title) {
        LinearLayout field = (LinearLayout) mInflater.inflate(R.layout.openmrs_title_view_field, null);
        TextView textView = field.findViewById(R.id.openmrsTitleView);
        if (mCustomDialogBundle.hasProgressDialog() || mCustomDialogBundle.hasLoadingBar()) {
            mFieldsLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            p.weight = 2;
            field.setLayoutParams(p);
            field.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            textView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
        textView.setText(title);
        textView.setSingleLine(true);
        mFieldsLayout.addView(field);
        return textView;
    }

    public void setLeftButton(String text) {
        mLeftButton.setText(text);
        setViewVisible(mLeftButton, true);
    }

    public void setRightButton(String text) {
        mRightButton.setText(text);
        setViewVisible(mRightButton, true);
    }

    private void setViewVisible(View view, boolean visible) {
        if (visible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public void setRightButton(View dialogLayout) {
        this.mRightButton = dialogLayout.findViewById(R.id.dialogFormButtonsSubmitButton);
    }

    public void setLeftButton(View dialogLayout) {
        this.mLeftButton = dialogLayout.findViewById(R.id.dialogFormButtonsCancelButton);
    }

    public void addProgressBar(String message) {
        RelativeLayout progressBarLayout = (RelativeLayout) mInflater.inflate(R.layout.dialog_progress, null);
        mFieldsLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp2px(getResources(),100));
        p.weight = 1;
        progressBarLayout.setLayoutParams(p);
        mFieldsLayout.addView(progressBarLayout);
    }

    public String getEditTextValue() {
        String value = "";
        if (mEditText!=null) {
            value = mEditText.getText().toString();
        }
        return value;
    }

    private boolean isDialogAvailable() {
        return null != this && null != this.getDialog();
    }

    private View.OnClickListener onClickActionSolver(final OnClickAction action) {
        //CHECKSTYLE:OFF
        return view -> {
            switch (action) {
                case DISMISS_URL_DIALOG:
                    ((LoginFragment) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.loginContentFrame))
                            .hideURLDialog();
                    dismiss();
                    break;
                case LOGIN:
                    ((LoginFragment) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.loginContentFrame))
                            .login(true);
                    dismiss();
                    break;
                case DISMISS:
                    dismiss();
                    break;
                case LOGOUT:
                    ((ACBaseActivity) getActivity()).logout();
                    dismiss();
                    break;
                case FINISH:
                    getActivity().moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                    break;
                case INTERNET:
                    getActivity().startActivity(new Intent(Settings.ACTION_SETTINGS));
                    dismiss();
                    break;
                case UNAUTHORIZED:
                    ((ACBaseActivity) getActivity()).moveUnauthorizedUserToLoginScreen();
                    dismiss();
                    break;
                case END_VISIT:
                    ((VisitDashboardActivity) getActivity()).mPresenter.endVisit();
                    dismiss();
                    break;
                case START_VISIT:
                    doStartVisitAction();
                    dismiss();
                    break;
                case REGISTER_PATIENT:
                    ((AddEditPatientActivity) getActivity()).mPresenter.registerPatient();
                    dismiss();
                    break;
                case CANCEL_REGISTERING:
                    ((AddEditPatientActivity) getActivity()).mPresenter.finishPatientInfoActivity();
                    dismiss();
                    break;
                case DELETE_PATIENT:
                    PatientDashboardActivity activity = (PatientDashboardActivity) getActivity();
                    activity.mPresenter.deletePatient();
                    dismiss();
                    activity.finish();
                    break;
                case MULTI_DELETE_PATIENT:
                    SyncedPatientsActivity syncedPatientsActivity = (SyncedPatientsActivity) getActivity();
                    for (Patient patientItem : itemsToDelete) {
                        if (syncedPatientsActivity != null) {
                            syncedPatientsActivity.mPresenter.deletePatient(patientItem);
                        }
                    }
                    dismiss();
                    Objects.requireNonNull(syncedPatientsActivity).finish();
                    ToastUtil.showShortToast(getContext(), ToastUtil.ToastType.SUCCESS, org.openmrs.mobile.R.string.multiple_patients_deleted);
                    break;
                case SELECT_LOCATION:
                    mOpenMRS.setLocation(locationListView.getAdapter().getItem(locationListView.getCheckedItemPosition()).toString());
                    dismiss();
                    break;
                default:
                    break;
            }
            //CHECKSTYLE:ON
        };
    }

    private void doStartVisitAction() {
        Activity activity = getActivity();
        if (activity instanceof PatientDashboardActivity) {
            PatientDashboardActivity pda = ((PatientDashboardActivity) activity);
            List<Fragment> fragments = pda.getSupportFragmentManager().getFragments();
            PatientVisitsFragment fragment = null;
            for (Fragment frag : fragments) {
                if (frag instanceof PatientVisitsFragment) {
                    fragment = (PatientVisitsFragment) frag;
                    break;
                }
            }
            if (fragment != null) {
                fragment.startVisit();
            }
        }
    }

    public static int dp2px(Resources resource, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,   dp,resource.getDisplayMetrics());
    }
}
