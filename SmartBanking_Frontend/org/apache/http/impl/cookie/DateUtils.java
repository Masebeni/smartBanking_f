package org.apache.http.impl.cookie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateUtils {
    private static final String[] DEFAULT_PATTERNS;
    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    public static final TimeZone GMT;
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    static {
        DEFAULT_PATTERNS = new String[]{PATTERN_ASCTIME, PATTERN_RFC1036, PATTERN_RFC1123};
        GMT = TimeZone.getTimeZone("GMT");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(GMT);
        calendar.set(2000, 0, 1, 0, 0, 0);
        calendar.set(14, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    public static Date parseDate(String dateValue) throws DateParseException {
        return parseDate(dateValue, null, null);
    }

    public static Date parseDate(String dateValue, String[] dateFormats) throws DateParseException {
        return parseDate(dateValue, dateFormats, null);
    }

    public static Date parseDate(String dateValue, String[] dateFormats, Date startDate) throws DateParseException {
        if (dateValue == null) {
            throw new IllegalArgumentException("dateValue is null");
        }
        if (dateFormats == null) {
            dateFormats = DEFAULT_PATTERNS;
        }
        if (startDate == null) {
            startDate = DEFAULT_TWO_DIGIT_YEAR_START;
        }
        if (dateValue.length() > 1 && dateValue.startsWith("'") && dateValue.endsWith("'")) {
            dateValue = dateValue.substring(1, dateValue.length() - 1);
        }
        SimpleDateFormat dateParser = null;
        int i = 0;
        while (i < dateFormats.length) {
            if (dateParser == null) {
                dateParser = new SimpleDateFormat(dateFormats[i], Locale.US);
                dateParser.setTimeZone(TimeZone.getTimeZone("GMT"));
                dateParser.set2DigitYearStart(startDate);
            } else {
                dateParser.applyPattern(dateFormats[i]);
            }
            try {
                return dateParser.parse(dateValue);
            } catch (ParseException e) {
                i++;
            }
        }
        throw new DateParseException(new StringBuffer().append("Unable to parse the date ").append(dateValue).toString());
    }

    public static String formatDate(Date date) {
        return formatDate(date, PATTERN_RFC1123);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        } else if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);
            formatter.setTimeZone(GMT);
            return formatter.format(date);
        }
    }

    private DateUtils() {
    }
}
