package org.sigmah.offline.handler;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.calendar.ActivityCalendarIdentifier;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.logframe.ExpectedResultDTO;
import org.sigmah.shared.dto.logframe.LogFrameActivityDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.logframe.SpecificObjectiveDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.calendar.ActivityCalendarHandler}.
 * Used when the user is offline.
 * <p/>
 * Creates a <code>Calendar</code> from the activities of the log frame.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ActivityCalendarAsyncHandler implements AsyncCommandHandler<GetCalendar, Calendar> {

	private final ProjectAsyncDAO projectAsyncDAO;

	@Inject
	public ActivityCalendarAsyncHandler(ProjectAsyncDAO projectAsyncDAO) {
		this.projectAsyncDAO = projectAsyncDAO;
	}

	@Override
	public void execute(GetCalendar command, OfflineExecutionContext executionContext, AsyncCallback<Calendar> callback) {
		final Calendar calendar = new Calendar();
		final RequestManager<Calendar> requestManager = new RequestManager<Calendar>(calendar, callback);

		final ActivityCalendarIdentifier identifier = (ActivityCalendarIdentifier) command.getIdentifier();
		calendar.setName(identifier.getCalendarName());
		calendar.setIdentifier(identifier);

		projectAsyncDAO.get(identifier.getProjectId(), new RequestManagerCallback<Calendar, ProjectDTO>(requestManager) {

			@Override
			public void onRequestSuccess(ProjectDTO result) {
				final LogFrameDTO logFrame = result.getLogFrame();

				if (logFrame != null) {
					final HashMap<Date, List<Event>> eventMap = new HashMap<Date, List<Event>>();
					calendar.setEvents(eventMap);

					final StringBuilder codeBuilder = new StringBuilder(identifier.getActivityPrefix());
					codeBuilder.append(' ');
					final int baseSize = codeBuilder.length();

					for (final SpecificObjectiveDTO specificObjective : logFrame.getSpecificObjectives()) {
						codeBuilder.append((char) ('A' + specificObjective.getCode() - 1));
						codeBuilder.append('.');
						final int specificObjectiveSize = codeBuilder.length();

						for (final ExpectedResultDTO expectedResult : specificObjective.getExpectedResults()) {
							codeBuilder.append(expectedResult.getCode());
							codeBuilder.append('.');
							final int expectedResultSize = codeBuilder.length();

							// For each activity
							for (final LogFrameActivityDTO activity : expectedResult.getActivities()) {
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
				}
			}
		});

		requestManager.ready();
	}
}
