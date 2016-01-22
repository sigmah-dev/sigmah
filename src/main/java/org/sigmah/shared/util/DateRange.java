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

import java.io.Serializable;
import java.util.Date;

import org.sigmah.client.util.ToStringBuilder;

/**
 * <p>
 * Defines a time period as a range of dates. The end points of the range are <em>inclusive</em>, so that a date range
 * of 1-Jan-09 to 31-Jan-09 would include all events that took place at any moment in the month of January. The
 * {@code DateRange} can be also be open on either end. Here are a few concrete examples:
 * <table>
 * <thead>
 * <tr>
 * <td><strong>{@code minDate}</strong></td>
 * <td><strong>{@code maxDate}</strong></td>
 * <td><strong>Meaning</strong></td>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>{@code null}</td>
 * <td>{@code null}</td>
 * <td>All dates</td>
 * </tr>
 * <tr>
 * <td>1-Feb-09</td>
 * <td>{@code null}</td>
 * <td>All dates on or after 1-Feb-09</td>
 * </tr>
 * <tr>
 * <td>{@code null}</td>
 * <td>31-Jan-09</td>
 * <td>All dates on or before 31-Jan-09 (2009 or earlier)</td>
 * </tr>
 * </tbody>
 * </table>
 * </p>
 * <p>
 * A server-side version of this class exists for XML manipulations, see
 * {@link org.sigmah.server.report.model.DateRange}.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DateRange implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3018792678414321316L;

	/**
	 * The range min date.
	 */
	private Date minDate;

	/**
	 * The range max date.
	 */
	private Date maxDate;

	/**
	 * Initializes a fully open date range (all dates are included).
	 */
	public DateRange() {
		this(null, null);
	}

	/**
	 * Initializes a new {@code DateRange}, clone of the given {@code dateRange}.
	 * 
	 * @param dateRange
	 *          The date range.
	 */
	public DateRange(final DateRange dateRange) {
		this(dateRange != null ? dateRange.minDate : null, dateRange != null ? dateRange.maxDate : null);
	}

	/**
	 * Initializes a {@code DateRange} bounded by {@code minDate} and {@code maxDate}.
	 * 
	 * @param minDate
	 *          The minimum date to be included in this range (inclusive), or {@code null} if there is no minimum bound.
	 * @param maxDate
	 *          The maximum date to be included in this range (inclusive), or {@code null} if there is no maximum bound.
	 */
	public DateRange(final Date minDate, final Date maxDate) {
		setMinDate(minDate);
		setMaxDate(maxDate);
	}

	/**
	 * Gets the minimum date in this range (inclusive).
	 * 
	 * @return The minimum date in this range (inclusive) or {@code null} if the range has no lower bound
	 */
	public Date getMinDate() {
		return minDate;
	}

	/**
	 * Sets the minimum date in this range (inclusive).
	 * 
	 * @param minDate
	 *          The minimum date in this range (inclusive) or {@code null} if the range has now upper bound.
	 */
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	/**
	 * Gets the maximum date in this range (inclusive).
	 * 
	 * @return The maximum date in this range (inclusive) or {@code null} if the range has no upper bound.
	 */
	public Date getMaxDate() {
		return maxDate;
	}

	/**
	 * Sets the maximum date in this range (inclusive).
	 * 
	 * @param maxDate
	 *          The maximum date in this range (inclusive) or {@code null} if the range has no upper bound.
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	/**
	 * @return true if the range is closed, i.e. has both min and max dates.
	 */
	public boolean isClosed() {
		return minDate != null && maxDate != null;
	}

	/**
	 * Returns the intersection {@code DateRange} between the given two {@code DateRange}s.
	 * 
	 * @param a
	 *          The first {@code DateRange} (cannot be {@code null}).
	 * @param b
	 *          The second {@code DateRange} (cannot be {@code null}).
	 * @return The intersection {@code DateRange} between the given two {@code DateRange}s.
	 */
	public static DateRange intersection(final DateRange a, final DateRange b) {

		final DateRange range = new DateRange();

		// Min date.
		if (a.minDate == null && b.minDate != null) {
			range.minDate = b.minDate;

		} else if (a.minDate != null && b.minDate == null) {
			range.minDate = a.minDate;

		} else if (a.minDate != null && b.minDate != null) {
			if (a.minDate.after(b.minDate)) {
				range.minDate = a.minDate;

			} else {
				range.minDate = b.minDate;
			}
		}

		// Max date.
		if (a.maxDate == null && b.maxDate != null) {
			range.maxDate = b.maxDate;

		} else if (a.maxDate != null && b.maxDate == null) {
			range.maxDate = a.maxDate;

		} else if (a.maxDate != null && b.maxDate != null) {
			if (a.maxDate.before(b.maxDate)) {
				range.maxDate = a.maxDate;

			} else {
				range.maxDate = b.maxDate;
			}
		}

		return range;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((maxDate == null) ? 0 : maxDate.hashCode());
		result = prime * result + ((minDate == null) ? 0 : minDate.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DateRange other = (DateRange) obj;
		if (maxDate == null) {
			if (other.maxDate != null) {
				return false;
			}
		} else if (!maxDate.equals(other.maxDate)) {
			return false;
		}
		if (minDate == null) {
			if (other.minDate != null) {
				return false;
			}
		} else if (!minDate.equals(other.minDate)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("minDate", minDate);
		builder.append("maxDate", maxDate);
		return builder.toString();
	}

}
