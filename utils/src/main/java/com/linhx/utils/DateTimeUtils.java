package com.linhx.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateTimeUtils
 *
 * @author linhx
 * @since 18/08/2021
 */
public class DateTimeUtils {
    public static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    public static DateFormat DATE_TIME_FORMAT_ISO = new SimpleDateFormat(ISO_DATE_TIME_PATTERN);

    public static boolean equals(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        }
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.getTime() == date2.getTime();
    }

    public static boolean equals(LocalDate date1, LocalDate date2) {
        if (date1 == null && date2 == null) {
            return true;
        }
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.equals(date2);
    }

    public static String formatIso(Date date) {
        return DATE_TIME_FORMAT_ISO.format(date);
    }

    public static Date dateFromNow(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, offset);
        return calendar.getTime();
    }

    public static Timestamp timestampNow () {
        return Timestamp.from(Instant.now());
    }
}
