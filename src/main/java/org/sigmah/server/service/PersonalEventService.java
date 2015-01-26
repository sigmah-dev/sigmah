package org.sigmah.server.service;

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
 * @author Raphaël Calabro (rcalabro@ideia.fr)
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

		final Date day = (Date) properties.get(Event.DATE);
		final Serializable startHourSerialized = properties.get(Event.START_TIME);
		final Serializable endHourSerialized = properties.get(Event.END_TIME);

		if (startHourSerialized instanceof Long) {
			final Date startHour = new Date((Long) startHourSerialized);
			event.setStartDate(startHour);
			
			if (endHourSerialized instanceof Long) {
				final Date endHour = new Date((Long) endHourSerialized);
				event.setEndDate(endHour);
			} else {
				event.setEndDate(null);
			}

		} else {
			event.setStartDate(new Date(day.getYear(), day.getMonth(), day.getDate()));
			event.setEndDate(new Date(day.getYear(), day.getMonth(), day.getDate() + 1));
		}
	}

}
