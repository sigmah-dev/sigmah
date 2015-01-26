package org.sigmah.client.ui.presenter.orgunit;

import java.util.EnumMap;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.presenter.calendar.CalendarPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.orgunit.OrgUnitCalendarView;
import org.sigmah.shared.dto.calendar.CalendarType;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * <p>
 * OrgUnit Calendar Presenter.
 * </p>
 * <p>
 * Delegates its entire logic business to the {@link CalendarPresenter}.
 * </p>
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class OrgUnitCalendarPresenter extends AbstractOrgUnitPresenter<OrgUnitCalendarPresenter.View> {

	/**
	 * Presenter's view interface.
	 */
	@ImplementedBy(OrgUnitCalendarView.class)
	public static interface View extends AbstractOrgUnitPresenter.View {

		/**
		 * Provides the reports presenter's view.<br>
		 * Should be called before view initialization.
		 * 
		 * @param view
		 *          The view.
		 */
		// Should be executed before view initialization.
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
	protected OrgUnitCalendarPresenter(final View view, final Injector injector, final Provider<CalendarPresenter> calendarPresenterProvider) {
		super(view, injector);
		calendarPresenter = calendarPresenterProvider.get();
		view.provideCalendarView(calendarPresenter.getView());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ORGUNIT_CALENDAR;
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
		calendars.put(CalendarType.Activity, getOrgUnit().getId());
		calendars.put(CalendarType.Personal, getOrgUnit().getCalendarId());
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
