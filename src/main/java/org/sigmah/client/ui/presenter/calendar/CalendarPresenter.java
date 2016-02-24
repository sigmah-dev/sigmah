package org.sigmah.client.ui.presenter.calendar;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchQueue;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.calendar.CalendarView;
import org.sigmah.client.ui.widget.CalendarWidget;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.calendar.CalendarType;
import org.sigmah.shared.dto.calendar.CalendarWrapper;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.PersonalEventDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.selection.AbstractStoreSelectionModel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import org.sigmah.client.ui.presenter.reminder.ReminderType;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;
import org.sigmah.shared.dto.calendar.CalendarIdentifier;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;

/**
 * Calendar widget presenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CalendarPresenter extends AbstractPresenter<CalendarPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(CalendarView.class)
	public static interface View extends ViewInterface {

		void initializeCalendarWidget(final CalendarWidget calendarWidget);

		void setAddEventButtonEnabled(final boolean addEventButtonEnabled);

		AbstractStoreSelectionModel<CalendarWrapper> getCalendarsSelectionModel();

		ListStore<CalendarWrapper> getCalendarsStore();

		Button getAddEventButton();

		Button getTodayButton();

		Button getWeekButton();

		Button getMonthButton();

		Button getPreviousButton();

		Button getNextButton();

        Button getReminderAddButton();

		Button getMonitoredPointsAddButton();

	}

	/**
	 * The calendar widget.
	 */
	private CalendarWidget calendar;

    private Integer projectId;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected CalendarPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Initialize calendar widget.
		// --

		calendar = new CalendarWidget(CalendarWidget.COLUMN_HEADERS, true, auth());
		calendar.today(); // Reset the current date.
		calendar.setDisplayMode(CalendarWidget.DisplayMode.MONTH);

		view.initializeCalendarWidget(calendar);

		view.setAddEventButtonEnabled(false);

		// --
		// Configuring calendar delegate.
		// --

		calendar.setDelegate(new CalendarWidget.Delegate() {

			@Override
			public void edit(final Event event, final CalendarWidget calendarWidget) {
				eventBus.navigateRequest(Page.CALENDAR_EVENT.request().addData(RequestParameter.DTO, event).addData(RequestParameter.CONTENT, getCalendars()));
			}

			@Override
			public void delete(final Event event, final CalendarWidget calendarWidget) {
				final CalendarIdentifier calendarIdentifier = event.getParent().getIdentifier();
				final Integer parentId = calendarIdentifier instanceof PersonalCalendarIdentifier ? 
					((PersonalCalendarIdentifier)calendarIdentifier).getId() : null;

				dispatch.execute(new Delete(PersonalEventDTO.ENTITY_NAME, event.getIdentifier(), parentId), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.calendarDeleteEventError());
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {

						final List<Event> oldEventList =
								event.getParent().getEvents().get(event.getKey());
						oldEventList.remove(event);
						calendar.refresh();
					}
				});
			}
		});

		// --
		// Calendars selection change event.
		// --

		view.getCalendarsSelectionModel().addSelectionChangedListener(new SelectionChangedListener<CalendarWrapper>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<CalendarWrapper> se) {
				final List<CalendarWrapper> wrappers = se.getSelection();
				final ArrayList<Calendar> calendars = new ArrayList<Calendar>();
				for (final CalendarWrapper wrapper : wrappers) {
					calendars.add(wrapper.getCalendar());
				}
				calendar.setCalendars(calendars);
			}
		});

		// --
		// Add event button.
		// --

		view.getAddEventButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {

				if (view.getCalendarsStore().getCount() == 0) {
					N10N.warn(I18N.CONSTANTS.calendar_addEvent_noCalendar_ko());
					return;
				}

				eventBus.navigateRequest(Page.CALENDAR_EVENT.request().addData(RequestParameter.CONTENT, getCalendars()));
			}
		});

		// --
		// Today button.
		// --

		view.getTodayButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				calendar.today();
			}
		});

		// --
		// Week button.
		// --

		view.getWeekButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				calendar.setDisplayMode(CalendarWidget.DisplayMode.WEEK);
			}
		});

		// --
		// Month button.
		// --

		view.getMonthButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				calendar.setDisplayMode(CalendarWidget.DisplayMode.MONTH);
			}
		});

		// --
		// Previous button.
		// --

		view.getPreviousButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				calendar.previous();
			}
		});

		// --
		// Next button.
		// --

		view.getNextButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				calendar.next();
			}
		});

		// --
		// Reminders / Monitored Points add buttons handlers.
		// --

		view.getReminderAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				eventBus.navigateRequest(Page.REMINDER_EDIT.requestWith(RequestParameter.TYPE, ReminderType.REMINDER).addParameter(RequestParameter.ID, 
                    projectId));
			}
		});

		view.getMonitoredPointsAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				eventBus.navigateRequest(Page.REMINDER_EDIT.requestWith(RequestParameter.TYPE, ReminderType.MONITORED_POINT).addParameter(RequestParameter.ID, 
                   projectId));
			}
		});

		// --
		// Update event handler.
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {
				if (event.concern(UpdateEvent.CALENDAR_EVENT_UPDATE)) {
					calendar.refresh();
				}
                
                if (event.concern(UpdateEvent.REMINDER_UPDATED)) {
                    // TODO appel 
                    reloadEventsOfReminderType((ReminderType) event.getParam(0));
                    calendar.refresh();
                }
			}
		}));
	}

	/**
	 * Reloads the calendars data (if necessary).
	 * 
	 * @param calendars
	 *          The calendar types with their corresponding identifier.
	 */
	public void reload(final Map<CalendarType, Integer> calendars) {
		Profiler.INSTANCE.markCheckpoint(Scenario.AGENDA, "Before refresh.");
		calendar.refresh();
		Profiler.INSTANCE.markCheckpoint(Scenario.AGENDA, "calendar.refresh ended.");
        this.projectId = calendars.get(CalendarType.Activity);

		view.setAddEventButtonEnabled(ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_PROJECT_AGENDA, GlobalPermissionEnum.EDIT_PROJECT));
		Profiler.INSTANCE.markCheckpoint(Scenario.AGENDA, "Before refresh.");
		reloadEvents(calendars);
		
	}

	/**
	 * {@inheritDoc}<br>
	 * Updates the calendar view.
	 */
	@Override
	public void onViewRevealed() {
		calendar.refresh();
	}

	/**
	 * Returns the {@link CalendarWrapper} list from the view store.
	 * 
	 * @return The collection.
	 */
	private List<CalendarWrapper> getCalendars() {

		final List<CalendarWrapper> calendars = new ArrayList<CalendarWrapper>();
		final ListStore<CalendarWrapper> store = view.getCalendarsStore();

		for (int i = 0; i < store.getCount(); i++) {
			calendars.add(store.getAt(i));
		}

		return calendars;
	}

	/**
	 * Reloads the calendar events using a {@link GetCalendar} command.
	 * 
	 * @param calendars
	 *          The calendar types with their corresponding identifier.
	 */
	private void reloadEvents(final Map<CalendarType, Integer> calendars) {

		view.getCalendarsStore().removeAll();

		if (ClientUtils.isEmpty(calendars)) {
			calendar.refresh();
			Profiler.INSTANCE.endScenario(Scenario.AGENDA);
			return;
		}

		final DispatchQueue queue = new DispatchQueue(dispatch, true) {

			@Override
			protected void onComplete() {
				calendar.refresh();
				Profiler.INSTANCE.endScenario(Scenario.AGENDA);
			}
		};

		for (final Entry<CalendarType, Integer> calendarEntry : calendars.entrySet()) {

			if (calendarEntry == null) {
				continue;
			}

			final CalendarType calendarType = calendarEntry.getKey();
			final Integer calendarId = calendarEntry.getValue();

			queue.add(new GetCalendar(calendarType, CalendarType.getIdentifier(calendarType, calendarId)), new CommandResultHandler<Calendar>() {

				@Override
				public void onCommandSuccess(final Calendar result) {
					Profiler.INSTANCE.markCheckpoint(Scenario.AGENDA, calendarType + " ended.");
					if(result != null) {
						// Defines the color index of the calendar.
						result.setStyle(calendarType.getColorCode());
                        result.setType(calendarType);

						view.getCalendarsStore().add(new CalendarWrapper(result));
						view.getCalendarsSelectionModel().select(view.getCalendarsStore().getCount() - 1, true);
					}
				}

				@Override
				protected void onCommandFailure(Throwable caught) {
					Profiler.INSTANCE.markCheckpoint(Scenario.AGENDA, calendarType + " ended with error.");
					super.onCommandFailure(caught); 
				}
				
			});
		}
		Profiler.INSTANCE.markCheckpoint(Scenario.AGENDA, "Before queue started.");
		queue.start();
	}
    /**
     * Reload a given type of reminder into the calendar.
     * 
     * @param reminderType Type of reminder to refresh.
     */
    private void reloadEventsOfReminderType(final ReminderType reminderType) {
        final CalendarType calendarType = reminderType == ReminderType.REMINDER ? CalendarType.Reminder : CalendarType.MonitoredPoint;
        
        final List<Calendar> calendars = calendar.getCalendars();
        for (int index = 0; index < calendars.size(); index++) {
            final Calendar currentCalendar = calendars.get(index);
            
            if (currentCalendar.getType() == calendarType) {

                final GetCalendar getCalendar = new GetCalendar(calendarType, currentCalendar.getIdentifier());
                final int location = index;
                
                dispatch.execute(getCalendar, new CommandResultHandler<Calendar>() {
                    
                    @Override
                    protected void onCommandSuccess(Calendar result) {
                        if(result != null) {
                            result.setStyle(calendarType.getColorCode());
                            result.setType(calendarType);
                            calendars.set(location, result);
                            calendar.refresh();
                        }
                    }
                    
                });
                return;
            }
        }
    }
}
