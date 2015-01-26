package org.sigmah.client.ui.view.orgunit;

import org.sigmah.client.ui.presenter.orgunit.OrgUnitCalendarPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.view.base.ViewInterface;

import com.google.inject.Singleton;

/**
 * OrgUnitCalendar view implementation.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class OrgUnitCalendarView extends AbstractView implements OrgUnitCalendarPresenter.View {

	/**
	 * The calendar presenter's view.
	 */
	// Provided before current view initialization.
	private ViewInterface view;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void provideCalendarView(final ViewInterface view) {
		this.view = view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		add(view);
	}

}
