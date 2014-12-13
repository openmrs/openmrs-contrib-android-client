package org.openmrs.client.test.acceptance.helpers;

import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import org.openmrs.client.R;

public final class SearchHelper {

    private SearchHelper() {
    }

    public static void doSearch(Solo solo, String query, String searchHint) throws java.lang.Exception {
        solo.clickOnActionBarItem(R.id.actionSearch);
        WaitHelper.waitForText(solo, searchHint);
        EditText search = solo.getEditText(searchHint);
        solo.enterText(search, query);
        solo.sendKey(Solo.ENTER);
    }
}
