package org.sigmah.client.ui.view.admin.models.importer;

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


import com.extjs.gxt.ui.client.Style;
import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportationSchemeModelsAdminPresenter.ImportationSchemeModelsAdminPresenterHandler;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableBudgetElementDTO;
import org.sigmah.shared.dto.importation.VariableBudgetSubFieldDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;
import java.util.Collections;
import org.sigmah.client.ui.widget.layout.Layouts;

/**
 * View of {@link ImportationSchemeModelsAdminPresenter}.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ImportationSchemeModelsAdminView extends AbstractView implements ImportationSchemeModelsAdminPresenter.View {

	private Grid<ImportationSchemeModelDTO> importationSchemeModelsGrid;
	private Grid<VariableFlexibleElementDTO> variableFlexibleElementsGrid;

	private Button addVariableFlexibleElementButton;
	private Button deleteVariableFlexibleElementButton;
	private Button addImportationSchemeModelButton;
	private Button deleteImportationSchemeModelButton;

	private ImportationSchemeModelsAdminPresenterHandler importationSchemeModelsAdminPresenterHandler;

	@Override
	public void initialize() {

		final ContentPanel importationSchemeModelsPanel = Panels.content(null);
		importationSchemeModelsPanel.setScrollMode(Scroll.AUTOY);
		importationSchemeModelsPanel.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				importationSchemeModelsPanel.getLayoutTarget().setStyleAttribute("overflowX", "hidden");
			}
		});

		importationSchemeModelsGrid = buildImportationSchemeModelsGrid();

		importationSchemeModelsPanel.add(importationSchemeModelsGrid);
		importationSchemeModelsPanel.setTopComponent(importationSchemeModelToolBar());

		final ContentPanel variableFlexibleElementPanel = Panels.content(null);
		variableFlexibleElementPanel.setScrollMode(Scroll.AUTOY);
		variableFlexibleElementsGrid = buildVariableFlexibleElementsGrid();

		variableFlexibleElementPanel.add(variableFlexibleElementsGrid);
		variableFlexibleElementPanel.setTopComponent(variableFlexibleElementToolBar());

		add(importationSchemeModelsPanel, Layouts.borderLayoutData(Style.LayoutRegion.WEST, 250.0f, Layouts.Margin.RIGHT));
		add(variableFlexibleElementPanel, Layouts.borderLayoutData(Style.LayoutRegion.CENTER));

	}

	private Component importationSchemeModelToolBar() {

		// Add button.
		addImportationSchemeModelButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());

		// Delete button.
		deleteImportationSchemeModelButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteImportationSchemeModelButton.disable();
		
		// Building the tool bar.
		final ToolBar toolbar = new ToolBar();
		toolbar.add(addImportationSchemeModelButton);
		toolbar.add(deleteImportationSchemeModelButton);

		return toolbar;

	}

	private Component variableFlexibleElementToolBar() {

		// Add button.
		addVariableFlexibleElementButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addVariableFlexibleElementButton.disable();

		// Delete button.
		deleteVariableFlexibleElementButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteVariableFlexibleElementButton.disable();

		// Building the tool bar.
		final ToolBar toolbar = new ToolBar();
		toolbar.add(addVariableFlexibleElementButton);
		toolbar.add(deleteVariableFlexibleElementButton);

		return toolbar;
	}

	private Grid<VariableFlexibleElementDTO> buildVariableFlexibleElementsGrid() {

		final ColumnConfig fieldColumn = new ColumnConfig("field", I18N.CONSTANTS.adminFlexible(), 200);
		fieldColumn.setRenderer(new GridCellRenderer<VariableFlexibleElementDTO>() {

			@Override
			public Object render(VariableFlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<VariableFlexibleElementDTO> store, Grid<VariableFlexibleElementDTO> grid) {
				
				final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, model.getIsKey() ? 2 : 1);
				panel.setCellPadding(0);
				panel.setCellSpacing(0);
				int column = 0;
				if(model.getIsKey()) {
					panel.setWidget(0, column, IconImageBundle.ICONS.login().createImage());
					panel.getCellFormatter().addStyleName(0, column, "project-grid-code-icon");
					column++;
				}
				// TODO: Replace the text by an anchor and add an edit method.
				panel.setText(0, column, model.getFlexibleElementDTO().getFormattedLabel());
				panel.getCellFormatter().addStyleName(0, column, "project-grid-code");
				
				return panel;
			}
		});

		final ColumnConfig variableColumn = new ColumnConfig("variable", I18N.CONSTANTS.adminImportationSchemeModelVariableHeading(), 200);
		variableColumn.setRenderer(new GridCellRenderer<VariableFlexibleElementDTO>() {

			@Override
			public Object render(VariableFlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<VariableFlexibleElementDTO> store, Grid<VariableFlexibleElementDTO> grid) {
				if (model instanceof VariableBudgetElementDTO) {
					String variableNames = "";
					VariableBudgetElementDTO variableBudgetElement = (VariableBudgetElementDTO) model;
					for (VariableBudgetSubFieldDTO varSubField : variableBudgetElement.getVariableBudgetSubFieldsDTO()) {
						variableNames += varSubField.getVariableDTO().getName() + "; ";
					}
					return variableNames;
				} else {
					return model.getVariableDTO().getName();
				}
			}
		});
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(fieldColumn);
		configs.add(variableColumn);

		ColumnModel cm = new ColumnModel(configs);

		final Grid<VariableFlexibleElementDTO> variableFlexibleElementsDTOGrid = new Grid<VariableFlexibleElementDTO>(new ListStore<VariableFlexibleElementDTO>(), cm);
		variableFlexibleElementsDTOGrid.getView().setForceFit(true);
		variableFlexibleElementsDTOGrid.setAutoHeight(true);
		variableFlexibleElementsDTOGrid.addStyleName("importation-scheme-models-grid");

		return variableFlexibleElementsDTOGrid;
	}

	private Grid<ImportationSchemeModelDTO> buildImportationSchemeModelsGrid() {

		final ColumnConfig importationSchemeColumn = new ColumnConfig("importationScheme", I18N.CONSTANTS.adminImportationScheme(), 250);

		importationSchemeColumn.setRenderer(new GridCellRenderer<ImportationSchemeModelDTO>() {

			@Override
			public Object render(final ImportationSchemeModelDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ImportationSchemeModelDTO> store, Grid<ImportationSchemeModelDTO> grid) {

				final Anchor anchor = new Anchor(model.getImportationSchemeDTO().getName());
				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						importationSchemeModelsAdminPresenterHandler.onClick(model);
					}
				});
				
				final SimplePanel panel = new SimplePanel();
				panel.addStyleName("project-grid-code");
				panel.setWidget(anchor);

				return panel;
			}

		});

		final ColumnModel columnModel = new ColumnModel(Collections.singletonList(importationSchemeColumn));

		final Grid<ImportationSchemeModelDTO> importationSchemeModelGrid = new Grid<ImportationSchemeModelDTO>(new ListStore<ImportationSchemeModelDTO>(), columnModel);
		importationSchemeModelGrid.getView().setForceFit(true);
		importationSchemeModelGrid.setAutoHeight(true);
		importationSchemeModelGrid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
		return importationSchemeModelGrid;
	}

	@Override
	public void setToolbarEnabled(Boolean enable) {
		// TODO set enable false to all element in in toolbar
	}

	@Override
	public ListStore<ImportationSchemeModelDTO> getImportationSchemeModelsStore() {
		return importationSchemeModelsGrid.getStore();
	}

	@Override
	public ListStore<VariableFlexibleElementDTO> getVariableFlexibleElementStore() {
		return variableFlexibleElementsGrid.getStore();
	}

	@Override
	public Grid<ImportationSchemeModelDTO> getImportationSchemeModelsGrid() {
		return importationSchemeModelsGrid;
	}

	@Override
	public Grid<VariableFlexibleElementDTO> getVariableFlexibleElementsGrid() {
		return variableFlexibleElementsGrid;
	}

	@Override
	public Button getAddVariableFlexibleElementButton() {
		return addVariableFlexibleElementButton;
	}

	@Override
	public Button getDeleteVariableFlexibleElementButton() {
		return deleteVariableFlexibleElementButton;
	}

	@Override
	public Button getAddImportationSchemeModelButton() {
		return addImportationSchemeModelButton;
	}

	@Override
	public Button getDeleteImportationSchemeModelButton() {
		return deleteImportationSchemeModelButton;
	}

	@Override
	public void setImportationSchemeModelsAdminPresenterHandler(ImportationSchemeModelsAdminPresenterHandler importationSchemeModelsAdminPresenterHandler) {

		this.importationSchemeModelsAdminPresenterHandler = importationSchemeModelsAdminPresenterHandler;

	}
}
