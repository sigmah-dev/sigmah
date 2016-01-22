package org.sigmah.client.ui.widget.orgunit;

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
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.orgunit.OrgUnitImageBundle;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.util.TreeGridCheckboxSelectionModel;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * <p>
 * Widget representing the organizational chart (tree) of an org unit.
 * </p>
 * <p>
 * To handle component events, see {@link #setTreeGridEventHandler(TreeGridEventHandler)}.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class OrgUnitTreeGrid implements HasTreeGrid<OrgUnitDTO> {

	// CSS style names.
	private static final String STYLE_FLEXIBILITY_ACTION = "flexibility-action";

	/**
	 * The tree grid.
	 */
	private final TreeGrid<OrgUnitDTO> tree;

	/**
	 * The actions toolbar.
	 */
	private final ToolBar toolbar;

	/**
	 * The grid events handler implementation.
	 */
	private HasTreeGrid.TreeGridEventHandler<OrgUnitDTO> eventsHandler;

	/**
	 * The selection model (can be {@code null} if the tree doesn't manage a selection model).
	 */
	private TreeGridCheckboxSelectionModel<OrgUnitDTO> selectionModel;

	/**
	 * Initializes a new {@code OrgUnitTreeGrid} widget.
	 * 
	 * @param hasSelectionModel
	 *          {@code true} if the tree grid widget manages a selection model, {@code false} if it does not.
	 */
	public OrgUnitTreeGrid(final boolean hasSelectionModel) {

		// --
		// Creates columns.
		// --

		final List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		// Name column.
		final ColumnConfig nameColumn = new ColumnConfig();
		nameColumn.setId(OrgUnitDTO.NAME);
		nameColumn.setHeaderText(I18N.CONSTANTS.projectName());
		nameColumn.setRenderer(new TreeGridCellRenderer<OrgUnitDTO>());
		nameColumn.setWidth(150);

		// Full Name column.
		final ColumnConfig fullNameColumn = new ColumnConfig();
		fullNameColumn.setId(OrgUnitDTO.FULL_NAME);
		fullNameColumn.setHeaderText(I18N.CONSTANTS.projectFullName());
		fullNameColumn.setWidth(250);
		fullNameColumn.setRenderer(new GridCellRenderer<OrgUnitDTO>() {

			@Override
			public Object render(final OrgUnitDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<OrgUnitDTO> store, final Grid<OrgUnitDTO> grid) {

				final com.google.gwt.user.client.ui.Label visitButton = new com.google.gwt.user.client.ui.Label((String) model.get(property));
				visitButton.addStyleName(STYLE_FLEXIBILITY_ACTION);

				visitButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {
						if (eventsHandler != null) {
							eventsHandler.onRowClickEvent(model);
						}
					}
				});

				return visitButton;
			}
		});

		// Country column.
		final ColumnConfig countryColumn = new ColumnConfig();
		countryColumn.setId(OrgUnitDTO.OFFICE_LOCATION_COUNTRY);
		countryColumn.setHeaderText(I18N.CONSTANTS.projectCountry());
		countryColumn.setWidth(100);
		countryColumn.setRenderer(new GridCellRenderer<OrgUnitDTO>() {

			@Override
			public Object render(final OrgUnitDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<OrgUnitDTO> store, final Grid<OrgUnitDTO> grid) {

				final CountryDTO country = (CountryDTO) model.get(property);

				return CountryDTO.toString(country);
			}
		});

		// Adds columns.
		if (hasSelectionModel) {
			// Tree selection model
			selectionModel = new TreeGridCheckboxSelectionModel<OrgUnitDTO>();
			columns.add(selectionModel.getColumn());
		}

		columns.add(nameColumn);
		columns.add(fullNameColumn);
		columns.add(countryColumn);

		// --
		// Tree store.
		// --

		final TreeStore<OrgUnitDTO> store = new TreeStore<OrgUnitDTO>();
		store.setSortInfo(new SortInfo(OrgUnitDTO.NAME, SortDir.ASC));

		store.setStoreSorter(new StoreSorter<OrgUnitDTO>() {

			@Override
			public int compare(final Store<OrgUnitDTO> store, final OrgUnitDTO m1, final OrgUnitDTO m2, final String property) {

				if (OrgUnitDTO.OFFICE_LOCATION_COUNTRY.equals(property)) {
					return ((CountryDTO) m1.get(property)).getName().compareToIgnoreCase(((CountryDTO) m2.get(property)).getName());
				} else {
					return super.compare(store, m1, m2, property);
				}
			}
		});

		// --
		// Tree grid.
		// --

		tree = new TreeGrid<OrgUnitDTO>(store, new ColumnModel(columns));
		tree.setBorders(true);
		tree.getStyle().setLeafIcon(OrgUnitImageBundle.ICONS.orgUnitSmall());
		tree.getStyle().setNodeCloseIcon(OrgUnitImageBundle.ICONS.orgUnitSmall());
		tree.getStyle().setNodeOpenIcon(OrgUnitImageBundle.ICONS.orgUnitSmallTransparent());
		tree.setTrackMouseOver(false);
		tree.addPlugin(createOrgUnitFilters());

		if (hasSelectionModel) {
			tree.setSelectionModel(selectionModel);
			tree.addPlugin(selectionModel);
		}

		// --
		// Expand all button.
		// --

		final Button expandButton = new Button(I18N.CONSTANTS.expandAll(), IconImageBundle.ICONS.expand(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				tree.expandAll();
			}
		});

		// --
		// Collapse all button.
		// --

		final Button collapseButton = new Button(I18N.CONSTANTS.collapseAll(), IconImageBundle.ICONS.collapse(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				tree.collapseAll();
			}
		});

		// --
		// Toolbar.
		// --

		toolbar = new ToolBar();
		toolbar.setAlignment(HorizontalAlignment.LEFT);

		toolbar.add(expandButton);
		toolbar.add(collapseButton);
	}

	/**
	 * Creates filters for OrgUnits tree grid widget.
	 * 
	 * @return The {@link GridFilters} instance.
	 */
	private static GridFilters createOrgUnitFilters() {

		final GridFilters filters = new GridFilters();

		filters.setLocal(true);
		// Data index of each filter should be identical with column id in ColumnModel of TreeGrid
		filters.addFilter(new StringFilter(OrgUnitDTO.NAME));
		filters.addFilter(new StringFilter(OrgUnitDTO.FULL_NAME));
		filters.addFilter(new StringFilter(OrgUnitDTO.OFFICE_LOCATION_COUNTRY) {

			@Override
			@SuppressWarnings("unchecked")
			protected <X> X getModelValue(final ModelData model) {

				final CountryDTO country = (CountryDTO) super.getModelValue(model);

				return (X) CountryDTO.toString(country);
			}

		});

		return filters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeGrid<OrgUnitDTO> getTreeGrid() {
		return tree;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeStore<OrgUnitDTO> getStore() {
		return tree.getTreeStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTreeGridEventHandler(final HasTreeGrid.TreeGridEventHandler<OrgUnitDTO> handler) {
		this.eventsHandler = handler;
	}

	public ToolBar getToolbar() {
		return toolbar;
	}

	public void addToolbarButton(final Button button) {

		if (button == null) {
			return;
		}

		toolbar.add(new SeparatorToolItem());
		toolbar.add(button);
	}

}
