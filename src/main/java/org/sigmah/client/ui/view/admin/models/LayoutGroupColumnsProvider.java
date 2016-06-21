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
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.LayoutGroupAdminPresenter;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.util.ColumnProviders;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.ElementTypeEnum;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

abstract class LayoutGroupColumnsProvider {

	/**
	 * Returns <code>true</code> if the parent model is editable.
	 * 
	 * @return <code>true</code> if the parent model is editable, <code>false</code> otherwise.
	 */
	protected abstract boolean isEditable();
	
	/**
	 * Returns the {@link GridEventHandler} implementation.
	 * 
	 * @return The {@link GridEventHandler} implementation.
	 */
	protected abstract GridEventHandler<LayoutGroupDTO> getGridEventHandler();

	/**
	 * Gets the columns model for the flexible elements admin grid.
	 * 
	 * @return The columns model for the flexible elements admin grid.
	 */
	public ColumnModel getColumnModel() {


		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// --
		// Group Name column.
		// --

		
		ColumnConfig column = new ColumnConfig(LayoutGroupDTO.TITLE, I18N.CONSTANTS.adminFlexibleName(), 200);
		column.setRenderer(new GridCellRenderer<LayoutGroupDTO>() {

			@Override
			public Object render(final LayoutGroupDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<LayoutGroupDTO> store, final Grid<LayoutGroupDTO> grid) {

				return ColumnProviders.renderLink(model.getTitle(), new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {
						model.set
(LayoutGroupAdminPresenter.ON_GROUP_CLICK_EVENT_KEY, Boolean.TRUE);
						getGridEventHandler().onRowClickEvent
(model);
					}

				});
			}
		});
		configs.add(column);
		
		// --
		// Containers column.
		// --

		column = new ColumnConfig(LayoutGroupDTO.PARENT_LAYOUT, I18N.CONSTANTS.adminFlexibleContainer(), 120);
		column.setRenderer(new GridCellRenderer<LayoutGroupDTO>() {

			@Override
			public Object render(final LayoutGroupDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<LayoutGroupDTO> store, final Grid<LayoutGroupDTO> grid) {

				final BaseModelData container = model.getParentLayout();
				return ColumnProviders.renderText(container.get("name"));
			}

		});
		configs.add(column);
        // --
		// Position column.
		// --

		column = new ColumnConfig("position", I18N.CONSTANTS.adminGroupsPosition(), 50);
		column.setRenderer(new GridCellRenderer<LayoutGroupDTO>() {

			@Override
			public Object render(final LayoutGroupDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<LayoutGroupDTO> store, final Grid<LayoutGroupDTO> grid) {

				return ColumnProviders.renderText(model.getRow());
			}
		});
		configs.add(column);
    	return new ColumnModel(configs);
	}

	
}
