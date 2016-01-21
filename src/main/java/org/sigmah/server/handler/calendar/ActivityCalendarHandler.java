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
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.sigmah.server.domain.logframe.ExpectedResult;
import org.sigmah.server.domain.logframe.LogFrame;
import org.sigmah.server.domain.logframe.LogFrameActivity;
import org.sigmah.server.domain.logframe.SpecificObjective;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.ActivityCalendarIdentifier;
import org.sigmah.shared.dto.calendar.CalendarIdentifier;
import org.sigmah.shared.dto.calendar.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Convert activities as calendar events.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ActivityCalendarHandler implements CalendarHandler {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private final static Logger LOG = LoggerFactory.getLogger(ActivityCalendarHandler.class);

	

	@Inject
	public ActivityCalendarHandler() {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Calendar getCalendar(CalendarIdentifier identifier, EntityManager em) {
		if (!(identifier instanceof ActivityCalendarIdentifier)) {
			throw new IllegalArgumentException("Identifier must be an instance of ActivityCalendarIdentifier, received an instance of "
				+ identifier.getClass().getSimpleName());
		}

		final ActivityCalendarIdentifier activityCalendarIdentifier = (ActivityCalendarIdentifier) identifier;

		em.clear();
		final Query query = em.createQuery("SELECT l FROM LogFrame l WHERE l.parentProject.id = :projectId");
		query.setParameter("projectId", activityCalendarIdentifier.getProjectId());

		// Configuring the calendar
		final Calendar calendar = new Calendar();
		calendar.setIdentifier(identifier);
		calendar.setName(activityCalendarIdentifier.getCalendarName());
		calendar.setEditable(false);

		final HashMap<Date, List<Event>> eventMap = new HashMap<Date, List<Event>>();
		calendar.setEvents(eventMap);

		try {
			final LogFrame logFrame = (LogFrame) query.getSingleResult();

			// Preparing the activity code
			final StringBuilder codeBuilder = new StringBuilder(activityCalendarIdentifier.getActivityPrefix());
			codeBuilder.append(' ');

			// Looping on the logical framework objects
			final List<SpecificObjective> specificObjectives = logFrame.getSpecificObjectives();
			for (final SpecificObjective specificObjective : specificObjectives) {
				int baseSize = codeBuilder.length();
				codeBuilder.append((char) ('A' + specificObjective.getCode() - 1));
				codeBuilder.append('.');

				final List<ExpectedResult> expectedResults = specificObjective.getExpectedResults();
				for (final ExpectedResult expectedResult : expectedResults) {
					int specificObjectiveSize = codeBuilder.length();
					codeBuilder.append(expectedResult.getCode());
					codeBuilder.append('.');

					// For each activity
					final List<LogFrameActivity> activities = expectedResult.getActivities();
					for (final LogFrameActivity activity : activities) {
						int expectedResultSize = codeBuilder.length();
						codeBuilder.append(activity.getCode());
						codeBuilder.append('.');

						final Date startDate = activity.getStartDate();

						if (activity.getTitle() != null) {
							codeBuilder.append(' ');
							codeBuilder.append(activity.getTitle());
						}

						// For each day
						if (startDate != null) {
							// if activity end date is not spécified set its value to start date
							if (activity.getEndDate() == null) {
								activity.setEndDate(startDate);
							}

							for (Date date = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate()); date.compareTo(activity.getEndDate()) < 1; date
								.setDate(date.getDate() + 1)) {
								final Date key = new Date(date.getTime());

								final Event event = new Event();
								event.setSummary(codeBuilder.toString());
								event.setDtstart(new Date(startDate.getTime()));

								if (startDate.equals(activity.getEndDate())) {
									event.setDtend(new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate() + 1));
								} else {
									event.setDtend(new Date(activity.getEndDate().getTime()));
								}

								event.setParent(calendar);
								event.setIdentifier(activity.getId());

								// Adding the event to the event map
								List<Event> list = eventMap.get(key);
								if (list == null) {
									list = new ArrayList<Event>();
									eventMap.put(key, list);
								}
								list.add(event);
							}
						}

						codeBuilder.setLength(expectedResultSize);
					}

					codeBuilder.setLength(specificObjectiveSize);
				}

				codeBuilder.setLength(baseSize);
			}
		} catch (NoResultException e) {
			// No activities in the current project
		}

		return calendar;
	}
}
