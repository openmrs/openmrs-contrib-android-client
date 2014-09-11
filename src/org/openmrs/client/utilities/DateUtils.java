package org.openmrs.client.utilities;

import org.openmrs.client.application.OpenMRS;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_WITH_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    private static final String OPEN_MRS_RESPONSE_FORMAT = "yyyy-MM-dd'T'HH:mm";

    public static final Long ZERO = 0L;

    private DateUtils() {

    }

    public static String convertTime(long time, String dateFormat) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }

    public static String convertTime(long timestamp) {
        return convertTime(timestamp, DEFAULT_DATE_FORMAT);
    }

    public static Long convertTime(String dateAsString) {
        Long time = null;
        if (StringUtils.notNull(dateAsString)) {
            DateFormat format = new SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT);
            Date formattedDate;
            try {
                formattedDate = format.parse(dateAsString);
                time = formattedDate.getTime();
            } catch (ParseException e) {
                OpenMRS.getInstance().getOpenMRSLogger().w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
            } catch (NullPointerException e) {
                OpenMRS.getInstance().getOpenMRSLogger().w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
            }
        }
        return time;
    }
}
