package org.openmrs.mobile.activities.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.CaptureVitalsActivity;
import org.openmrs.mobile.activities.DialogActivity;
import org.openmrs.mobile.activities.LoginActivity;
import org.openmrs.mobile.activities.VisitDashboardActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;

/**
 * General class for creating dialog fragment instances
 */
public class CustomFragmentDialog extends DialogFragment {
    private static final int TYPED_DIMENSION_VALUE = 10;

    public enum OnClickAction {
        SET_URL, SHOW_URL_DIALOG, DISMISS_URL_DIALOG, DISMISS, LOGOUT, UNAUTHORIZED, END_VISIT, START_VISIT, LOGIN
    }

    protected LayoutInflater mInflater;
    protected LinearLayout mFieldsLayout;

    protected TextView mTextView;
    protected TextView mTitleTextView;

    private Button mLeftButton;
    private Button mRightButton;

    protected EditText mEditText;

    private CustomDialogBundle mCustomDialogBundle;

    public CustomFragmentDialog() {
    }

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
        } else {
            this.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
        }
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mInflater = inflater;
        View dialogLayout = mInflater.inflate(R.layout.fragment_dialog_layout, null, false);
        this.mFieldsLayout = (LinearLayout) dialogLayout.findViewById(R.id.dialogForm);
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
            this.setBorderless();
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
        if (getActivity().getClass().equals(DialogActivity.class)) {
            getActivity().finish();
        }
        super.onDestroyView();

    }

    public final void setOnBackListener() {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (getActivity().getClass().equals(LoginActivity.class)) {
                        if (OpenMRS.getInstance().getServerUrl().equals(ApplicationConstants.EMPTY_STRING)) {
                            OpenMRS.getInstance().getOpenMRSLogger().d("Exit application");
                            getActivity().onBackPressed();
                            dismiss();
                        } else {
                            ((LoginActivity) getActivity()).hideURLDialog();
                            dismiss();
                        }
                    }
                }
                return false;
            }
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
    }

    public EditText addEditTextField(String defaultMessage) {
        LinearLayout field = (LinearLayout) mInflater.inflate(R.layout.openmrs_edit_text_field, null);
        EditText editText = (EditText) field.findViewById(R.id.openmrsEditText);
        if (null != defaultMessage) {
            editText.setText(defaultMessage);
        }
        mFieldsLayout.addView(field);
        return editText;
    }

    public TextView addTextField(String message) {
        LinearLayout field = (LinearLayout) mInflater.inflate(R.layout.openmrs_text_view_field, null);
        TextView textView = (TextView) field.findViewById(R.id.openmrsTextView);
        textView.setText(message);
        textView.setSingleLine(false);
        FontsUtil.setFont(textView, FontsUtil.OpenFonts.OPEN_SANS_ITALIC);
        mFieldsLayout.addView(field, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    public TextView addTitleBar(String title) {
        LinearLayout field = (LinearLayout) mInflater.inflate(R.layout.openmrs_title_view_field, null);
        TextView textView = (TextView) field.findViewById(R.id.openmrsTitleView);
        textView.setText(title);
        textView.setSingleLine(true);
        mFieldsLayout.addView(field, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
        this.mRightButton = (Button) dialogLayout.findViewById(R.id.dialogFormButtonsSubmitButton);
    }

    public void setLeftButton(View dialogLayout) {
        this.mLeftButton = (Button) dialogLayout.findViewById(R.id.dialogFormButtonsCancelButton);
    }

    public void addProgressBar(String message) {
        FrameLayout progressBarLayout = (FrameLayout) mInflater.inflate(R.layout.dialog_progress, null);
        TextView textView = (TextView) progressBarLayout.findViewById(R.id.progressTextView);
        textView.setText(message);
        mFieldsLayout.addView(progressBarLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public String getEditTextValue() {
        return mEditText.getText().toString();
    }

    private boolean isDialogAvailable() {
        return null != this && null != this.getDialog();
    }

    private View.OnClickListener onClickActionSolver(final OnClickAction action) {
        return new View.OnClickListener() {
            //CHECKSTYLE:OFF
            @Override
            public void onClick(View v) {
                switch (action) {
                    case SET_URL:
                        ((LoginActivity) getActivity()).setUrl(CustomFragmentDialog.this.mEditText.getText().toString());
                        dismiss();
                        break;
                    case DISMISS_URL_DIALOG:
                        ((LoginActivity) getActivity()).hideURLDialog();
                        dismiss();
                        break;
                    case LOGIN:
                        ((LoginActivity) getActivity()).login();
                        dismiss();
                        break;
                    case DISMISS:
                        dismiss();
                        break;
                    case LOGOUT:
                        ((ACBaseActivity) getActivity()).logout();
                        dismiss();
                        break;
                    case UNAUTHORIZED:
                        ((ACBaseActivity) getActivity()).moveUnauthorizedUserToLoginScreen();
                        dismiss();
                        break;
                    case END_VISIT:
                        ((VisitDashboardActivity) getActivity()).endVisit();
                        dismiss();
                        break;
                    case START_VISIT:
                        doStartVisitAction();
                        dismiss();
                        break;
                    default:
                        break;
                }
            }
            //CHECKSTYLE:ON
        };
    }

    private void doStartVisitAction() {
        Activity activity = getActivity();
        if (activity instanceof PatientDashboardActivity) {
            PatientDashboardActivity pda = ((PatientDashboardActivity) activity);
            PatientVisitsFragment fragment = (PatientVisitsFragment) pda.getSupportFragmentManager().getFragments().get(PatientDashboardActivity.TabHost.VISITS_TAB_POS);
            if (fragment != null) {
                fragment.startVisit();
            }
        } else {
            CaptureVitalsActivity cva = ((CaptureVitalsActivity) activity);
            cva.startVisit();
        }
    }
}
