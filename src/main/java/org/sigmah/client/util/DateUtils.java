package org.sigmah.client.util;

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


import com.extjs.gxt.ui.client.widget.form.Time;
import java.util.Comparator;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * Utility class to manipulates dates.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class DateUtils {

	/**
	 * Short date format.
	 * Available only client side. Avoid using this if possible.
	 * 
	 * @see PredefinedFormat#DATE_SHORT
	 */
	public static final DateTimeFormat DATE_SHORT = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
	
	/**
	 * Short date format.
	 * 
	 * @see PredefinedFormat#DATE_SHORT
	 */
	public static final com.google.gwt.i18n.shared.DateTimeFormat SHARED_DATE_SHORT = com.google.gwt.i18n.shared.DateTimeFormat.getFormat(com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.DATE_SHORT);

	/**
	 * Short date/time format.
	 * 
	 * @see PredefinedFormat#DATE_TIME_SHORT
	 */
	public static final DateTimeFormat DATE_TIME_SHORT = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);

	/**
	 * Compare two dates. The comparison ignores hours, minutes and seconds.
	 */
	@SuppressWarnings("deprecation")
	public static final Comparator<Date> DAY_COMPARATOR = new Comparator<Date>() {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final Date d1, final Date d2) {

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
	
	/**
	 * Returns a date whose day, month and year are taken from the given 
	 * <code>date</code> and whose hour and minutes are taken from the given
	 * <code>time</code>.
	 * 
	 * @param date Day of month, month and year to use.
	 * @param time Hour and minutes to use.
	 * @return A new date made by combining the data of the given objects.
	 */
	@SuppressWarnings("deprecation")
	public static final Date mix(Date date, Time time) {
		if(date != null && time != null) {
			final Date result = new Date(date.getTime());
			result.setHours(time.getHour());
			result.setMinutes(time.getMinutes());
			return result;
			
		} else {
			return null;
		}
	}

	/**
	 * Private constructor.
	 */
	private DateUtils() {
		// Only provides static constants.
	}

}
