package org.openmrs.mobile.test.unit;

import android.test.InstrumentationTestCase;
import android.text.TextWatcher;
import android.widget.EditText;

import org.openmrs.mobile.listeners.watcher.PatientBirthdateValidatorWatcher;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class PatientBirthDateValidatorWatcherTest extends InstrumentationTestCase {

    private EditText birthdateEditText;
    private EditText monthEditText;
    private EditText yearEditText;

    @Override
    public void setUp() {
        birthdateEditText = new EditText(this.getInstrumentation().getContext());
        monthEditText = new EditText(this.getInstrumentation().getContext());
        yearEditText = new EditText(this.getInstrumentation().getContext());

        TextWatcher textWatcher = new PatientBirthdateValidatorWatcher(birthdateEditText, monthEditText, yearEditText);

        monthEditText.addTextChangedListener(textWatcher);
        yearEditText.addTextChangedListener(textWatcher);
    }

    public void test_shouldConvertMonthsToYears() {
        final String monthsInTenYears = "120";
        final String tenYears = "10";

        yearEditText.getText().clear();
        monthEditText.setText(monthsInTenYears);
        assertEquals(yearEditText.getText().toString(), tenYears);
    }

    public void test_shouldWipeYearsAndMonthInputWhenYearInputWasTooBig() {
        final String yearsLessThanMaxAllowed = String.valueOf(ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE / 2);
        final String yearsMoreThanMaxAllowed = String.valueOf(ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE * 2);

        yearEditText.setText(yearsLessThanMaxAllowed);
        assertEquals(yearEditText.getText().toString(), yearsLessThanMaxAllowed);
        yearEditText.setText(yearsMoreThanMaxAllowed);
        assertEquals(yearEditText.getText().toString(), ApplicationConstants.EMPTY_STRING);
    }

}
