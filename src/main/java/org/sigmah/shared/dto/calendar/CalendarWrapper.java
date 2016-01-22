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

import org.sigmah.shared.command.result.Calendar;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * Wrapper class that allow the use of {@link Calendar}s objects with Ext-GWT.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class CalendarWrapper extends BaseModel {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1017103235263407544L;

	public static final String ID = "id";
	public static final String NAME = "name";

	private Calendar calendar;

	/**
	 * Empty constructor, needed by the serialization process.
	 */
	public CalendarWrapper() {
		// Serialization.
	}

	/**
	 * Wrap the given Calendar as a BaseModel object.
	 * 
	 * @param calendar
	 *          the calendar to wrap.
	 */
	public CalendarWrapper(Calendar calendar) {
		this.set(NAME, calendar.getName());
		this.set(ID, calendar.getIdentifier());
		this.calendar = calendar;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CalendarWrapper other = (CalendarWrapper) obj;
		if (this.calendar != other.calendar && (this.calendar == null || !this.calendar.equals(other.calendar))) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + (this.calendar != null ? this.calendar.hashCode() : 0);
		return hash;
	}
}
