package org.openmrs.client.test.acceptance;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.CheckBox;

import com.jayway.android.robotium.solo.Solo;

import org.openmrs.client.activities.DashboardActivity;
import org.openmrs.client.activities.FindPatientsActivity;
import org.openmrs.client.R;
import org.openmrs.client.activities.FindPatientsSearchActivity;
import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.databases.OpenMRSSQLiteOpenHelper;
import org.openmrs.client.test.acceptance.helpers.LoginHelper;
import org.openmrs.client.test.acceptance.helpers.SearchHelper;
import org.openmrs.client.test.acceptance.helpers.WaitHelper;

public class FindPatientsDetailsTest extends ActivityInstrumentationTestCase2<DashboardActivity> {

    private Solo solo;
    private static final String PATIENT_EXIST = "Paul";
    private static final String PATIENT_NO_EXIST = "Enrique";
    private static boolean isAuthenticated;

    public FindPatientsDetailsTest() {
        super(DashboardActivity.class);
    }

    @Override
    public void setUp() throws java.lang.Exception {
        super.setUp();
        if (!isAuthenticated) {
            getInstrumentation().getTargetContext().deleteDatabase(OpenMRSSQLiteOpenHelper.DATABASE_NAME);
        }
        solo = new Solo(getInstrumentation());
        getActivity();
        getInstrumentation().waitForIdleSync();

        if (!isAuthenticated) {
            if (!WaitHelper.waitForActivity(solo, LoginActivity.class)) {
                ((DashboardActivity) solo.getCurrentActivity()).moveUnauthorizedUserToLoginScreen();
            }
            assertTrue(WaitHelper.waitForActivity(solo, LoginActivity.class));

            LoginHelper.login(solo);
            isAuthenticated = true;
            assertTrue(WaitHelper.waitForActivity(solo, DashboardActivity.class));
        }

        assertTrue(WaitHelper.waitForText(solo, "Find Patients"));
        solo.clickOnText("Find Patients");
        assertTrue(WaitHelper.waitForActivity(solo, FindPatientsActivity.class));

        WaitHelper.waitForViewVisibilityChange(solo, solo.getView(R.id.patientListViewLoading),
                View.VISIBLE, WaitHelper.TIMEOUT_SIXTY_SECOND);
    }

    public void testPatientNotExist() throws Exception {
        SearchHelper.doSearch(solo, PATIENT_NO_EXIST, "Patient name");
        assertTrue(WaitHelper.waitForActivity(solo, FindPatientsSearchActivity.class));
        assertTrue(WaitHelper.waitForText(solo, "No results found for query \"" +  PATIENT_NO_EXIST + "\""));
    }

    public void testSearchPatient() throws Exception {
        SearchHelper.doSearch(solo, PATIENT_EXIST, "Patient name");

        assertTrue(WaitHelper.waitForActivity(solo, FindPatientsSearchActivity.class));

        assertTrue(WaitHelper.waitForText(solo, PATIENT_EXIST));
    }

    public void testSearchPatientAndSave() throws Exception {
        SearchHelper.doSearch(solo, PATIENT_EXIST, "Patient name");

        assertTrue(WaitHelper.waitForActivity(solo, FindPatientsSearchActivity.class));

        assertTrue(WaitHelper.waitForText(solo, PATIENT_EXIST));

        CheckBox isPatientSave = (CheckBox) solo.getView(R.id.offlineCheckbox);
        assertTrue(WaitHelper.waitForText(solo, "Download"));
        assertFalse(isPatientSave.isChecked());

        solo.clickOnCheckBox(0);

        assertTrue(WaitHelper.waitForText(solo, "Available offline"));

        solo.goBackToActivity("FindPatientsActivity");
        assertTrue(WaitHelper.waitForActivity(solo, FindPatientsActivity.class));

        WaitHelper.waitForViewVisibilityChange(solo, solo.getView(R.id.patientListViewLoading),
                View.VISIBLE, WaitHelper.TIMEOUT_SIXTY_SECOND);

        assertTrue(WaitHelper.waitForText(solo, "Downloaded"));
        solo.clickOnText("Downloaded");
        assertTrue(WaitHelper.waitForText(solo, PATIENT_EXIST));
        solo.goBackToActivity("DashboardActivity");
        assertTrue(WaitHelper.waitForActivity(solo, DashboardActivity.class));
    }

    @Override
    public void tearDown() throws java.lang.Exception  {
        solo.finishOpenedActivities();
        super.tearDown();
    }

}
