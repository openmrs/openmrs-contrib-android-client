package org.openmrs.client.utilities;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String OPEN_MRS_RESPONSE_FORMAT = "yyyy-MM-dd";

    private DateUtils() {

    }

    public static String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(date);
    }

    public static Long convertTime(String dateAsString) {
        DateFormat format = new SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT);
        Long time;
        Date formattedDate;
        try {
            formattedDate = format.parse(dateAsString);
            time = formattedDate.getTime();
        } catch (ParseException e) {
            time = null;
        }
        return time;
    }
}
