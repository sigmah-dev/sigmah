package org.sigmah.client.ui.view.project;

import org.sigmah.client.ui.presenter.project.ProjectCalendarPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.view.base.ViewInterface;

import com.google.inject.Singleton;

/**
 * {@link ProjectCalendarPresenter} view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectCalendarView extends AbstractView implements ProjectCalendarPresenter.View {

	/**
	 * The calendar presenter's view.
	 */
	// Provided before current view initialization.
	private ViewInterface view;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		add(view);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void provideCalendarView(final ViewInterface view) {
		this.view = view;
	}

}
