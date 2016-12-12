package org.openmrs.mobile.utilities;

import android.widget.EditText;

public class ViewUtils {

    public static String getInput(EditText e) {
        if(e.getText() == null) {
            return null;
        } else if (isEmpty(e)) {
            return null;
        } else {
            return e.getText().toString();
        }
    }

    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

}
