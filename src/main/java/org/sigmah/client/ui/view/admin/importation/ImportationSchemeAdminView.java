package org.sigmah.client.ui.view.admin.importation;

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

import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.importation.ImportationSchemeAdminPresenter;
import org.sigmah.client.ui.presenter.admin.importation.ImportationSchemeAdminPresenter.ImportationSchemePresenterHandler;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;

/**
 * View for {@link ImportationSchemeAdminPresenter}.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ImportationSchemeAdminView extends AbstractView implements ImportationSchemeAdminPresenter.View {

	// CSS style names.
	private static final String STYLE_TOOL_CLOSE_ICON = "x-tool-close";
	private static final String STYLE_LAYOUT = "x-border-layout-ct";
	
	private Grid<ImportationSchemeDTO> schemesGrid;
	private Grid<VariableDTO> variablesGrid;
	private ContentPanel schemePanel;
	private ContentPanel variablePanel;
	private IconButton closeButton;
	
	private Button addVariableButton;
	private Button deleteVariableButton;
	
	private Button addSchemeButton;
	private Button deleteSchemeButton;
	private Button editSchemeButton;
	
	private NumberField firstRow;
	private TextField<String> sheetName;
	private Button saveSheetNameFirstRowButton;
	private Label firstRowLabel;
	private Label sheetNameLabel;
	private ImportationSchemePresenterHandler importationShemePresenterHandler;

	@Override
	public void initialize() {

		schemePanel = Panels.content(I18N.CONSTANTS.adminImportationSchemes());
		schemePanel.setWidth(450);
		schemePanel.setScrollMode(Scroll.AUTOY);
		schemesGrid = buildSchemasGrid();
		schemePanel.add(schemesGrid);
		schemePanel.setTopComponent(importationSchemeToolBar());

		variablePanel = Panels.content(I18N.CONSTANTS.edit());
		variablePanel.setScrollMode(Scroll.AUTOY);
		variablesGrid = buildVariablesGrid();
		variablePanel.add(variablesGrid);
		variablePanel.setTopComponent(variableToolBar());
		
		closeButton = new ToolButton(STYLE_TOOL_CLOSE_ICON);
		variablePanel.getHeader().addTool(closeButton);

		final LayoutContainer details = Layouts.fit(false, STYLE_LAYOUT);
		details.add(variablePanel);

		final BorderLayoutData leftLayoutData = new BorderLayoutData(LayoutRegion.WEST, 450);
		leftLayoutData.setMargins(new Margins(0, 4, 0, 0));
		add(schemePanel, leftLayoutData);

		final BorderLayoutData mainLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
		mainLayoutData.setMargins(new Margins(0, 0, 0, 4));
		add(details, mainLayoutData);

	}

	/**
	 * Variable Importation Scheme Grid
	 * 
	 * @return Grid<VariableDTO>
	 */
	private Grid<VariableDTO> buildVariablesGrid() {

		// Reference column.
		final ColumnConfig referenceColumn = new ColumnConfig("reference", I18N.CONSTANTS.importVariableReference(), 75);

		// Name column.
		final ColumnConfig nameColumn = new ColumnConfig("name", I18N.CONSTANTS.importVariableName(), 300);
		nameColumn.setRenderer(new GridCellRenderer<VariableDTO>() {

			@Override
			public Object render(final VariableDTO variable, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				final Anchor anchor = new Anchor(variable.getName());
				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						importationShemePresenterHandler.onVariableImportationSchemeEdit(variable);
					}
				});
				
				final SimplePanel panel = new SimplePanel();
				panel.addStyleName("project-grid-code");
				panel.setWidget(anchor);

				return panel;
			}
		});
		
		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(referenceColumn);
		configs.add(nameColumn);

		ColumnModel cm = new ColumnModel(configs);

		final Grid<VariableDTO> variablesDTOGrid = new Grid<VariableDTO>(new ListStore<VariableDTO>(), cm);
		variablesDTOGrid.getView().setForceFit(true);
		variablesDTOGrid.hide();
		return variablesDTOGrid;

	}

	/**
	 * Build Importation Scheme Grid
	 * 
	 * @return Grid<ImportationSchemeDTO>
	 */
	private Grid<ImportationSchemeDTO> buildSchemasGrid() {

		final ColumnConfig nameColumn = new ColumnConfig("name", I18N.CONSTANTS.importSchemeName(), 75);

		nameColumn.setRenderer(new GridCellRenderer<ImportationSchemeDTO>() {

			@Override
			public Object render(final ImportationSchemeDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ImportationSchemeDTO> store, Grid<ImportationSchemeDTO> grid) {

				final Anchor anchor = new Anchor(model.getName());
				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						importationShemePresenterHandler.onImportationSchemeEdit(model);
					}
				});
				
				final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 2);
				panel.setCellPadding(0);
				panel.setCellSpacing(0);
				panel.setWidget(0, 0, ImportationSchemeDTO.getFileFormatIcon(model));
				panel.getCellFormatter().addStyleName(0, 0, "project-grid-code-icon");
				panel.setWidget(0, 1, anchor);
				panel.getCellFormatter().addStyleName(0, 1, "project-grid-code");

				return panel;
			}

		});

		final ColumnConfig typeColumn = new ColumnConfig("importType", I18N.CONSTANTS.adminImportSchemeFileImportType(), 50);
		typeColumn.setRenderer(new GridCellRenderer<ImportationSchemeDTO>() {

			@Override
			public Object render(final ImportationSchemeDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ImportationSchemeDTO> store, Grid<ImportationSchemeDTO> grid) {
				return ImportationSchemeImportType.getStringValue(model.getImportType());
			}

		});

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(nameColumn);
		configs.add(typeColumn);

		final ColumnModel columnModel = new ColumnModel(configs);
		final Grid<ImportationSchemeDTO> grid = new Grid<ImportationSchemeDTO>(new ListStore<ImportationSchemeDTO>(), columnModel);
		grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
		grid.getView().setForceFit(true);
		grid.setWidth(450);
		return grid;
	}

	/**
	 * Importation Scheme Tool Bar
	 * 
	 * @return ToolBar
	 */
	private ToolBar importationSchemeToolBar() {
		// Add scheme button.
		addSchemeButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());

		// Delete scheme button.
		deleteSchemeButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteSchemeButton.setEnabled(false);
		
		// Edit scheme button.
		editSchemeButton = new Button(I18N.CONSTANTS.edit(), IconImageBundle.ICONS.editPage());
		editSchemeButton.setEnabled(false);
		
		final ToolBar toolbar = new ToolBar();
		toolbar.add(addSchemeButton);
		toolbar.add(deleteSchemeButton);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(editSchemeButton);

		return toolbar;
	}

	/**
	 * Importation Scheme Variable Tool Bar
	 * 
	 * @return ToolBar
	 */

	private ToolBar variableToolBar() {

		ToolBar toolbar = new ToolBar();

		firstRowLabel = new Label(I18N.CONSTANTS.adminImportationSchemeFirstRow());
		toolbar.add(firstRowLabel);

		firstRow = new NumberField();
		firstRow.setAllowBlank(false);
		firstRow.setWidth(50);
		toolbar.add(firstRow);

		sheetNameLabel = new Label(I18N.CONSTANTS.adminImportationSchemeSheetName());
		toolbar.add(sheetNameLabel);

		sheetName = new TextField<String>();
		sheetName.setAllowBlank(false);
		sheetName.setWidth(100);

		toolbar.add(sheetName);

		saveSheetNameFirstRowButton = new Button(I18N.CONSTANTS.save());
		saveSheetNameFirstRowButton.setIcon(IconImageBundle.ICONS.save());

		saveSheetNameFirstRowButton.setWidth(80);

		sheetName.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				saveSheetNameFirstRowButton.enable();

			}
		});

		firstRow.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				saveSheetNameFirstRowButton.enable();
			}
		});

		saveSheetNameFirstRowButton.disable();
		toolbar.add(saveSheetNameFirstRowButton);

		addVariableButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());

		toolbar.add(addVariableButton);

		deleteVariableButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteVariableButton.setEnabled(false);
		toolbar.add(deleteVariableButton);

		return toolbar;
	}

	@Override
	public void confirmDeleteSchemesSelected(final ConfirmCallback confirmCallback) {

		if (getSchemesSelection().isEmpty()) {

			N10N.infoNotif(I18N.CONSTANTS.delete(), I18N.CONSTANTS.adminImportationSchemesDeleteNone());

		} else {

			String confirmMessage = "";

			for (ImportationSchemeDTO schemaToDelete : getSchemesSelection()) {
				confirmMessage += schemaToDelete.getName() + ", ";
			}
			if (!confirmMessage.isEmpty()) {
				confirmMessage = confirmMessage.substring(0, confirmMessage.lastIndexOf(", "));
			}

			confirmMessage = I18N.MESSAGES.confirmDeleteSchemes(confirmMessage);

			N10N.confirmation(I18N.CONSTANTS.delete(), confirmMessage, new ConfirmCallback() {

				@Override
				public void onAction() {
					// TODO
				}
			});
		}
	}

	@Override
	public void confirmDeleteVariablesSelected(final ConfirmCallback confirmCallback) {

		if (getVariablesSelection().isEmpty()) {

			N10N.infoNotif(I18N.CONSTANTS.delete(), I18N.CONSTANTS.adminVariablesDeleteNone());

		} else {

			String confirmMessage = "";

			for (VariableDTO variableToDelete : getVariablesSelection()) {
				confirmMessage += variableToDelete.getName() + ", ";
			}

			if (!confirmMessage.isEmpty()) {
				confirmMessage = confirmMessage.substring(0, confirmMessage.lastIndexOf(", "));
			}

			confirmMessage = I18N.MESSAGES.confirmDeleteVariables(confirmMessage);

			N10N.confirmation(I18N.CONSTANTS.delete(), confirmMessage, new ConfirmCallback() {

				@Override
				public void onAction() {
					// TODO
				}
			});
		}

	}

	/**
	 * @return the addVariableButton
	 */
	@Override
	public Button getAddVariableButton() {
		return addVariableButton;
	}

	/**
	 * @return the deleteVariableButton
	 */
	@Override
	public Button getDeleteVariableButton() {
		return deleteVariableButton;
	}

	/**
	 * @return the deleteSchemaButton
	 */
	@Override
	public Button getDeleteSchemeButton() {
		return deleteSchemeButton;
	}

	/**
	 * @return the addSchemaButton
	 */
	@Override
	public Button getAddSchemeButton() {
		return addSchemeButton;
	}

	@Override
	public Button getEditSchemeButton() {
		return editSchemeButton;
	}

	@Override
	public IconButton getVariablesCloseButton() {
		return closeButton;
	}
	
	/**
	 * @return the saveSheetNameFirstRowButton
	 */
	@Override
	public Button getSaveSheetNameFirstRowButton() {
		return saveSheetNameFirstRowButton;
	}

	@Override
	public List<VariableDTO> getVariablesSelection() {
		GridSelectionModel<VariableDTO> sm = variablesGrid.getSelectionModel();
		return sm.getSelectedItems();
	}

	@Override
	public List<ImportationSchemeDTO> getSchemesSelection() {
		GridSelectionModel<ImportationSchemeDTO> sm = schemesGrid.getSelectionModel();
		return sm.getSelectedItems();
	}

	@Override
	public LoadingMask getVariablesLoadingMonitor() {
		return new LoadingMask(variablesGrid, I18N.CONSTANTS.loading());
	}

	@Override
	public LoadingMask getSchemesLoadingMonitor() {
		return new LoadingMask(schemesGrid, I18N.CONSTANTS.loading());
	}

	@Override
	public ListStore<VariableDTO> getVariablesStore() {
		return variablesGrid.getStore();
	}

	@Override
	public ListStore<ImportationSchemeDTO> getSchemesStore() {
		return schemesGrid.getStore();
	}

	/**
	 * @return the schemesGrid
	 */
	@Override
	public Grid<ImportationSchemeDTO> getSchemesGrid() {
		return schemesGrid;
	}

	/**
	 * @return the variablesGrid
	 */
	@Override
	public Grid<VariableDTO> getVariablesGrid() {
		return variablesGrid;
	}

	@Override
	public boolean isFirstRowSheetNameValid(ImportationSchemeFileFormat format) {

		if (format.equals(ImportationSchemeFileFormat.CSV)) {
			return firstRow.isValid();
		} else {
			return sheetName.isValid() && firstRow.isValid();
		}

	}

	@Override
	public void setImportationSchemePresenterHandler(ImportationSchemePresenterHandler importationSchemePresenterHandler) {
		this.importationShemePresenterHandler = importationSchemePresenterHandler;
	}

	@Override
	public ContentPanel getSchemePanel() {
		return schemePanel;
	}

	@Override
	public ContentPanel getVariablePanel() {
		return variablePanel;
	}

	@Override
	public NumberField getFirstRow() {
		return firstRow;
	}

	@Override
	public TextField<String> getSheetName() {
		return sheetName;
	}

	@Override
	public Label getFirstRowLabel() {
		return firstRowLabel;
	}

	@Override
	public Label getSheetNameLabel() {
		return sheetNameLabel;
	}

}
