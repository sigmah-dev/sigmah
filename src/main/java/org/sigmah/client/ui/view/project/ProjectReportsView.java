package org.sigmah.client.ui.view.project;

import org.sigmah.client.ui.presenter.project.ProjectReportsPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.view.base.ViewInterface;

import com.google.inject.Singleton;

/**
 * {@link ProjectReportsPresenter} view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectReportsView extends AbstractView implements ProjectReportsPresenter.View {

	/**
	 * The reports presenter's view.
	 */
	// Provided before current view initialization.
	private ViewInterface view;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void provideReportsView(final ViewInterface view) {
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
