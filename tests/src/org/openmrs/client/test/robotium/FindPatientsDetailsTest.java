package org.openmrs.client.test.robotium;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.CheckBox;

import com.jayway.android.robotium.solo.Solo;

import org.openmrs.client.activities.FindPatientsActivity;
import org.openmrs.client.R;
import org.openmrs.client.databases.OpenMRSSQLiteOpenHelper;

public class FindPatientsDetailsTest extends
        ActivityInstrumentationTestCase2<FindPatientsActivity> {

    private Solo solo;
    private static final String PATIENT_EXIST = "Armstrong";
    private static final String PATIENT_NO_EXIST = "Enrique";
    private static boolean isAuthenticated;
    private static final long TIMEOUT = 10000;

    public FindPatientsDetailsTest() {
        super(FindPatientsActivity.class);
    }

    @Override
    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
        if (!isAuthenticated) {
            getInstrumentation().getTargetContext().deleteDatabase(OpenMRSSQLiteOpenHelper.DATABASE_NAME);
            ((FindPatientsActivity) solo.getCurrentActivity()).moveUnauthorizedUserToLoginScreen();
            LoginHelper.login(solo);
            isAuthenticated = true;
        }
    }

    public void testPatientNotExist() throws Exception {
        SearchHelper.doSearch(getInstrumentation(), PATIENT_NO_EXIST);
        boolean result = solo.waitForText("No results found for query \"" +  PATIENT_NO_EXIST + "\"", 1, TIMEOUT * 2);
        assertTrue(result);
    }

    public void testSearchPatient() throws Exception {
        SearchHelper.doSearch(getInstrumentation(), PATIENT_EXIST);
        boolean result = solo.waitForText(PATIENT_EXIST, 1, TIMEOUT);
        assertTrue(result);
    }

    public void testSearchPatientAndSave() throws Exception {
        SearchHelper.doSearch(getInstrumentation(), PATIENT_EXIST);
        boolean result = solo.waitForText(PATIENT_EXIST, 1, TIMEOUT);
        assertTrue(result);

        CheckBox isPatientSave = (CheckBox) solo.getView(R.id.offlineCheckbox);
        assertFalse(isPatientSave.isChecked());

        solo.clickOnCheckBox(0);

        result = solo.waitForText("Available offline", 1, TIMEOUT);
        assertTrue(result);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);

        result = solo.waitForText(PATIENT_EXIST, 1, TIMEOUT);
        assertTrue(result);
    }

    @Override
    public void tearDown() {
        solo.finishOpenedActivities();
    }

}
