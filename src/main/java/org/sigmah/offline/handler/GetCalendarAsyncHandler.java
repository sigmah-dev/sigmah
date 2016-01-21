package org.sigmah.offline.handler;

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

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.CalendarType;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.result.Authentication;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetCalendarHandler}.
 * Used when the user is offline.
 * <p/>
 * This is a global handler to retrieves every type of calendars.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetCalendarAsyncHandler implements AsyncCommandHandler<GetCalendar, Calendar>, DispatchListener<GetCalendar, Calendar> {

	private final Map<CalendarType, AsyncCommandHandler<GetCalendar, Calendar>> calendarHandlers;

	@Inject
	public GetCalendarAsyncHandler(ActivityCalendarAsyncHandler activityCalendarAsyncHandler,
			PersonalCalendarAsyncHandler personalCalendarAsyncHandler,
			MonitoredPointCalendarAsyncHandler monitoredPointCalendarAsyncHandler,
			ReminderCalendarAsyncHandler reminderCalendarAsyncHandler) {
		calendarHandlers = new EnumMap<CalendarType, AsyncCommandHandler<GetCalendar, Calendar>>(CalendarType.class);
		calendarHandlers.put(CalendarType.Activity, activityCalendarAsyncHandler);
		calendarHandlers.put(CalendarType.Personal, personalCalendarAsyncHandler);
		calendarHandlers.put(CalendarType.MonitoredPoint, monitoredPointCalendarAsyncHandler);
		calendarHandlers.put(CalendarType.Reminder, reminderCalendarAsyncHandler);
	}
	
	@Override
	public void execute(GetCalendar command, OfflineExecutionContext executionContext, AsyncCallback<Calendar> callback) {
		final AsyncCommandHandler<GetCalendar, Calendar> calendarHandler = calendarHandlers.get(command.getType());
		calendarHandler.execute(command, executionContext, callback);
	}

	@Override
	public void onSuccess(GetCalendar command, Calendar result, Authentication authentication) {
		if(result != null) {
			final AsyncCommandHandler<GetCalendar, Calendar> calendarHandler = calendarHandlers.get(command.getType());

			if(calendarHandler instanceof DispatchListener) {
				final DispatchListener<GetCalendar, Calendar> dispatchListener = (DispatchListener<GetCalendar, Calendar>)calendarHandler;
				dispatchListener.onSuccess(command, result, authentication);
			}
		}
	}
}
