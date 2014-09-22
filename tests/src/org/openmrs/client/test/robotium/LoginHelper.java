package org.openmrs.client.test.robotium;

import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public final class LoginHelper {

    public static final String LOGIN = "test1";
    public static final String PASSWORD = "Testuser1";
    public static final String SERVER_URL = "http://openmrs-ac-ci.soldevelo.com:8081/openmrs-standalone";
    public static final String LOGIN_BUTTON = "Login";
    public static final String DONE_BUTTON = "Done";
    private static final long TIMEOUT = 5000;

    private LoginHelper() {
    }

    public static boolean login(Solo solo) {
        //Write login
        EditText loginUsernameField = (EditText) solo.getView(org.openmrs.client.R.id.loginUsernameField);
        solo.clearEditText(loginUsernameField);
        solo.enterText(loginUsernameField, LOGIN);

        //Write password
        EditText loginPasswordField = (EditText) solo.getView(org.openmrs.client.R.id.loginPasswordField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, PASSWORD);

        //Click on Login button
        solo.clickOnButton(LOGIN_BUTTON);

        //Write url
        EditText urlField = (EditText) solo.getView(org.openmrs.client.R.id.openmrsEditText);
        solo.clearEditText(urlField);
        solo.enterText(urlField, SERVER_URL);

        //Click on Login button
        solo.clickOnButton(DONE_BUTTON);

        return solo.waitForText("Login successful", 1, TIMEOUT);
    }
}
