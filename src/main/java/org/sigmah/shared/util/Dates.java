/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.util;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.Date;
import org.sigmah.shared.dto.pivot.model.DateUnit;
import org.sigmah.shared.dto.pivot.content.DimensionCategory;
import org.sigmah.shared.dto.pivot.content.MonthCategory;
import org.sigmah.shared.dto.pivot.content.YearCategory;

/**
 * Abstract class providing functions for manipulating dates. There are concrete implementations
 * based on the <code>Calendar</code> class, and another for client-side usage.
 *
 * @see org.sigmah.client.util.GWTDates
 * @see org.sigmah.server.util.CalendarDates
 *
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public abstract class Dates {

    public abstract Month getCurrentMonth();

    public abstract DateRange yearRange(int year);

    public abstract DateRange monthRange(int year, int month);

    public DateRange monthRange(Month month) {
        return monthRange(month.getYear(), month.getMonth());
    }

    public DateRange rangeFromCategory(DimensionCategory category) {

        if(category instanceof YearCategory) {
            return yearRange(((YearCategory)category).getYear());
        } else if(category instanceof MonthCategory) {
            MonthCategory monthCategory = (MonthCategory)category;
            return monthRange(monthCategory.getYear(), monthCategory.getMonth());
        } else {
            return new DateRange();
        }
    }


    public abstract int getYear(Date date);

    public abstract int getMonth(Date date);

    public abstract int getDay(Date date);

    public abstract Date floor(Date date, DateUnit dateUnit);

    public abstract Date ceil(Date date, DateUnit dateUnit);

    public abstract Date add(Date date, DateUnit dateUnit, int count);

    public abstract boolean isLastDayOfMonth(Date date);


    public Date startDateOfLastCompleteMonth(Date today) {
        // set this to the beginning of the last complete month
        Date start = floor(today, DateUnit.MONTH);
        if(!isLastDayOfMonth(today)) {
            start = add(start, DateUnit.MONTH, -1);
        }
        return start;
    }

    public Date endDateOfLastCompleteMonth(Date today) {
        // set this to the beginning of the last complete month
        Date end = ceil(today, DateUnit.MONTH);
        if(! isLastDayOfMonth(today)) {
            end = add(end, DateUnit.MONTH, -1);
        }
        return end;
    }

    public DateRange lastCompleteMonthRange(Date today) {
       return new DateRange(startDateOfLastCompleteMonth(today),
                            endDateOfLastCompleteMonth(today));
    }
    
    /**
     * 
     * @param dateRange
     * @return true if the given dateRange includes a single calendar month
     */
    public boolean isMonthRange(DateRange dateRange) {
    	return dateRange.isClosed() && 
    		getMonth(dateRange.getMinDate()) == getMonth(dateRange.getMaxDate()) &&
    		getDay(dateRange.getMinDate()) == 1 &&
    		isLastDayOfMonth(dateRange.getMaxDate());
    }

    public Month monthFromDate(Date date) {
    	return new Month(getYear(date), getMonth(date));
    }

	public DateRange dateRange(Month startMonth, Month endMonth) {
		return new DateRange(monthRange(startMonth).getMinDate(), monthRange(endMonth).getMaxDate());
	}
    
}
