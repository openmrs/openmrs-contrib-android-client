package org.openmrs.client.utilities;

public final class StringUtils {
    public static final String NULL_AS_STRING = "null";
    public static final String NEW_LINE = "\n";
    public static final String SPACE_CHAR = " ";

    private StringUtils() {
    }

    public static boolean notNull(String string) {
        return null != string && !NULL_AS_STRING.equals(string);
    }
}
