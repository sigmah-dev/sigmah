package org.sigmah.offline.js;

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

import com.allen_sauer.gwt.log.client.Log;
import java.util.Date;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.CalendarWrapper;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ObjectJsMapBoxer implements AutoBoxingJsMap.Boxer<Object> {
	
	private enum Type {
		BOOLEAN,
		CALENDAR_WRAPPER,
		DATE,
		INTEGER,
		LONG,
		NULL,
		STRING,
		UNKNOWN;
	}
	
	private static final char SEPARATOR = '#';

	@Override
	public String toString(Object object) {
		final StringBuilder stringBuilder = new StringBuilder();
		
		if(object == null) {
			stringBuilder.append(Type.NULL)
				.append(SEPARATOR);
			
		} else if(object instanceof String) {
			stringBuilder.append(Type.STRING)
				.append(SEPARATOR)
				.append((String)object);
			
		} else if(object instanceof Date) {
			stringBuilder.append(Type.DATE)
				.append(SEPARATOR)
				.append(((Date)object).getTime());
			
		} else if(object instanceof Integer) {
			stringBuilder.append(Type.INTEGER)
				.append(SEPARATOR)
				.append((Integer)object);
			
		} else if(object instanceof Long) {
			stringBuilder.append(Type.LONG)
				.append(SEPARATOR)
				.append((Long)object);
			
		} else if(object instanceof Boolean) {
			stringBuilder.append(Type.BOOLEAN)
				.append(SEPARATOR)
				.append((Boolean) object);
			
		} else if(object instanceof CalendarWrapper) {
			final CalendarWrapper calendarWrapper = (CalendarWrapper)object;
			final PersonalCalendarIdentifier personalCalendarIdentifier = (PersonalCalendarIdentifier)calendarWrapper.getCalendar().getIdentifier();
			
			stringBuilder.append(Type.CALENDAR_WRAPPER)
				.append(SEPARATOR)
				.append(personalCalendarIdentifier.getId());
			
		} else {
			stringBuilder.append(Type.UNKNOWN)
				.append(SEPARATOR);
			Log.warn("Unsupported type '" + object.getClass() + "'.");
		}
		
		return stringBuilder.toString();
	}

	@Override
	public Object fromString(String string) {
		final int separator = string.indexOf(SEPARATOR);
		final Type type = Type.valueOf(string.substring(0, separator));
		
		switch(type) {
			case BOOLEAN:
				return Boolean.parseBoolean(string.substring(separator + 1));
			case CALENDAR_WRAPPER:
				final PersonalCalendarIdentifier personalCalendarIdentifier = new PersonalCalendarIdentifier(Integer.parseInt(string.substring(separator + 1)));
				final Calendar calendar = new Calendar();
				calendar.setIdentifier(personalCalendarIdentifier);
				return new CalendarWrapper(calendar);
			case DATE:
				return new Date(Long.parseLong(string.substring(separator + 1)));
			case INTEGER:
				return Integer.parseInt(string.substring(separator + 1));
			case LONG:
				return Long.parseLong(string.substring(separator + 1));
			case STRING:
				return string.substring(separator + 1);
			default:
				return null;
		}
	}
	
}
