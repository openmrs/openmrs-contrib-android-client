package org.openmrs.client.test.robotium;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import junit.framework.Assert;

import org.openmrs.client.activities.ACBaseActivity;
import org.openmrs.client.activities.FindPatientsActivity;

public class LoginHelper {

    private static final String LOGIN = "admin";
    private static final String PASSWORD = "Admin12";
    private static final String SERVER_URL = "http://192.168.1.115:8081/openmrs-standalone";
    private static final long TIMEOUT = 5000;

    public static void login(Solo solo) {
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

        boolean result = solo.waitForText(solo.getString(org.openmrs.client.R.string.login_successful), 1, TIMEOUT);
        Assert.assertTrue(result);
    }
}
