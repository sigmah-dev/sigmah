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

import com.google.gwt.i18n.client.DateTimeFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.ReminderCalendarIdentifier;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import org.sigmah.offline.dao.ReminderAsyncDAO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.calendar.ReminderCalendarHandler}.
 * Used when the user is offline.
 * <p/>
 * Creates a <code>Calendar</code> from the monitored point of the given project.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ReminderCalendarAsyncHandler implements AsyncCommandHandler<GetCalendar, Calendar> {

	@Inject
	private ReminderAsyncDAO reminderAsyncDAO;
	
	@Override
	public void execute(GetCalendar command, OfflineExecutionContext executionContext, final AsyncCallback<Calendar> callback) {
		final ReminderCalendarIdentifier identifier = (ReminderCalendarIdentifier)command.getIdentifier();
		
		reminderAsyncDAO.getAllByParentListId(identifier.getReminderListId(), new AsyncCallback<List<ReminderDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(List<ReminderDTO> reminders) {
				final Calendar calendar = new Calendar();
				calendar.setName(identifier.getCalendarName());
				calendar.setIdentifier(identifier);
				
				final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(identifier.getDateFormat());
				
				final HashMap<Date, List<Event>> events = new HashMap<Date, List<Event>>();
				for(final ReminderDTO reminder : reminders) {
					final Event event = createEvent(reminder, identifier, dateTimeFormat);
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
	
	private Event createEvent(ReminderDTO reminder, ReminderCalendarIdentifier calendarIdentifier, DateTimeFormat format) {
		final Event event = new Event();
		event.setIdentifier(reminder.getId());

		// A completed point is displayed at its completion date,
		// while a running point is displayed at its expected date.
		final Date date = reminder.isCompleted() ? reminder.getCompletionDate() : reminder.getExpectedDate();

		event.setDtstart(date);
		event.setDtend(new Date(date.getYear(), date.getMonth(), date.getDate() + 1));

		// Summary.
		StringBuilder sb = new StringBuilder();
		sb.append(reminder.getLabel());
		if (reminder.isCompleted()) {
			sb.append(" (");
			sb.append(calendarIdentifier.getCompletedEventString());
			sb.append(')');
		}
		event.setSummary(sb.toString());

		// Description.
		sb = new StringBuilder();
		sb.append(reminder.getLabel());
		if (reminder.isCompleted()) {
			sb.append(" (");
			sb.append(calendarIdentifier.getExpectedDateString());
			sb.append(": ");
			sb.append(format.format(reminder.getExpectedDate()));
			sb.append(')');
		}

		event.setDescription(sb.toString());
		
		return event;
	}
}
