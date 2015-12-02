package org.sigmah.offline.js;

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
