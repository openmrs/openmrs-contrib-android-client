package org.openmrs.client.test.unit;

import android.test.InstrumentationTestCase;

import org.openmrs.client.utilities.DateUtils;

public class DateUtilsTest extends InstrumentationTestCase {
    private static final String INITIAL_DATA_1;
    private static final String INITIAL_DATA_2;
    private static final String EXPECTED_DATA_1;
    private static final String EXPECTED_DATA_2;
    private static final String INVALID_DATA_3;
    private static final String INVALID_DATA_4;
    private static final String EXPECTED_LONG_3;

    @Override
    public void setUp() throws java.lang.Exception {
        super.setUp();
        getInstrumentation().waitForIdleSync();
    }

    static {
        INITIAL_DATA_1 = "1967-03-26T00:00:00.000+0200";
        EXPECTED_DATA_1 = "26/03/1967";
        INITIAL_DATA_2 = "1990-03-24T00:00";
        EXPECTED_DATA_2 = "24/03/1990";

        INVALID_DATA_3 = "09-07-1697T00:00";
        EXPECTED_LONG_3 = "598597200000";
        INVALID_DATA_4 = "1988/12/20";
    }

    public void testDateUtils() {
        Long stringToLongResult;
        String longToStringResult;

        stringToLongResult = DateUtils.convertTime(INITIAL_DATA_1);
        longToStringResult = DateUtils.convertTime(stringToLongResult);
        assertEquals(EXPECTED_DATA_1, longToStringResult);

        stringToLongResult = DateUtils.convertTime(INITIAL_DATA_2);
        longToStringResult = DateUtils.convertTime(stringToLongResult);
        assertEquals(EXPECTED_DATA_2, longToStringResult);

        stringToLongResult = DateUtils.convertTime(INVALID_DATA_3);
        assertNotSame(EXPECTED_LONG_3, String.valueOf(stringToLongResult));

        stringToLongResult = DateUtils.convertTime(INVALID_DATA_4);
        assertNull(stringToLongResult);
    }
}
