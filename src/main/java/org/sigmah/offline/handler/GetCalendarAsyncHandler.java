package org.sigmah.offline.handler;

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
		final AsyncCommandHandler<GetCalendar, Calendar> calendarHandler = calendarHandlers.get(command.getType());
		
		if(calendarHandler instanceof DispatchListener) {
			final DispatchListener<GetCalendar, Calendar> dispatchListener = (DispatchListener<GetCalendar, Calendar>)calendarHandler;
			dispatchListener.onSuccess(command, result, authentication);
		}
	}
}
