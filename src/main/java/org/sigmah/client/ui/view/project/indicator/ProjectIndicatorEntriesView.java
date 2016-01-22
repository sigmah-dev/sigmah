package org.sigmah.client.ui.view.project.indicator;

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
