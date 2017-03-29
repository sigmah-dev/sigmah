package org.sigmah.client.ui.presenter.orgunit;

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
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget.LoadingMode;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget.RefreshMode;
import org.sigmah.client.ui.view.orgunit.OrgUnitDashboardView;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.ui.widget.HasTreeGrid.TreeGridEventHandler;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * OrgUnit Dashboard Presenter.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

public class OrgUnitDashboardPresenter extends AbstractOrgUnitPresenter<OrgUnitDashboardPresenter.View> {

	/**
	 * Presenter's view interface.
	 */
	
	public static interface View extends AbstractOrgUnitPresenter.View {

		/**
		 * Returns the OrgUnit Tree
		 * 
		 * @return the OrgUnit Tree
		 */
		HasTreeGrid<OrgUnitDTO> getOrgUnitsTreeGrid();

		/**
		 * Sets the org units panel header title.
		 * 
		 * @param title
		 *          The new title.
		 */
		void setOrgUnitsPanelTitle(String title);

		/**
		 * get Project List Widget
		 * 
		 * @return ProjectsListWidget
		 */
		ProjectsListWidget getProjectsList();

	}

	
	public OrgUnitDashboardPresenter(View view, ClientFactory factory) {
		super(view, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ORGUNIT_DASHBOARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Org units tree grid events handler.
		view.getOrgUnitsTreeGrid().setTreeGridEventHandler(new TreeGridEventHandler<OrgUnitDTO>() {

			@Override
			public void onRowClickEvent(final OrgUnitDTO rowElement) {
				eventBus.navigateRequest(Page.ORGUNIT_DASHBOARD.requestWith(RequestParameter.ID, rowElement.getId()));
			}
		});

		// Projects widget initialization.
		view.getProjectsList().init(RefreshMode.ALWAYS, LoadingMode.ONE_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(PageRequest request) {

		// Loads orgunit.
		loadOrgUnits(getOrgUnit());

		// Reloads projects.
		view.getProjectsList().refresh(false, true, getOrgUnit().getId());
	}

	/**
	 * Retrieves OrgUnits and populates tree grid store.
	 * 
	 * @param orgUnit
	 *          The current {@link OrgUnitDTO}.
	 */
	private void loadOrgUnits(final OrgUnitDTO orgUnit) {

		view.setOrgUnitsPanelTitle(I18N.CONSTANTS.orgunitTree() + " - " + orgUnit.getName() + " (" + orgUnit.getFullName() + ")");

		if (!orgUnit.getChildrenOrgUnits().isEmpty()) {

			view.getOrgUnitsTreeGrid().getStore().removeAll();

			for (final OrgUnitDTO childOrgUnit : orgUnit.getChildrenOrgUnits()) {
				view.getOrgUnitsTreeGrid().getStore().add(childOrgUnit, true);
			}

			view.getOrgUnitsTreeGrid().getTreeGrid().setExpanded(view.getOrgUnitsTreeGrid().getStore().getRootItems().get(0), true, false);

		} else {
			view.setOrgUnitsPanelTitle(I18N.CONSTANTS.orgunitTree() + " - " + orgUnit.getName() + " (" + orgUnit.getFullName() + ")");
			view.getOrgUnitsTreeGrid().getStore().removeAll();
		}
	}

}
