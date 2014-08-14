package org.openmrs.client.utilities;

public final class URLUtils {
    private static final String SLASH = "/";

    private URLUtils() {
    }

    public static String trimLastSlash(String url) {
        String validUrl = url;
        while (validUrl.endsWith(SLASH)) {
            validUrl = validUrl.substring(0, validUrl.lastIndexOf(SLASH));
        }
        return validUrl;
    }
}
