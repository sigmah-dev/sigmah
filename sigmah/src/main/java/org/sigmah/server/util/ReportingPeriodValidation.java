package org.sigmah.server.util;

import java.util.Calendar;

import org.sigmah.shared.domain.ReportingPeriod;

/**
 * Validate a {@link ReportingPeriod}
 * 
 * @author AurÃ©lien PonÃ§on
 */
public class ReportingPeriodValidation {

    /**
     * This function is used to validate a {@link ReportingPeriod}. It verify if the period of the element is not too
     * near of the limit of the month (in order to avoid timezone issues). If true, it modify the element.
     * 
     * @param period
     *            the period to validate
     * @return a boolean that indicates if the period was valid
     */
    public static boolean validate(ReportingPeriod period) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(period.getDate1());

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(period.getDate2());

        if (cal1.get(Calendar.DATE) == 1 && cal2.get(Calendar.DATE) == cal2.getActualMaximum(Calendar.DATE)) {
            // Update old periods
            cal1.set(Calendar.DATE, 5);
            period.setDate1(cal1.getTime());

            cal2.set(Calendar.DATE, cal2.getActualMaximum(Calendar.DATE) - 5);
            period.setDate2(cal2.getTime());

            return false;
        } else if (cal1.get(Calendar.DATE) > 15) {
            // Update periods with a left shift on the month of the first date
            cal1.add(Calendar.MONTH, 1);
            cal1.set(Calendar.DATE, 5);
            period.setDate1(cal1.getTime());

            cal2.set(Calendar.DATE, cal2.getActualMaximum(Calendar.DATE) - 5);
            period.setDate2(cal2.getTime());

            return false;
        } else if (cal2.get(Calendar.DATE) < 15) {
            // Update periods with a right shift on the month of the second date
            cal2.add(Calendar.MONTH, -1);
            cal2.set(Calendar.DATE, cal2.getActualMaximum(Calendar.DATE) - 5);
            period.setDate2(cal2.getTime());

            cal1.set(Calendar.DATE, 5);
            period.setDate1(cal1.getTime());

            return false;
        } else {
            return true;
        }
    }
}
