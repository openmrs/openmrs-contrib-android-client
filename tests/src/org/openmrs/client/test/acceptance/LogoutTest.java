package org.openmrs.client.test.acceptance;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import org.openmrs.client.activities.DashboardActivity;
import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.test.acceptance.helpers.LoginHelper;
import org.openmrs.client.test.acceptance.helpers.WaitHelper;

public class LogoutTest extends ActivityInstrumentationTestCase2<DashboardActivity> {

    private Solo solo;
    private static final String LOGOUT = "Logout";

    public LogoutTest() {
        super(DashboardActivity.class);
    }

    @Override
    public void setUp() throws java.lang.Exception {
        super.setUp();

        solo = new Solo(getInstrumentation());
        getActivity();
        getInstrumentation().waitForIdleSync();
        if (WaitHelper.waitForActivity(solo, LoginActivity.class)) {
            LoginHelper.login(solo);
        }
    }

    public void testLogout() throws Exception {
        solo.assertCurrentActivity("Wrong activity. DashboardActivity expected", DashboardActivity.class);

        //open menu
        solo.clickOnMenuItem(LOGOUT);

        assertTrue(WaitHelper.waitForText(solo, "Logging out"));

        //Click on Cancel button
        assertTrue(WaitHelper.waitForText(solo, "Cancel"));
        solo.clickOnButton("Cancel");

        solo.assertCurrentActivity("Wrong activity. DashboardActivity expected", DashboardActivity.class);

        solo.clickOnMenuItem(LOGOUT);

        //wait for Logout dialog
        assertTrue(WaitHelper.waitForText(solo, "Logging out"));

        //Click on Logout button
        assertTrue(WaitHelper.waitForText(solo, LOGOUT));
        solo.clickOnButton(LOGOUT);

        assertTrue(WaitHelper.waitForActivity(solo, LoginActivity.class));
        solo.assertCurrentActivity("Wrong activity. LoginActivity expected", LoginActivity.class);
    }

    @Override
    public void tearDown() throws java.lang.Exception  {
        solo.finishOpenedActivities();
        super.tearDown();
    }
}
