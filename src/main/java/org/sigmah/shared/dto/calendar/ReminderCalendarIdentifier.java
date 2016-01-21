package org.sigmah.shared.dto.calendar;

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

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReminderCalendarIdentifier implements CalendarIdentifier {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3131879006325032077L;

	private int reminderListId;
	private String calendarName;
	private String completedEventString;
	private String expectedDateString;
	private String dateFormat;

	public ReminderCalendarIdentifier() {
		// Serialization.
	}

	public ReminderCalendarIdentifier(int reminderListId, String calendarName, String completedEventString, String expectedDateString, String dateFormat) {
		this.reminderListId = reminderListId;
		this.calendarName = calendarName;
		this.completedEventString = completedEventString;
		this.expectedDateString = expectedDateString;
		this.dateFormat = dateFormat;
	}

	public int getReminderListId() {
		return reminderListId;
	}

	public void setReminderListId(int reminderListId) {
		this.reminderListId = reminderListId;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public String getCompletedEventString() {
		return completedEventString;
	}

	public void setCompletedEventString(String completedEventString) {
		this.completedEventString = completedEventString;
	}

	public String getExpectedDateString() {
		return expectedDateString;
	}

	public void setExpectedDateString(String expectedDateString) {
		this.expectedDateString = expectedDateString;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calendarName == null) ? 0 : calendarName.hashCode());
		result = prime * result + ((dateFormat == null) ? 0 : dateFormat.hashCode());
		result = prime * result + ((expectedDateString == null) ? 0 : expectedDateString.hashCode());
		result = prime * result + reminderListId;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReminderCalendarIdentifier other = (ReminderCalendarIdentifier) obj;
		if (calendarName == null) {
			if (other.calendarName != null)
				return false;
		} else if (!calendarName.equals(other.calendarName))
			return false;
		if (completedEventString == null) {
			if (other.completedEventString != null)
				return false;
		} else if (!completedEventString.equals(other.completedEventString))
			return false;
		if (dateFormat == null) {
			if (other.dateFormat != null)
				return false;
		} else if (!dateFormat.equals(other.dateFormat))
			return false;
		if (expectedDateString == null) {
			if (other.expectedDateString != null)
				return false;
		} else if (!expectedDateString.equals(other.expectedDateString))
			return false;
		if (reminderListId != other.reminderListId)
			return false;
		return true;
	}

}
