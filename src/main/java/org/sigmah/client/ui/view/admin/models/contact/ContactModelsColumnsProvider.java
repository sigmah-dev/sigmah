package org.sigmah.client.ui.view.admin.models.contact;

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


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.util.ColumnProviders;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Provides Contact models main grid columns configuration.
 */
abstract class ContactModelsColumnsProvider {

	/**
	 * Returns the grid event handler.
	 * 
	 * @return The grid event handler.
	 */
	abstract GridEventHandler<ContactModelDTO> getGridEventHandler();

	/**
	 * Gets the columns model for the Contact models grid.
	 * 
	 * @return The columns model for the Contact models grid.
	 */
	public ColumnModel getColumnModel() {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// --
		// Name column.
		// --

		ColumnConfig column = new ColumnConfig(ContactModelDTO.NAME, I18N.CONSTANTS.adminContactModelName(), 200);
		column.setRenderer(new GridCellRenderer<ContactModelDTO>() {

			@Override
			public Object render(final ContactModelDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ContactModelDTO> store, final Grid<ContactModelDTO> grid) {

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
		// Status column.
		// --

		column = new ColumnConfig(ContactModelDTO.STATUS, I18N.CONSTANTS.adminProjectModelsStatus(), 100);
		column.setRenderer(new GridCellRenderer<ContactModelDTO>() {

			@Override
			public Object render(final ContactModelDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ContactModelDTO> store, final Grid<ContactModelDTO> grid) {

				final ProjectModelStatus status = !model.isUnderMaintenance() ?
					model.getStatus() : ProjectModelStatus.UNDER_MAINTENANCE;
				
				return status != null ? ProjectModelStatus.getName(status) : "";
			}
		});
		configs.add(column);

		// --
		// Contact type column.
		// --

		column = new ColumnConfig(ContactModelDTO.TYPE, I18N.CONSTANTS.adminContactModelType(), 100);
		column.setRenderer(new GridCellRenderer<ContactModelDTO>() {

			@Override
			public Object render(final ContactModelDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<ContactModelDTO> store, final Grid<ContactModelDTO> grid) {

				ContactModelType type = model.get(property);

				String typeLabel = I18N.CONSTANTS.contactTypeIndividualLabel();

				if(type == ContactModelType.ORGANIZATION) {
					typeLabel = I18N.CONSTANTS.contactTypeOrganizationLabel();
				}

				return typeLabel;
			}
		});
		configs.add(column);

		return new ColumnModel(configs);
	}

}
