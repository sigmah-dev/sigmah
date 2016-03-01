package org.sigmah.client.ui.presenter.project;

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

import java.util.EnumMap;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.presenter.calendar.CalendarPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.project.ProjectCalendarView;
import org.sigmah.shared.dto.calendar.CalendarType;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;

/**
 * <p>
 * Project's calendar presenter which manages the {@link ProjectCalendarView}.
 * </p>
 * <p>
 * Delegates its entire logic business to the {@link CalendarPresenter}.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectCalendarPresenter extends AbstractProjectPresenter<ProjectCalendarPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectCalendarView.class)
	public static interface View extends AbstractProjectPresenter.View {

		/**
		 * Provides the reports presenter's view.<br>
		 * Should be called before view initialization.
		 * 
		 * @param view
		 *          The view.
		 */
		void provideCalendarView(ViewInterface view);

	}

	/**
	 * The calendar presenter to which business logic is delegated.
	 */
	private final CalendarPresenter calendarPresenter;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 * @param calendarPresenterProvider
	 *          The {@link CalendarPresenter} provider.
	 */
	@Inject
	public ProjectCalendarPresenter(final View view, final Injector injector, final Provider<CalendarPresenter> calendarPresenterProvider) {
		super(view, injector);
		calendarPresenter = calendarPresenterProvider.get();
		view.provideCalendarView(calendarPresenter.getView());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.PROJECT_CALENDAR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		calendarPresenter.initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {
		Profiler.INSTANCE.markCheckpoint(Scenario.AGENDA, "onPageRequest started");
		final EnumMap<CalendarType, Integer> calendars = new EnumMap<CalendarType, Integer>(CalendarType.class);
		calendars.put(CalendarType.Activity, getProject().getId());
		calendars.put(CalendarType.Personal, getProject().getCalendarId());
		calendars.put(CalendarType.MonitoredPoint, getProject().getPointsList().getId());
		calendars.put(CalendarType.Reminder, getProject().getRemindersList().getId());
		calendarPresenter.reload(calendars);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onViewRevealed() {
		calendarPresenter.onViewRevealed();
	}

}
