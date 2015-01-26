package org.sigmah.client.ui.view.project.indicator;

import com.google.inject.Inject;
import org.sigmah.client.ui.presenter.project.indicator.ProjectIndicatorEntriesPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.inject.Singleton;
import org.sigmah.client.ui.view.pivot.ProjectPivotContainer;

/**
 * {@link ProjectIndicatorEntriesPresenter} view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectIndicatorEntriesView extends AbstractView implements ProjectIndicatorEntriesPresenter.View {

	@Inject
	private ProjectPivotContainer projectPivotContainer;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		add(projectPivotContainer);
	}

	@Override
	public ProjectPivotContainer getProjectPivotContainer() {
		return projectPivotContainer;
	}

}
