package org.openmrs.mobile.api;

import android.widget.Toast;

import org.openmrs.mobile.application.OpenMRS;

/**
 * Should be used for all notifications displayed to the user.
 */
public class Notifier {

    public void notify(String message) {
        Toast.makeText(OpenMRS.getInstance(), message, Toast.LENGTH_SHORT).show();
    }
}
