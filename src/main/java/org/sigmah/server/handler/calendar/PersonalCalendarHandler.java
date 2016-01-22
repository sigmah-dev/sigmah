package org.sigmah.server.handler.calendar;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.sigmah.server.domain.calendar.PersonalCalendar;
import org.sigmah.server.domain.calendar.PersonalEvent;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.CalendarIdentifier;
import org.sigmah.shared.dto.calendar.CalendarType;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;

import com.google.inject.Inject;

/**
 * Retrieve personal events.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PersonalCalendarHandler implements CalendarHandler {

	

	@Inject
	public PersonalCalendarHandler() {
	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Calendar getCalendar(CalendarIdentifier identifier, EntityManager em) {
		if (!(identifier instanceof PersonalCalendarIdentifier))
			throw new IllegalArgumentException();

		// Fetching the calendar
		final PersonalCalendarIdentifier id = (PersonalCalendarIdentifier) identifier;
		final TypedQuery<PersonalCalendar> calendarQuery = em.createQuery("SELECT c FROM PersonalCalendar c WHERE c.id = :calendarId", PersonalCalendar.class);
		calendarQuery.setParameter("calendarId", id.getId());

		final PersonalCalendar personalCalendar = calendarQuery.getSingleResult();

		// Fetching the events
		final TypedQuery<PersonalEvent> eventQuery =
				em.createQuery("SELECT p FROM PersonalEvent p WHERE p.calendarId = :calendarId ORDER BY p.startDate", PersonalEvent.class);
		eventQuery.setParameter("calendarId", id.getId());

		final List<PersonalEvent> events = eventQuery.getResultList();

		final Calendar calendar = new Calendar();
		calendar.setType(CalendarType.Personal);
		calendar.setName(personalCalendar.getName());
		calendar.setIdentifier(identifier);
		calendar.setEditable(true);

		if (events != null) {
			final HashMap<Date, List<Event>> eventMap = new HashMap<Date, List<Event>>();

			for (final PersonalEvent event : events) {
				final Date key = normalize(event.getStartDate());

				List<Event> list = eventMap.get(key);
				if (list == null) {
					list = new ArrayList<Event>();
					eventMap.put(key, list);
				}

				final Event calendarEvent = new Event();
				calendarEvent.setIdentifier(event.getId());
				calendarEvent.setParent(calendar);
				calendarEvent.setSummary(event.getSummary());
				calendarEvent.setDescription(event.getDescription());
				calendarEvent.setDtstart(new Date(event.getStartDate().getTime()));
				if (event.getEndDate() != null)
					calendarEvent.setDtend(new Date(event.getEndDate().getTime()));

				list.add(calendarEvent);
			}

			calendar.setEvents(eventMap);
		}

		return calendar;
	}

	@SuppressWarnings("deprecation")
	private static Date normalize(Date date) {
		return new Date(date.getYear(), date.getMonth(), date.getDate());
	}

}
