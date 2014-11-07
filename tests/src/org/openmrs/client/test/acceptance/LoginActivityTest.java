package org.openmrs.client.test.acceptance;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.R;
import org.openmrs.client.test.acceptance.helpers.LoginHelper;
import org.openmrs.client.test.acceptance.helpers.WaitHelper;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;
    private static final String WRONG_SERVER_URL = "http://openmrs-ac-ci.soldevelo.com:8080/openmrs-standalone";
    private static final String WRONG_PASSWORD = "Testuser";
    private static final String EMPTY_FIELD = "Login and password can not be empty.";
    private static boolean isURLSet;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws java.lang.Exception {
        super.setUp();

        solo = new Solo(getInstrumentation());
        getActivity();
        getInstrumentation().waitForIdleSync();

        if (!isURLSet) {
            isURLSet = LoginHelper.setURL(solo);
            assertTrue(isURLSet);
        }
    }

    public void testEmptyFields() throws Exception {
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        View loginButton = solo.getView(R.id.loginButton);

        assertTrue(LoginHelper.setLocation(solo));

        //Empty both fields
        solo.clearEditText(loginUsernameField);
        solo.clearEditText(loginPasswordField);

        //Click on Login button
        WaitHelper.waitForText(solo, LoginHelper.LOGIN_BUTTON);
        assertTrue(loginButton.isEnabled());
        solo.clickOnView(loginButton);

        assertTrue(WaitHelper.waitForText(solo, EMPTY_FIELD));

        //Empty password field
        solo.clearEditText(loginUsernameField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginUsernameField, LoginHelper.LOGIN);

        //Click on Login button
        solo.clickOnView(loginButton);

        assertTrue(WaitHelper.waitForText(solo, EMPTY_FIELD));

        //Empty login field
        solo.clearEditText(loginUsernameField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, LoginHelper.PASSWORD);

        //Click on Login button
        solo.clickOnView(loginButton);

        assertTrue(WaitHelper.waitForText(solo, EMPTY_FIELD));
    }

    public void testLoginFailed() throws Exception {
        //Write login
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        solo.clearEditText(loginUsernameField);
        solo.enterText(loginUsernameField, LoginHelper.LOGIN);

        //Write password
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, WRONG_PASSWORD);

        //Set location
        assertTrue(LoginHelper.setLocation(solo));

        //Click on Login button
        View loginButton = solo.getView(org.openmrs.client.R.id.loginButton);
        WaitHelper.waitForText(solo, LoginHelper.LOGIN_BUTTON);
        assertTrue(loginButton.isEnabled());
        solo.clickOnView(loginButton);

        assertTrue(WaitHelper.waitForText(solo, "Your user name or password may be incorrect. Please try again."));
    }

    public void testWrongUrl() throws Exception {
        WaitHelper.waitForView(solo, org.openmrs.client.R.id.urlEdit);
        View editURLButton = solo.getView(org.openmrs.client.R.id.urlEdit);
        solo.clickOnView(editURLButton);
        assertTrue(WaitHelper.waitForText(solo, LoginHelper.URL_DIALOG));

        EditText urlField = (EditText) solo.getView(R.id.openmrsEditText);
        solo.clearEditText(urlField);
        solo.enterText(urlField, WRONG_SERVER_URL);

        //Click on Done button
        View doneButton = solo.getView(org.openmrs.client.R.id.dialogFormButtonsSubmitButton);
        WaitHelper.waitForText(solo, LoginHelper.DONE_BUTTON);
        solo.clickOnView(doneButton);

        assertTrue(WaitHelper.waitForText(solo, "Cancel"));
    }

    public void testLogin() throws Exception {
        assertTrue(LoginHelper.login(solo));
    }

    @Override
    public void tearDown() throws java.lang.Exception  {
        solo.finishOpenedActivities();
        super.tearDown();
    }
}
