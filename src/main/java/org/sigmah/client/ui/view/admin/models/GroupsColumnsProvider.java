package org.sigmah.client.ui.view.admin.models;

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
import java.util.Collection;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ColumnProviders;
import org.sigmah.shared.dto.GroupsDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;


abstract class GroupsColumnsProvider {

	/**
	 * Returns the {@link GridEventHandler} implementation.
	 * 
	 * @return The {@link GridEventHandler} implementation.
	 */
	protected abstract GridEventHandler<GroupsDTO> getGridEventHandler();

	public ColumnModel getColumnModel() {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// --
		// Name column.
		// --

		ColumnConfig column = new ColumnConfig(GroupsDTO.NAME, I18N.CONSTANTS.adminGroupsName(), 100);
		column.setRenderer(new GridCellRenderer<GroupsDTO>() {

			@Override
			public Object render(final GroupsDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<GroupsDTO> store, final Grid<GroupsDTO> grid) {

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
		// Vertical Position column.
		// --

		configs.add(new ColumnConfig(GroupsDTO.POSITION, I18N.CONSTANTS.adminGroupsPosition(),150));
		
		// --
		// Containers column.
		// --

		column = new ColumnConfig(GroupsDTO.CONTAINER, I18N.CONSTANTS.adminGroupsContainer(), 200);
		column.setRenderer(new GridCellRenderer<GroupsDTO>() {

			@Override
			public Object render(final GroupsDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<GroupsDTO> store, final Grid<GroupsDTO> grid) {

				final BaseModelData container = model.getContainerModel();
				return ColumnProviders.renderText(container.get("name"));
			}

		});
		configs.add(column);

		return new ColumnModel(configs);
	}

	/**
	 * Renders a collection of string values.
	 * 
	 * @param values
	 *          The values.
	 * @param defaultLabel
	 *          The default label rendered if the {@code values} collection is {@code null} or empty.
	 * @return The rendering of the given {@code values}.
	 */
	private static Object renderCollection(final Collection<String> values, final String defaultLabel) {

		final StringBuilder builder = new StringBuilder();

		if (ClientUtils.isNotEmpty(values)) {

			for (final Object value : values) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(value);
			}

		} else {
			builder.append(defaultLabel);
		}

		return ColumnProviders.renderText(builder.toString());
	}

}
