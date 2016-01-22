package org.sigmah.client.ui.view.admin.models.project;

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


import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.project.ProjectModelsAdminPresenter.ProjectTypeProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;

/**
 * Provides project models main grid columns configuration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
abstract class ProjectModelsColumnsProvider {

	/**
	 * Returns the grid event handler.
	 * 
	 * @return The grid event handler.
	 */
	abstract GridEventHandler<ProjectModelDTO> getGridEventHandler();

	/**
	 * Returns the {@link ProjectTypeProvider} implementation.
	 * 
	 * @return The {@link ProjectTypeProvider} implementation.
	 */
	abstract ProjectTypeProvider getProjectTypeProvider();

	/**
	 * Gets the columns model for the project models grid.
	 * 
	 * @return The columns model for the project models grid.
	 */
	public ColumnModel getColumnModel() {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// --
		// Name column.
		// --

		ColumnConfig column = new ColumnConfig(ProjectModelDTO.NAME, I18N.CONSTANTS.adminProjectModelsName(), 300);
		column.setRenderer(new GridCellRenderer<ProjectModelDTO>() {

			@Override
			public Object render(final ProjectModelDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectModelDTO> store, final Grid<ProjectModelDTO> grid) {

				final Anchor nameLink = new Anchor((String) model.get(property));
				nameLink.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {
						getGridEventHandler().onRowClickEvent(model);
					}
				});

				final ProjectModelType type = getProjectTypeProvider().getProjectModelType(model);

				if (type == null) {
					return nameLink;
				}

				final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 2);
				panel.setCellPadding(0);
				panel.setCellSpacing(0);
				panel.setWidget(0, 0, FundingIconProvider.getProjectTypeIcon(type, IconSize.MEDIUM).createImage());
				panel.getCellFormatter().addStyleName(0, 0, "project-grid-code-icon");
				panel.setWidget(0, 1, nameLink);
				panel.getCellFormatter().addStyleName(0, 1, "project-grid-code");

				return panel;
			}
		});
		configs.add(column);

		// --
		// Status column.
		// --

		column = new ColumnConfig(ProjectModelDTO.STATUS, I18N.CONSTANTS.adminProjectModelsStatus(), 300);
		column.setRenderer(new GridCellRenderer<ProjectModelDTO>() {

			@Override
			public Object render(final ProjectModelDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectModelDTO> store, final Grid<ProjectModelDTO> grid) {

				final ProjectModelStatus status = !model.isUnderMaintenance() ?
					model.getStatus() : ProjectModelStatus.UNDER_MAINTENANCE;
				
				return status != null ? ProjectModelStatus.getName(status) : "";
			}
		});
		configs.add(column);

		// --
		// Grid and store initialization.
		// --

		return new ColumnModel(configs);
	}

}
