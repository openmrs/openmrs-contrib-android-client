package org.openmrs.client.activities.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.bundle.CustomDialogBundle;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.URLUtils;

/**
 * General class for creating dialog fragment instances
 */
public class CustomFragmentDialog extends DialogFragment {
    private static final int TYPED_DIMENSION_VALUE = 10;

    public enum OnClickAction {
        LOGIN, DISMISS, RETRY;
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

        buildDialog();
        return dialogLayout;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isDialogAvailable()) {
            this.setBorderless();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isDialogAvailable()) {
            this.setBorderless();
        }
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public final void setBorderless() {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int marginWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TYPED_DIMENSION_VALUE,
                OpenMRS.getInstance().getResources().getDisplayMetrics());

        Display mDisplay = getDialog().getWindow().getWindowManager().getDefaultDisplay();
        int width = mDisplay.getWidth();

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
            addProgressBar();
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
        mFieldsLayout.addView(field, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    public TextView addTitleBar(String title) {
        LinearLayout field = (LinearLayout) mInflater.inflate(R.layout.openmrs_title_view_field, null);
        TextView textView = (TextView) field.findViewById(R.id.openmrsTitleView);
        textView.setText(title);
        textView.setSingleLine(true);
        mFieldsLayout.addView(field, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
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

    public void addProgressBar() {
        RelativeLayout progressBaLayout = (RelativeLayout) mInflater.inflate(R.layout.dialog_progress, null);
        mFieldsLayout.addView(progressBaLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }

    private boolean isDialogAvailable() {
        return null != this && null != this.getDialog();
    }

    private View.OnClickListener onClickActionSolver(final OnClickAction action) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (action) {
                    case LOGIN:
                        OpenMRS.getInstance().setServerUrl(URLUtils.trimLastSlash(CustomFragmentDialog.this.mEditText.getText().toString()));
                        ((LoginActivity) getActivity()).login();
                        dismiss();
                        break;
                    case DISMISS:
                        dismiss();
                        break;
                    case RETRY:
                        ((LoginActivity) getActivity()).login();
                        dismiss();
                    default:
                        break;
                }
            }
        };
    }


}
