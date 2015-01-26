package org.sigmah.offline.handler;

import com.google.gwt.i18n.client.DateTimeFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.MonitoredPointCalendarIdentifier;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import org.sigmah.offline.dao.MonitoredPointAsyncDAO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.calendar.MonitoredPointCalendarHandler}.
 * Used when the user is offline.
 * <p/>
 * Creates a <code>Calendar</code> from the monitored point of the given project.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class MonitoredPointCalendarAsyncHandler implements AsyncCommandHandler<GetCalendar, Calendar> {

	@Inject
	private MonitoredPointAsyncDAO monitoredPointAsyncDAO;
	
	@Override
	public void execute(GetCalendar command, OfflineExecutionContext executionContext, final AsyncCallback<Calendar> callback) {
		final MonitoredPointCalendarIdentifier identifier = (MonitoredPointCalendarIdentifier)command.getIdentifier();
		
		monitoredPointAsyncDAO.getAllByParentListId(identifier.getMonitoredListId(), new AsyncCallback<List<MonitoredPointDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(List<MonitoredPointDTO> monitoredPoints) {
				final Calendar calendar = new Calendar();
				calendar.setName(identifier.getCalendarName());
				calendar.setIdentifier(identifier);
				
				final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(identifier.getDateFormat());
				
				final HashMap<Date, List<Event>> events = new HashMap<Date, List<Event>>();
				for(final MonitoredPointDTO monitoredPoint : monitoredPoints) {
					final Event event = createEvent(monitoredPoint, identifier, dateTimeFormat);
					event.setParent(calendar);

					final Date key = event.getKey();

					List<Event> eventList = events.get(key);
					if(eventList == null) {
						eventList = new ArrayList<Event>();
						events.put(key, eventList);
					}
					eventList.add(event);
				}
				calendar.setEvents(events);
				
				callback.onSuccess(calendar);
			}
		});
		
	}
	
	private Event createEvent(MonitoredPointDTO point, MonitoredPointCalendarIdentifier calendarIdentifier, DateTimeFormat format) {
		final Event event = new Event();
		event.setIdentifier(point.getId());

		// A completed point is displayed at its completion date,
		// while a running point is displayed at its expected date.
		final Date date = point.isCompleted() ? point.getCompletionDate() : point.getExpectedDate();

		event.setDtstart(date);
		event.setDtend(new Date(date.getYear(), date.getMonth(), date.getDate() + 1));

		// Summary.
		StringBuilder sb = new StringBuilder();
		sb.append(point.getLabel());
		if (point.isCompleted()) {
			sb.append(" (");
			sb.append(calendarIdentifier.getCompletedEventString());
			sb.append(')');
		}
		event.setSummary(sb.toString());

		// Description.
		sb = new StringBuilder();
		sb.append(point.getLabel());
		if (point.isCompleted()) {
			sb.append(" (");
			sb.append(calendarIdentifier.getExpectedDateString());
			sb.append(": ");
			sb.append(format.format(point.getExpectedDate()));
			sb.append(')');
		}

		event.setDescription(sb.toString());
		
		return event;
	}
	
}
