package org.openmrs.client.test.robotium;

import android.app.Instrumentation;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.jayway.android.robotium.solo.Solo;

public class SearchHelper {

    private Solo solo;
    private Instrumentation instrumentation;


    private SearchHelper(Solo solo, Instrumentation instrumentation) {
    }

    public static void doSearch(Instrumentation instrumentation, String query) {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_SEARCH);

        KeyCharacterMap keymap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
        for (KeyEvent key : keymap.getEvents(query.toCharArray())) {
            instrumentation.sendKeySync(key);
        }

        instrumentation.sendCharacterSync(KeyEvent.KEYCODE_ENTER);
    }
}
