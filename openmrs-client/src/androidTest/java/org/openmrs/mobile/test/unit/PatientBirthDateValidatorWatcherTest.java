package org.openmrs.mobile.test.unit;

import android.test.InstrumentationTestCase;
import android.text.TextWatcher;
import android.widget.EditText;

import org.openmrs.mobile.listeners.watcher.PatientBirthdateValidatorWatcher;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class PatientBirthDateValidatorWatcherTest extends InstrumentationTestCase {

    private EditText mMonthEditText;
    private EditText mYearEditText;

    @Override
    public void setUp() {
        EditText birthdateEditText = new EditText(this.getInstrumentation().getContext());
        mMonthEditText = new EditText(this.getInstrumentation().getContext());
        mYearEditText = new EditText(this.getInstrumentation().getContext());

        TextWatcher textWatcher = new PatientBirthdateValidatorWatcher(birthdateEditText, mMonthEditText, mYearEditText);

        mMonthEditText.addTextChangedListener(textWatcher);
        mYearEditText.addTextChangedListener(textWatcher);
    }

    public void test_shouldConvertMonthsToYears() {
        final String monthsInTenYears = "120";
        final String tenYears = "10";

        mYearEditText.getText().clear();
        mMonthEditText.setText(monthsInTenYears);
        assertEquals(mYearEditText.getText().toString(), tenYears);
    }

    public void test_shouldWipeYearsAndMonthInputWhenYearInputWasTooBig() {
        final String yearsLessThanMaxAllowed = String.valueOf(ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE / 2);
        final String yearsMoreThanMaxAllowed = String.valueOf(ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE * 2);

        mYearEditText.setText(yearsLessThanMaxAllowed);
        assertEquals(mYearEditText.getText().toString(), yearsLessThanMaxAllowed);
        mYearEditText.setText(yearsMoreThanMaxAllowed);
        assertEquals(mYearEditText.getText().toString(), ApplicationConstants.EMPTY_STRING);
    }

}
