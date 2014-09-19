package org.openmrs.client.test.robotium;

import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public final class LoginHelper {

    public static final String LOGIN = "test1";
    public static final String PASSWORD = "Testuser1";
    public static final String SERVER_URL = "http://openmrs-ac-ci.soldevelo.com:8081/openmrs-standalone";
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
        solo.clickOnButton(solo.getString(org.openmrs.client.R.string.login_button));

        //Write url
        EditText urlField = (EditText) solo.getView(org.openmrs.client.R.id.openmrsEditText);
        solo.clearEditText(urlField);
        solo.enterText(urlField, SERVER_URL);

        //Click on Login button
        solo.clickOnButton(solo.getString(org.openmrs.client.R.string.dialog_button_done));

        return solo.waitForText(solo.getString(org.openmrs.client.R.string.login_successful), 1, TIMEOUT);
    }
}
