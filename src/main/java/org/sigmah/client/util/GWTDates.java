package org.sigmah.client.util;

import com.extjs.gxt.ui.client.util.DateWrapper;

import java.util.Date;
import org.sigmah.shared.dto.pivot.model.DateUnit;
import org.sigmah.shared.util.DateRange;
import org.sigmah.shared.util.Dates;
import org.sigmah.shared.util.Month;

/**
 * Client-side implementation of {@link Dates}.
 *
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class GWTDates extends Dates {

	@Override
    public Month getCurrentMonth() {
        DateWrapper today = new DateWrapper();
        return new Month(today.getFullYear(), today.getMonth());
    }

    @Override
    public DateRange yearRange(int year) {
        DateRange range = new DateRange();

        DateWrapper date = new DateWrapper(year, 0, 1);
        range.setMinDate(date.asDate());

        date = new DateWrapper(year, 11, 31);
        range.setMaxDate(date.asDate());

        return range;
    }

    @Override
    public DateRange monthRange(int year, int month) {

        DateRange range = new DateRange();

        DateWrapper date = new DateWrapper(year, month-1, 1);
        range.setMinDate(date.asDate());

        date = date.addMonths(1);
        date = date.addDays(-1);
        range.setMaxDate(date.asDate());

        return range;
    }

    @Override
    public int getYear(Date date) {
        DateWrapper dw = new DateWrapper(date);
        return dw.getFullYear();
    }

    @Override
    public int getMonth(Date date) {
        DateWrapper dw = new DateWrapper(date);
        return dw.getMonth()+1;
    }

    @Override
    public int getDay(Date date) {
        DateWrapper dw = new DateWrapper(date);
        return dw.getDay();
    }

    @Override
    public Date floor(Date date, DateUnit dateUnit) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Date ceil(Date date, DateUnit dateUnit) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Date add(Date date, DateUnit dateUnit, int count) {
         throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isLastDayOfMonth(Date date) {
        return false;
    }
}
