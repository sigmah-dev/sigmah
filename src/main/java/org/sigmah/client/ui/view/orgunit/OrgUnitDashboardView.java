package org.sigmah.client.ui.view.orgunit;

import org.sigmah.client.ClientFactory;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitDashboardPresenter;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.orgunit.OrgUnitTreeGrid;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

public class OrgUnitDashboardView extends AbstractView implements OrgUnitDashboardPresenter.View {

	
	ClientFactory factory;

	private OrgUnitTreeGrid tree;
	private ContentPanel contentPanelOrgUnit;
	private ProjectsListWidget projectsListWidget;
	
	public OrgUnitDashboardView(ClientFactory factory) {
		this.factory = factory;
	}

	@Override
	public void initialize() {

		final LayoutContainer container = Layouts.vBox();
		container.add(createOrgUnitsPanel(), Layouts.vBoxData(Margin.BOTTOM));
		container.add(createProjectsPanel(), Layouts.vBoxData());

		add(container);
	}

	private Component createOrgUnitsPanel() {

		contentPanelOrgUnit = Panels.content(I18N.CONSTANTS.orgunitTree());
		tree = new OrgUnitTreeGrid(false);
		tree.getDisplayOnlyMainOrgUnitCheckbox().setVisible(false);
		contentPanelOrgUnit.setTopComponent(tree.getToolbar());
		contentPanelOrgUnit.add(tree.getTreeGrid());

		return contentPanelOrgUnit;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOrgUnitsPanelTitle(String title) {
		contentPanelOrgUnit.setHeadingText(title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasTreeGrid<OrgUnitDTO> getOrgUnitsTreeGrid() {
		return tree;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectsListWidget getProjectsList() {
		return projectsListWidget;
	}

	/**
	 * Creates the projects component.
	 *
	 * @return The projects component widget.
	 */
	private Widget createProjectsPanel() {

		projectsListWidget = factory.getProjectsListWidget();
		projectsListWidget.initialize();

		return projectsListWidget.getView().asWidget();

	}

}
