package org.openmrs.client.test.robotium;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.R;

public class LoginActivityTest extends
        ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;
    private static final String WRONG_SERVER_URL = "http://openmrs-ac-ci.soldevelo.com:8080/openmrs-standalone";
    private static final String WRONG_PASSWORD = "Testuser";
    private static final long TIMEOUT = 10000;
    private static final String EMPTY_FIELD = "Login and password can not be empty.";

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testEmptyBothFields() throws Exception {
        boolean result;
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginUsernameField);
        solo.clearEditText(loginPasswordField);

        //Empty both fields
        solo.clickOnButton(LoginHelper.LOGIN_BUTTON);
        result = solo.waitForText(EMPTY_FIELD, 1, TIMEOUT);
        assertTrue(result);
    }

    public void testEmptyPassField() throws Exception {
        boolean result;
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginUsernameField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginUsernameField, LoginHelper.LOGIN);
        solo.clickOnButton(LoginHelper.LOGIN_BUTTON);
        result = solo.waitForText(EMPTY_FIELD, 1, TIMEOUT);
        assertTrue(result);
    }

    public void testEmptyLoginField() throws Exception {
        boolean result;
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginUsernameField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, LoginHelper.PASSWORD);
        solo.clickOnButton(LoginHelper.LOGIN_BUTTON);
        result = solo.waitForText(EMPTY_FIELD, 1, TIMEOUT);
        assertTrue(result);
    }

    public void testLogin() throws Exception {
        boolean result = LoginHelper.login(solo);
        assertTrue(result);
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

        //Click on Login button
        solo.clickOnButton(LoginHelper.LOGIN_BUTTON);

        //Write url
        EditText urlField = (EditText) solo.getView(R.id.openmrsEditText);
        solo.clearEditText(urlField);
        solo.enterText(urlField, LoginHelper.SERVER_URL);

        //Click on Login button
        solo.clickOnButton(LoginHelper.DONE_BUTTON);

        boolean result = solo.waitForText("Your user name or password may be incorrect. Please try again.", 1, TIMEOUT);
        assertTrue(result);
    }

    public void testWrongUrl() throws Exception {
        //Write login
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        solo.clearEditText(loginUsernameField);
        solo.enterText(loginUsernameField, LoginHelper.LOGIN);

        //Write password
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, LoginHelper.PASSWORD);

        //Click on Login button
        solo.clickOnButton(LoginHelper.LOGIN_BUTTON);

        //Write wrong url
        EditText urlField = (EditText) solo.getView(R.id.openmrsEditText);
        solo.clearEditText(urlField);
        solo.enterText(urlField, WRONG_SERVER_URL);

        //Click on Login button
        solo.clickOnButton(LoginHelper.DONE_BUTTON);

        boolean result = solo.waitForText("Cancel", 1, TIMEOUT * 2);
        assertTrue(result);
    }

    @Override
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
