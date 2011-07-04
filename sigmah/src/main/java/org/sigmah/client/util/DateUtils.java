package org.sigmah.client.util;

import java.util.Comparator;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Utility class to manipulates dates.
 * 
 * @author tmi
 * 
 */
public final class DateUtils {

    private DateUtils() {
    }

    public static final DateTimeFormat DATE_SHORT = DateTimeFormat.getShortDateFormat();

    public static final DateTimeFormat DATE_TIME_SHORT = DateTimeFormat.getShortDateTimeFormat();

    /**
     * Compare two dates. The comparison ignores hours, minutes and seconds.
     */
    public static final Comparator<Date> DAY_COMPARATOR = new Comparator<Date>() {

        @SuppressWarnings("deprecation")
        @Override
        public int compare(Date d1, Date d2) {

            if (d1 == null) {
                if (d2 == null) {
                    return 0;
                } else {
                    return -1;
                }
            }

            if (d2 == null) {
                return 1;
            }

            if (d1.getYear() < d2.getYear()) {
                return -1;
            } else if (d1.getYear() > d2.getYear()) {
                return 1;
            } else {
                if (d1.getMonth() < d2.getMonth()) {
                    return -1;
                } else if (d1.getMonth() > d2.getMonth()) {
                    return 1;
                } else {
                    if (d1.getDate() < d2.getDate()) {
                        return -1;
                    } else if (d1.getDate() > d2.getDate()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    };

}
