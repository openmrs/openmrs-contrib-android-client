package org.openmrs.client.test.robotium;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.CheckBox;

import com.jayway.android.robotium.solo.Solo;

import org.openmrs.client.activities.FindPatientsActivity;
import org.openmrs.client.R;

public class FindPatientsDetailsTest extends
        ActivityInstrumentationTestCase2<FindPatientsActivity> {

    private Solo solo;
    private static final String PATIENT_EXIST = "John";
    private static final String PATIENT_NO_EXIST = "Enrique";
    private static boolean isAuthenticated;

    public FindPatientsDetailsTest() {
        super(FindPatientsActivity.class);
    }

    @Override
    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
        if (!isAuthenticated) {
            ((FindPatientsActivity) solo.getCurrentActivity()).moveUnauthorizedUserToLoginScreen();
            LoginHelper.login(solo);
            isAuthenticated = true;
        }
    }

    public void testPatientNotExist() throws Exception {
        SearchHelper.doSearch(getInstrumentation(), PATIENT_NO_EXIST);
        boolean result = solo.waitForText(solo.getString(R.string.search_patient_no_result_for_query) +  PATIENT_NO_EXIST + "\"", 1, 5000);
        assertTrue(result);
    }

    public void testSearchPatient() throws Exception {
        SearchHelper.doSearch(getInstrumentation(), PATIENT_EXIST);
        boolean result = solo.waitForText(PATIENT_EXIST, 1, 3000);
        assertTrue(result);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);

        result = solo.waitForText(PATIENT_EXIST, 1, 3000);
        assertTrue(result);
    }

    public void testSearchPatientAndSave() throws Exception {
        SearchHelper.doSearch(getInstrumentation(), PATIENT_EXIST);
        boolean result = solo.waitForText(PATIENT_EXIST, 1, 3000);
        assertTrue(result);

        CheckBox isPatientSave = (CheckBox) solo.getView(R.id.offlineCheckbox);
        assertFalse(isPatientSave.isChecked());

        solo.clickOnCheckBox(0);

        result = solo.waitForText(solo.getString(R.string.find_patients_row_checkbox_available_offline_label));
        assertTrue(result);
        assertTrue(isPatientSave.isChecked());

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);

        result = solo.waitForText(PATIENT_EXIST);
        assertTrue(result);
    }

    @Override
    public void tearDown() {
        solo.finishOpenedActivities();
    }

}
