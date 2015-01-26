package org.sigmah.client.ui.presenter.project;

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
