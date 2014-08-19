package org.openmrs.client.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URLValidator {
    private static final String URL_PATTERN = "^(https?:\\/\\/){1}([\\da-z\\.-]*)\\.([a-z\\.]*)([\\w \\.-]*)*(:([0-9]{2,5}))?((\\/(\\w*))*?)\\/*$";
    private static final String SLASH = "/";

    private URLValidator() {
    }

    public static ValidationResult validate(String url) {
        ValidationResult result;
        Pattern urlPattern = Pattern.compile(URL_PATTERN);
        Matcher matcher = urlPattern.matcher(url);
        String validURL = url;
        if (matcher.matches()) {
            validURL = trimLastSlash(validURL);
            result = new ValidationResult(true, validURL);
        } else {
            result = new ValidationResult(false, validURL);
        }
        return result;
    }

    public static String trimLastSlash(String url) {
        String validUrl = url;
        while (validUrl.endsWith(SLASH)) {
            validUrl = validUrl.substring(0, validUrl.lastIndexOf(SLASH));
        }
        return validUrl;
    }

    public static class ValidationResult {
        private final boolean isURLValid;
        private final String url;

        public ValidationResult(boolean isValid, String url) {
            this.isURLValid = isValid;
            this.url = url;
        }

        public boolean isURLValid() {
            return isURLValid;
        }

        public String getUrl() {
            return url;
        }

    }
}
