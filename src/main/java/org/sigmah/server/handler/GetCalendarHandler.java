package org.sigmah.server.handler;

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

import java.util.EnumMap;
import java.util.Map;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.calendar.ActivityCalendarHandler;
import org.sigmah.server.handler.calendar.CalendarHandler;
import org.sigmah.server.handler.calendar.MonitoredPointCalendarHandler;
import org.sigmah.server.handler.calendar.PersonalCalendarHandler;
import org.sigmah.server.handler.calendar.ReminderCalendarHandler;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.calendar.CalendarType;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves calendars and events.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
@Singleton
public class GetCalendarHandler extends AbstractCommandHandler<GetCalendar, Calendar> {

	
	private static final Logger LOGGER=LoggerFactory.getLogger(GetCalendarHandler.class);
	/**
	 * List of converters. They convert model objects to <code>Calendar</code>s and <code>Event</code>s objects.
	 * 
	 * @see CalendarHandler
	 */
	private final Map<CalendarType, CalendarHandler> handlers;

	@Inject
	public GetCalendarHandler(Injector injector) {
		final EnumMap<CalendarType, CalendarHandler> map = new EnumMap<CalendarType, CalendarHandler>(CalendarType.class);
		map.put(CalendarType.Activity, injector.getInstance(ActivityCalendarHandler.class));
		map.put(CalendarType.Personal, injector.getInstance(PersonalCalendarHandler.class));
		map.put(CalendarType.MonitoredPoint, injector.getInstance(MonitoredPointCalendarHandler.class));
		map.put(CalendarType.Reminder, injector.getInstance(ReminderCalendarHandler.class));

		handlers = map;
	}

	public Map<CalendarType, CalendarHandler> getHandlers() {
		return handlers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Calendar execute(GetCalendar cmd, final UserExecutionContext context) throws CommandException {
		final CalendarHandler handler = handlers.get(cmd.getType());
		Calendar calendar= handler.getCalendar(cmd.getIdentifier(),em());
		return calendar;
	}
}
