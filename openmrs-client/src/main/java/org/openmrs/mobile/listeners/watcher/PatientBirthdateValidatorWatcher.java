package org.openmrs.mobile.listeners.watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

public class PatientBirthdateValidatorWatcher implements TextWatcher{

    private EditText eddob;
    private EditText edmonth;
    private EditText edyr;

    public PatientBirthdateValidatorWatcher(EditText dateOfBirthEditText, EditText monthEditText, EditText yearsEditText) {
        this.eddob = dateOfBirthEditText;
        this.edmonth = monthEditText;
        this.edyr = yearsEditText;
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
        eddob.getText().clear();
        if (editable.length() > 3) {
            ToastUtil.error("Input is too big");
            edmonth.getText().clear();
            edyr.getText().clear();
        }
        else {
            if (StringUtils.notEmpty(editable.toString()) && Integer.parseInt(editable.toString()) > ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE) {
                ToastUtil.error("Patient's age must be between 0 and " + ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE);
                edmonth.getText().clear();
                edyr.getText().clear();
            }
        }

        int monthValue = 0;
        if (StringUtils.notEmpty(edmonth.getText().toString())) {
            monthValue = Integer.parseInt(edmonth.getText().toString());
        }
        if (monthValue >= 12) {
            edmonth.setText(String.valueOf(monthValue % 12));
            edyr.setText(String.valueOf(monthValue / 12));
        }
    }
}
