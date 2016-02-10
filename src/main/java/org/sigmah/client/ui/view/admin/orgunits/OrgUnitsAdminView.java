package org.sigmah.client.ui.view.admin.orgunits;

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
import org.sigmah.client.ui.presenter.admin.orgunits.AddOrgUnitAdminPresenter;
import org.sigmah.client.ui.presenter.admin.orgunits.OrgUnitsAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.orgunit.OrgUnitImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.TreeGridCheckboxSelectionModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.inject.Singleton;

/**
 * {@link AddOrgUnitAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class OrgUnitsAdminView extends AbstractView implements OrgUnitsAdminPresenter.View {

	// CSS style names.
	private static final String STYLE_FLEXIBILITY_ACTION = "flexibility-action";

	private ContentPanel mainPanel;
	private TreeGrid<OrgUnitDTO> treeGrid;
	private ToolBar toolbar;
	private Button addButton;
	private Button moveButton;
	private Button removeButton;

	private TreeGridEventHandler<OrgUnitDTO> handler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		mainPanel = Panels.content(I18N.CONSTANTS.orgunitTree(), Layouts.fitLayout());

		mainPanel.setTopComponent(buildToolbar());
		mainPanel.add(buildTreeGrid());

		add(mainPanel);
	}

	/**
	 * Builds the tree grid component.
	 * 
	 * @return The tree grid component.
	 */
	private Component buildTreeGrid() {

		// --
		// Code column.
		// --

		final ColumnConfig nameColumn = new ColumnConfig();
		nameColumn.setId(OrgUnitDTO.NAME);
		nameColumn.setHeaderHtml(I18N.CONSTANTS.projectName());
		nameColumn.setRenderer(new TreeGridCellRenderer<OrgUnitDTO>());
		nameColumn.setWidth(150);

		// --
		// Title column.
		// --

		final ColumnConfig fullNameColumn = new ColumnConfig();
		fullNameColumn.setId(OrgUnitDTO.FULL_NAME);
		fullNameColumn.setHeaderHtml(I18N.CONSTANTS.projectFullName());
		fullNameColumn.setRenderer(new GridCellRenderer<OrgUnitDTO>() {

			@Override
			public Object render(final OrgUnitDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<OrgUnitDTO> store, final Grid<OrgUnitDTO> grid) {

				final InlineLabel visitButton = new InlineLabel((String) model.get(property));
				visitButton.addStyleName(STYLE_FLEXIBILITY_ACTION);
				visitButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(final ClickEvent e) {
						handler.onRowClickEvent(model);
					}
				});

				return visitButton;
			}
		});

		// --
		// Country column.
		// --

		final ColumnConfig countryColumn = new ColumnConfig();
		countryColumn.setId(OrgUnitDTO.OFFICE_LOCATION_COUNTRY);
		countryColumn.setHeaderHtml(I18N.CONSTANTS.projectCountry());
		countryColumn.setWidth(100);
		countryColumn.setRenderer(new GridCellRenderer<OrgUnitDTO>() {

			@Override
			public Object render(final OrgUnitDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<OrgUnitDTO> store, final Grid<OrgUnitDTO> grid) {

				final CountryDTO country = (CountryDTO) model.get(property);

				if (country == null) {
					return "";
				}

				return country.getName() + " (" + country.getCodeISO() + ')';

			}
		});

		// --
		// OrgUnitModel column.
		// --

		final ColumnConfig modelColumn = new ColumnConfig();
		modelColumn.setId(OrgUnitDTO.MODEL);
		modelColumn.setHeaderHtml(I18N.CONSTANTS.projectModel());
		modelColumn.setWidth(200);
		modelColumn.setRenderer(new GridCellRenderer<OrgUnitDTO>() {

			@Override
			public Object render(final OrgUnitDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<OrgUnitDTO> store, final Grid<OrgUnitDTO> grid) {

				final OrgUnitModelDTO orgUnitModel = (OrgUnitModelDTO) model.get(property);
				return orgUnitModel != null ? orgUnitModel.getName() : "";
			}

		});

		// --
		// Tree store.
		// --

		final TreeStore<OrgUnitDTO> store = new TreeStore<OrgUnitDTO>();
		store.setSortInfo(new SortInfo(OrgUnitDTO.NAME, SortDir.ASC));

		store.setStoreSorter(new StoreSorter<OrgUnitDTO>() {

			@Override
			public int compare(final Store<OrgUnitDTO> store, final OrgUnitDTO m1, final OrgUnitDTO m2, final String property) {

				if (OrgUnitDTO.OFFICE_LOCATION_COUNTRY.equals(property)) {

					final CountryDTO country1 = (CountryDTO) m1.get(property);
					final CountryDTO country2 = (CountryDTO) m2.get(property);
					return country1.getName().compareToIgnoreCase(country2.getName());

				} else {
					return super.compare(store, m1, m2, property);
				}
			}
		});

		// --
		// Tree selection model.
		// --

		final CheckBoxSelectionModel<OrgUnitDTO> selectionModel = new TreeGridCheckboxSelectionModel<OrgUnitDTO>();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);

		final List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(selectionModel.getColumn());
		columns.add(nameColumn);
		columns.add(fullNameColumn);
		columns.add(countryColumn);
		columns.add(modelColumn);

		// --
		// Tree grid.
		// --

		treeGrid = new TreeGrid<OrgUnitDTO>(store, new ColumnModel(columns));
		treeGrid.setBorders(true);
		treeGrid.getStyle().setLeafIcon(OrgUnitImageBundle.ICONS.orgUnitSmall());
		treeGrid.getStyle().setNodeCloseIcon(OrgUnitImageBundle.ICONS.orgUnitSmall());
		treeGrid.getStyle().setNodeOpenIcon(OrgUnitImageBundle.ICONS.orgUnitSmallTransparent());
		treeGrid.setAutoExpandColumn(OrgUnitDTO.FULL_NAME);
		treeGrid.setTrackMouseOver(false);
		treeGrid.setSelectionModel(selectionModel);
		treeGrid.addPlugin(selectionModel);

		return treeGrid;
	}

	/**
	 * Builds the toolbar component.
	 * 
	 * @return The toolbar component.
	 */
	private Component buildToolbar() {

		// --
		// Expand all button.
		// --

		final Button expandButton = Forms.button(I18N.CONSTANTS.expandAll(), IconImageBundle.ICONS.expand(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				treeGrid.expandAll();
			}
		});

		// --
		// Collapse all button.
		// --

		final Button collapseButton = Forms.button(I18N.CONSTANTS.collapseAll(), IconImageBundle.ICONS.collapse(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				treeGrid.collapseAll();
			}
		});

		// --
		// Actions buttons.
		// --

		addButton = Forms.button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		moveButton = Forms.button(I18N.CONSTANTS.adminOrgUnitMove(), IconImageBundle.ICONS.up());
		removeButton = Forms.button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());

		// --
		// Toolbar.
		// --

		toolbar = new ToolBar();
		toolbar.setAlignment(HorizontalAlignment.LEFT);

		toolbar.add(expandButton);
		toolbar.add(collapseButton);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(addButton);
		toolbar.add(moveButton);
		toolbar.add(removeButton);

		return toolbar;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeGrid<OrgUnitDTO> getTreeGrid() {
		return treeGrid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeStore<OrgUnitDTO> getStore() {
		return treeGrid.getTreeStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTreeGridEventHandler(final TreeGridEventHandler<OrgUnitDTO> handler) {
		this.handler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getMoveButton() {
		return moveButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getRemoveButton() {
		return removeButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getMainPanel() {
		return mainPanel;
	}

}
