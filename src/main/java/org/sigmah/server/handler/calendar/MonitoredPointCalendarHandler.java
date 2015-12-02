package org.sigmah.server.handler.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.sigmah.server.domain.reminder.MonitoredPoint;
import org.sigmah.server.domain.reminder.MonitoredPointList;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.CalendarIdentifier;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.MonitoredPointCalendarIdentifier;

import com.google.inject.Inject;

/**
 * Retrieve monitored points as events.
 * 
 * @author tmi
 */
public class MonitoredPointCalendarHandler implements CalendarHandler {


	@Inject
	public MonitoredPointCalendarHandler() {

	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Calendar getCalendar(CalendarIdentifier identifier,EntityManager em) {

		if (!(identifier instanceof MonitoredPointCalendarIdentifier)) {
			throw new IllegalArgumentException("Identifier must be an instance of MonitoredPointCalendarIdentifier, received an instance of "
					+ identifier.getClass().getSimpleName());
		}

		final MonitoredPointCalendarIdentifier calendarIdentifier = (MonitoredPointCalendarIdentifier) identifier;

		final SimpleDateFormat format = new SimpleDateFormat(calendarIdentifier.getDateFormat());

		final Query query = em.createQuery("SELECT l FROM MonitoredPointList l WHERE l.id = :listId");
		query.setParameter("listId", calendarIdentifier.getMonitoredListId());

		// Configuring the calendar
		final Calendar calendar = new Calendar();
		calendar.setIdentifier(identifier);
		calendar.setName(calendarIdentifier.getCalendarName());
		calendar.setEditable(false);

		final HashMap<Date, List<Event>> eventMap = new HashMap<Date, List<Event>>();
		calendar.setEvents(eventMap);

		try {

			final MonitoredPointList list = (MonitoredPointList) query.getSingleResult();

			if (list.getPoints() != null) {
				for (final MonitoredPoint point : list.getPoints()) {
                    if(point.getDeleted() == null || !point.getDeleted()) {
					final Event event = new Event();
					event.setParent(calendar);
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

					// Adding the event to the event map
					final Date key = event.getKey();
					List<Event> events = eventMap.get(key);
					if (events == null) {
						events = new ArrayList<Event>();
						eventMap.put(key, events);
					}
					events.add(event);
				}
			}
			}

		} catch (NoResultException e) {
			// No monitored points in the current project.
		}

		return calendar;
	}
}
