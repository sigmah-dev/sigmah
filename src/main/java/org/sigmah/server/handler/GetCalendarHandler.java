package org.sigmah.server.handler;

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

/**
 * Retrieves calendars and events.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
@Singleton
public class GetCalendarHandler extends AbstractCommandHandler<GetCalendar, Calendar> {

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
		return handler.getCalendar(cmd.getIdentifier(),em());
	}
}
