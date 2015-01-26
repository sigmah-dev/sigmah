/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.policy;

import java.io.Serializable;
import java.util.Date;

import org.sigmah.client.page.project.calendar.ProjectCalendarPresenter.CalendarWrapper;
import org.sigmah.server.dao.PersonalEventDAO;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.calendar.PersonalEvent;

import com.google.inject.Inject;

/**
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PersonalEventPolicy implements EntityPolicy<PersonalEvent> {
	final PersonalEventDAO dao;

	@Inject
	public PersonalEventPolicy(PersonalEventDAO dao) {
		this.dao = dao;
	}

	@Override
	public Object create(User user, PropertyMap properties) {
		final PersonalEvent event = new PersonalEvent();

		event.setDateCreated(new Date());
		fillEvent(event, properties);

		dao.persist(event);

		return event.getId();
	}

	@Override
	public void update(User user, Object entityId, PropertyMap changes) {
		final PersonalEvent event = dao.findById((Integer) entityId);
		fillEvent(event, changes);

		dao.merge(event);
	}

	@SuppressWarnings("deprecation")
	private void fillEvent(PersonalEvent event, PropertyMap properties) {
		final CalendarWrapper calendar = (CalendarWrapper) properties.get("calendarId");
		event.setCalendarId((Integer) calendar.getCalendar().getIdentifier());

		event.setSummary((String) properties.get("summary"));
		event.setDescription((String) properties.get("description"));

		final Date day = (Date) properties.get("date");
		final Serializable startHourSerialized = properties.get("startDate");
		final Serializable endHourSerialized = properties.get("endDate");

		if (startHourSerialized != null) {
			final Date startHour = new Date((Long) startHourSerialized);
			event.setStartDate(startHour);
			if (endHourSerialized != null) {
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
