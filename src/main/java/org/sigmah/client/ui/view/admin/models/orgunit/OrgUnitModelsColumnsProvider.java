package org.sigmah.client.ui.view.admin.models.orgunit;

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
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.util.ColumnProviders;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Provides OrgUnit models main grid columns configuration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
abstract class OrgUnitModelsColumnsProvider {

	/**
	 * Returns the grid event handler.
	 * 
	 * @return The grid event handler.
	 */
	abstract GridEventHandler<OrgUnitModelDTO> getGridEventHandler();

	/**
	 * Gets the columns model for the OrgUnit models grid.
	 * 
	 * @return The columns model for the OrgUnit models grid.
	 */
	public ColumnModel getColumnModel() {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// --
		// Name column.
		// --

		ColumnConfig column = new ColumnConfig(OrgUnitModelDTO.NAME, I18N.CONSTANTS.adminOrgUnitsModelName(), 200);
		column.setRenderer(new GridCellRenderer<OrgUnitModelDTO>() {

			@Override
			public Object render(final OrgUnitModelDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<OrgUnitModelDTO> store, final Grid<OrgUnitModelDTO> grid) {

				// Name link.
				return ColumnProviders.renderLink(model.getName(), new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {
						getGridEventHandler().onRowClickEvent(model);
					}
				});
			}

		});
		configs.add(column);

		// --
		// Title column.
		// --

		configs.add(new ColumnConfig(OrgUnitModelDTO.TITLE, I18N.CONSTANTS.adminOrgUnitsModelTitle(), 200));

		// --
		// Has budget column.
		// --

		column = new ColumnConfig(OrgUnitModelDTO.HAS_BUDGET, I18N.CONSTANTS.adminOrgUnitsModelHasBudget(), 75);
		column.setRenderer(new GridCellRenderer<OrgUnitModelDTO>() {

			@Override
			public Object render(final OrgUnitModelDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<OrgUnitModelDTO> store, final Grid<OrgUnitModelDTO> grid) {

				return ColumnProviders.renderBoolean(model.getHasBudget(), I18N.CONSTANTS.adminOrgUnitsModelHasBudget());
			}
		});
		configs.add(column);

		// --
		// Can contain projects column.
		// --

		column = new ColumnConfig(OrgUnitModelDTO.CAN_CONTAIN_PROJECTS, I18N.CONSTANTS.adminOrgUnitsModelContainProjects(), 75);
		column.setRenderer(new GridCellRenderer<OrgUnitModelDTO>() {

			@Override
			public Object render(final OrgUnitModelDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<OrgUnitModelDTO> store, final Grid<OrgUnitModelDTO> grid) {

				return ColumnProviders.renderBoolean(model.getCanContainProjects(), I18N.CONSTANTS.adminOrgUnitsModelContainProjects());
			}
		});
		configs.add(column);

		// --
		// Status column.
		// --

		column = new ColumnConfig(OrgUnitModelDTO.STATUS, I18N.CONSTANTS.adminProjectModelsStatus(), 200);
		column.setRenderer(new GridCellRenderer<OrgUnitModelDTO>() {

			@Override
			public Object render(final OrgUnitModelDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<OrgUnitModelDTO> store, final Grid<OrgUnitModelDTO> grid) {

				final ProjectModelStatus status = !model.isUnderMaintenance() ?
					model.getStatus() : ProjectModelStatus.UNDER_MAINTENANCE;
				
				return status != null ? ProjectModelStatus.getName(status) : "";
			}
		});
		configs.add(column);

		return new ColumnModel(configs);
	}

}
