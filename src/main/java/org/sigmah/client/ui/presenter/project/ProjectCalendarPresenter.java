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
import java.util.HashMap;
import java.util.Map;

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
        final Map<Integer, Integer> calendars = new HashMap<Integer, Integer>();
        Integer calendarId = 1;
        calendars.put(calendarId++, getProject().getId());
        calendars.put(calendarId++, getProject().getCalendarId());
        calendars.put(calendarId++, getProject().getPointsList().getId());
        calendars.put(calendarId++, getProject().getRemindersList().getId());

        if (!getProject().getFunded().isEmpty()) {
            for (int i = 0; i < getProject().getFunded().size(); i++) {
                calendars.put(calendarId++, getProject().getFunded().get(i).getFunded().getId());
                calendars.put(calendarId++, getProject().getFunded().get(i).getFunded().getCalendarId());
                calendars.put(calendarId++, getProject().getFunded().get(i).getFunded().getPointsList().getId());
                calendars.put(calendarId++, getProject().getFunded().get(i).getFunded().getRemindersList().getId());
            }
        }
        if (!getProject().getFunding().isEmpty()) {
            for (int i = 0; i < getProject().getFunding().size(); i++) {
                calendars.put(calendarId++, getProject().getFunding().get(i).getFunding().getId());
                calendars.put(calendarId++, getProject().getFunding().get(i).getFunding().getCalendarId());
                calendars.put(calendarId++, getProject().getFunding().get(i).getFunding().getPointsList().getId());
                calendars.put(calendarId++, getProject().getFunding().get(i).getFunding().getRemindersList().getId());
            }
        }
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
