package org.openmrs.mobile.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.login.LoginFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.listeners.watcher.LoginValidatorWatcher;
import org.openmrs.mobile.utilities.StringUtils;

public class ChangePassword extends AppCompatActivity {

    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private TextInputLayout oldPasswordInput;
    private TextInputLayout newPasswordInput;
    private TextInputLayout confirmPasswordInput;
    private CheckBox mShowPassword1;
    private CheckBox mShowPassword2;
    private CheckBox mShowPassword3;

    private Button submitButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initField();
        initListeners();
    }

    private void initField() {

        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        submitButton = (Button) findViewById(R.id.changePasswordButton);
        oldPasswordInput = (TextInputLayout) findViewById(R.id.old_password_input);
        newPasswordInput = (TextInputLayout) findViewById(R.id.new_password_input);
        confirmPasswordInput = (TextInputLayout) findViewById(R.id.confirm_password_input);
        mShowPassword1 = (CheckBox) findViewById(R.id.checkboxShowPassword1);
        mShowPassword2 = (CheckBox) findViewById(R.id.checkboxShowPassword2);
        mShowPassword3 = (CheckBox) findViewById(R.id.checkboxShowPassword3);
    }

    private void initListeners() {





        oldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    oldPassword.setHint("");
                    oldPasswordInput.setHint(Html.fromHtml(getString(R.string.old_password)));
                } else if (oldPassword.getText().toString().equals("")) {
                    oldPassword.setHint(Html.fromHtml(getString(R.string.old_password) + getString(R.string.req_star)));
                    oldPasswordInput.setHint("");
                }
            }
        });

        newPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    newPassword.setHint("");
                    newPasswordInput.setHint(Html.fromHtml(getString(R.string.new_password)));
                } else if (newPassword.getText().toString().equals("")) {
                    newPassword.setHint(Html.fromHtml(getString(R.string.new_password) + getString(R.string.req_star)));
                    newPasswordInput.setHint("");
                }
            }
        });

        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    confirmPassword.setHint("");
                    confirmPasswordInput.setHint(Html.fromHtml(getString(R.string.confirm_password)));
                } else if (newPassword.getText().toString().equals("")) {
                    newPassword.setHint(Html.fromHtml(getString(R.string.confirm_password) + getString(R.string.req_star)));
                    confirmPasswordInput.setHint("");
                }
            }
        });

        mShowPassword1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    oldPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        mShowPassword2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        mShowPassword3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });




    }


}
