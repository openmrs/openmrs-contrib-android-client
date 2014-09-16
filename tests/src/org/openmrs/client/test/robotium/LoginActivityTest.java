package org.openmrs.client.test.robotium;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.R;

public class LoginActivityTest extends
        ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;
    private static final String LOGIN = "admin";
    private static final String PASSWORD = "Admin123";
    private static final String SERVER_URL = "http://192.168.1.115:8081/openmrs-standalone";
    private static final String WRONG_SERVER_URL = "http://192.168.1.115:8080/openmrs-standalone";
    private static final long TIMEOUT = 3000;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testEmptyFields() throws Exception {
        boolean result;
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginUsernameField);
        solo.clearEditText(loginPasswordField);

        //Empty both fields
        solo.clickOnButton(solo.getString(R.string.login_button));
        result = solo.waitForText(solo.getString(R.string.login_dialog_login_or_password_empty));
        assertTrue(result);

        //Write only login
        solo.clearEditText(loginUsernameField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginUsernameField, LOGIN);
        solo.clickOnButton(solo.getString(R.string.login_button));
        result = solo.waitForText(solo.getString(R.string.login_dialog_login_or_password_empty));
        assertTrue(result);

        //Write only password
        solo.clearEditText(loginUsernameField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, PASSWORD);
        solo.clickOnButton(solo.getString(R.string.login_button));
        result = solo.waitForText(solo.getString(R.string.login_dialog_login_or_password_empty), 1, TIMEOUT);
        assertTrue(result);
    }

    public void testLogin() throws Exception {
        //Write login
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        solo.clearEditText(loginUsernameField);
        solo.enterText(loginUsernameField, LOGIN);

        //Write password
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, PASSWORD);

        //Click on Login button
        solo.clickOnButton(solo.getString(R.string.login_button));

        //Write url
        EditText urlField = (EditText) solo.getView(R.id.openmrsEditText);
        solo.clearEditText(urlField);
        solo.enterText(urlField, SERVER_URL);

        //Click on Login button
        solo.clickOnButton(solo.getString(R.string.dialog_button_done));

        boolean result = solo.waitForText(solo.getString(R.string.login_successful));
        assertTrue(result);
    }

    public void testLoginFailed() throws Exception {
        //Write login
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        solo.clearEditText(loginUsernameField);
        solo.enterText(loginUsernameField, LOGIN);

        //Write password
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, "wrongPass");

        //Click on Login button
        solo.clickOnButton(solo.getString(R.string.login_button));

        //Write url
        EditText urlField = (EditText) solo.getView(R.id.openmrsEditText);
        solo.clearEditText(urlField);
        solo.enterText(urlField, SERVER_URL);

        //Click on Login button
        solo.clickOnButton(solo.getString(R.string.dialog_button_done));

        boolean result = solo.waitForText(solo.getString(R.string.auth_failed_dialog_message), 1, TIMEOUT);
        assertTrue(result);
    }

    public void testWrongUrl() throws Exception {
        //Write login
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        solo.clearEditText(loginUsernameField);
        solo.enterText(loginUsernameField, LOGIN);

        //Write password
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, PASSWORD);

        //Click on Login button
        solo.clickOnButton(solo.getString(R.string.login_button));

        //Write wrong url
        EditText urlField = (EditText) solo.getView(R.id.openmrsEditText);
        solo.clearEditText(urlField);
        solo.enterText(urlField, WRONG_SERVER_URL);

        //Click on Login button
        solo.clickOnButton(solo.getString(R.string.dialog_button_done));

        boolean result = solo.waitForText(solo.getString(R.string.server_unavailable_dialog_message), 1, TIMEOUT);
        assertTrue(result);
    }

    @Override
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
