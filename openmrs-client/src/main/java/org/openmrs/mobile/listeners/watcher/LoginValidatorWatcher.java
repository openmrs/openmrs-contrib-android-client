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

package org.openmrs.mobile.listeners.watcher;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.login.LocationArrayAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.StringUtils;

//Class used to extract view validation logic
public class LoginValidatorWatcher implements TextWatcher, AdapterView.OnItemSelectedListener {

    private EditText mUrl;
    private EditText mUsername;
    private EditText mPassword;
    private Spinner mLocation;

    private Button mLoginButton;

    private boolean urlChanged;
    private boolean locationErrorOccurred;

    public LoginValidatorWatcher(EditText urlEditText, EditText usernameEditText, EditText passwordEditText, Spinner locationSpinner, Button loginButton) {
        this.mUrl = urlEditText;
        this.mUsername = usernameEditText;
        this.mPassword = passwordEditText;
        this.mLocation = locationSpinner;
        this.mLoginButton = loginButton;

        mUrl.addTextChangedListener(this);
        mUsername.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
        mLocation.setOnItemSelectedListener(this);
    }

    public boolean isUrlChanged() {
        return urlChanged;
    }

    public void setUrlChanged(boolean urlChanged) {
        this.urlChanged = urlChanged;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // This method is intentionally empty
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // This method is intentionally empty
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // If URL text changed
        if (editable.toString().hashCode() == mUrl.getText().toString().hashCode()) {
            urlChanged(editable);
        }
        mLoginButton.setEnabled(isAllDataValid());
    }

    private void urlChanged(Editable editable) {
        if ((!OpenMRS.getInstance().getServerUrl().equals(editable.toString())) && StringUtils.notEmpty(editable.toString())) {
            setUrlChanged(true);
        }
        else if (OpenMRS.getInstance().getServerUrl().equals(editable.toString())) {
            setUrlChanged(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0 && id >= 1) {
            ((LocationArrayAdapter) parent.getAdapter()).notifyDataSetChanged();
            //Set Text Color to black once option selected
            TextView currentText = (TextView) parent.getChildAt(0);
            if (currentText != null) {
                currentText.setTextColor(Color.BLACK);
            }
        } else if (position >= 0 && id == 0) {
            //If spinner is at start option, append a red * to signify requirement
            TextView currentText = (TextView) parent.getChildAt(0);
            if (currentText != null) {
                currentText.setText(Html.fromHtml(view.getContext().getString(R.string.login_location_select)
                        + view.getContext().getString(R.string.req_star)));
            }
        }
        mLoginButton.setEnabled(isAllDataValid());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // This method is intentionally empty
    }

    private boolean isAllDataValid() {

        boolean result = validateNotEmpty(mUsername) && validateNotEmpty(mPassword) && !urlChanged;

        if (locationErrorOccurred && urlChanged) {
            mLocation.setEnabled(false);
            mLocation.setVisibility(View.GONE);
        }
        else {
            mLocation.setEnabled(true);
            mLocation.setVisibility(View.VISIBLE);
        }

        if (!result && (!locationErrorOccurred && urlChanged)) {
            mLocation.setEnabled(false);
            mLocation.setVisibility(View.GONE);
        }

        return result;
    }

    private boolean validateNotEmpty(EditText editText) {
        return StringUtils.notEmpty(editText.getText().toString());
    }

    public void setLocationErrorOccurred(boolean locationErrorOccurred) {
        this.locationErrorOccurred = locationErrorOccurred;
    }

    public boolean isLocationErrorOccurred() {
        return locationErrorOccurred;
    }
}
