package org.openmrs.client.test.acceptance.helpers;

import android.view.View;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import org.openmrs.client.R;

import static junit.framework.Assert.assertTrue;

public final class LoginHelper {

    public static final String LOGIN = "admin";
    public static final String PASSWORD = "Admin123";
    public static final String SERVER_URL = "http://openmrs-ac-ci.soldevelo.com:8081/openmrs-standalone";
    public static final String LOGIN_BUTTON = "Login";
    public static final String DONE_BUTTON = "Done";
    public static final String URL_DIALOG = "Enter URL address";

    private LoginHelper() {
    }

    public static boolean login(Solo solo) throws java.lang.Exception {
        assertTrue(setURL(solo));

        //Write login
        EditText loginUsernameField = (EditText) solo.getView(R.id.loginUsernameField);
        solo.clearEditText(loginUsernameField);
        solo.enterText(loginUsernameField, LOGIN);

        //Write password
        EditText loginPasswordField = (EditText) solo.getView(R.id.loginPasswordField);
        solo.clearEditText(loginPasswordField);
        solo.enterText(loginPasswordField, PASSWORD);

        //Set location
        assertTrue(LoginHelper.setLocation(solo));

        //Click on Login button
        View loginButton = solo.getView(org.openmrs.client.R.id.loginButton);
        WaitHelper.waitForText(solo, LoginHelper.LOGIN_BUTTON);
        assertTrue(loginButton.isEnabled());
        solo.clickOnView(loginButton);

        return WaitHelper.waitForText(solo, "Login successful");
    }

    public static boolean setURL(Solo solo) throws java.lang.Exception {
        //check if dialog for url is visible
        if (!WaitHelper.waitForText(solo, URL_DIALOG)) {
            //if not, check if a change is required
            if (!WaitHelper.waitForText(solo, SERVER_URL)) {
                WaitHelper.waitForView(solo, org.openmrs.client.R.id.urlEdit);
                View editURLButton = solo.getView(org.openmrs.client.R.id.urlEdit);
                solo.clickOnView(editURLButton);
            } else {
                return true;
            }
        }
        assertTrue(WaitHelper.waitForText(solo, URL_DIALOG));

        //Write url
        EditText urlField = (EditText) solo.getView(R.id.openmrsEditText);
        solo.clearEditText(urlField);
        solo.enterText(urlField, SERVER_URL);

        //Click on Done button
        View doneButton = solo.getView(org.openmrs.client.R.id.dialogFormButtonsSubmitButton);
        WaitHelper.waitForText(solo, LoginHelper.DONE_BUTTON);
        solo.clickOnView(doneButton);

        solo.sleep(WaitHelper.TIMEOUT_ONE_SECOND);

        return WaitHelper.waitForText(solo, SERVER_URL);
    }

    public static boolean setLocation(Solo solo) throws java.lang.Exception {
        if (WaitHelper.waitForText(solo, "Choose location for this session")) {
            solo.pressSpinnerItem(0, 0);
        }
        return !WaitHelper.waitForText(solo, "Choose location for this session");
    }
}
