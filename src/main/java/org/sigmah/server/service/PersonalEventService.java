package org.sigmah.server.service;

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

import java.io.Serializable;
import java.util.Date;

import org.sigmah.server.dao.PersonalEventDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.calendar.PersonalEvent;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.calendar.CalendarWrapper;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;
import org.sigmah.shared.dto.calendar.PersonalEventDTO;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * {@link PersonalEvent} policy implementation.
 * 
 * @author RaphaГ«l Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class PersonalEventService extends AbstractEntityService<PersonalEvent, Integer, PersonalEventDTO> {

	/**
	 * Injected {@link PersonalEventDAO}.
	 */
	private final PersonalEventDAO personalEventDAO;

	@Inject
	public PersonalEventService(PersonalEventDAO personalEventDAO) {
		this.personalEventDAO = personalEventDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonalEvent create(PropertyMap properties, final UserExecutionContext context) {

		final PersonalEvent event = new PersonalEvent();

		event.setDateCreated(new Date());
		fillEvent(event, properties);

		return personalEventDAO.persist(event, context.getUser());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonalEvent update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {

		final PersonalEvent event = personalEventDAO.findById(entityId);

		fillEvent(event, changes);

		return personalEventDAO.persist(event, context.getUser());
	}

	@SuppressWarnings("deprecation")
	private static void fillEvent(PersonalEvent event, PropertyMap properties) {
		final CalendarWrapper calendar = (CalendarWrapper) properties.get(Event.CALENDAR_ID);
		event.setCalendarId(((PersonalCalendarIdentifier) calendar.getCalendar().getIdentifier()).getId());

		event.setSummary((String) properties.get(Event.SUMMARY));
		event.setDescription((String) properties.get(Event.DESCRIPTION));
                event.setEventType((String) properties.get(Event.EVENT_TYPE));
                event.setReferenceId((Integer) properties.get(Event.REFERENCE_ID));
                
		final Date day = (Date) properties.get(Event.DATE);
                final Date dayEnd = (Date) properties.get(Event.DATE_END);
		final Serializable startHourSerialized = properties.get(Event.START_TIME);
		final Serializable endHourSerialized = properties.get(Event.END_TIME);

                        if (startHourSerialized != null && startHourSerialized instanceof Long) {
			final Date startHour = new Date((Long) startHourSerialized);
			event.setStartDate(startHour);
			
			if (endHourSerialized != null && endHourSerialized instanceof Long) {
				final Date endHour = new Date((Long) endHourSerialized);
				event.setEndDate(new Date(dayEnd.getTime()+(endHour.getTime()-day.getTime())));
			} else {
				event.setEndDate(null);
			}

		} else {
			event.setStartDate(new Date(day.getYear(), day.getMonth(), day.getDate()));
			event.setEndDate(new Date(dayEnd.getYear(), dayEnd.getMonth(), dayEnd.getDate()));
		}
	}

}
