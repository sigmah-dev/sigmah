package org.sigmah.client.page.admin.importation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.importation.AdminImportPresenter.View;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.ui.ToggleAnchor;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.importation.ImportationSchemeFileFormat;
import org.sigmah.shared.domain.importation.ImportationSchemeImportType;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminImportView extends View {
	private Dispatcher dispatcher;
	
	private final Grid<ImportationSchemeDTO> schemesGrid;
	private final Grid<VariableDTO> variablesGrid;
	private final ContentPanel schemePanel;
	private final ContentPanel variablePanel;
	private ListStore<VariableDTO> variablesStore;
	private ListStore<ImportationSchemeDTO> schemesStore;
	private ImportationSchemeDTO currentSchemeDTO;
	private Button addVariableButton;
	private Button deleteVariableButton;
	private Button deleteSchemeButton;
	private Button addSchemeButton;
	private NumberField firstRow;
	private TextField<String> sheetName;
	private Button saveSheetNameFirstRowButton;
	private Label firstRowLabel;
	private Label sheetNameLabel;

	public AdminImportView(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
		
		setLayout(new BorderLayout());
		setHeaderVisible(false);
		setBorders(false);
		setBodyBorder(false);

		schemePanel = new ContentPanel(new FitLayout());
		schemePanel.setHeaderVisible(false);
		schemePanel.setWidth(450);
		schemePanel.setScrollMode(Scroll.AUTO);
		schemesGrid = buildSchemasGrid();
		schemePanel.add(schemesGrid);
		schemePanel.setTopComponent(importationSchemeToolBar());

		variablePanel = new ContentPanel(new FitLayout());
		variablePanel.setScrollMode(Scroll.AUTOY);
		variablePanel.setHeaderVisible(false);
		variablePanel.setBorders(true);
		variablesGrid = buildVariablesGrid();
		variablePanel.add(variablesGrid);
		variablePanel.setTopComponent(variableToolBar());
		

		final BorderLayoutData leftLayoutData = new BorderLayoutData(LayoutRegion.WEST, 450);
		leftLayoutData.setMargins(new Margins(0, 4, 0, 0));
		add(schemePanel, leftLayoutData);
		final BorderLayoutData mainLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
		mainLayoutData.setMargins(new Margins(0, 0, 0, 4));
		add(variablePanel, mainLayoutData);
	}

	private Grid<VariableDTO> buildVariablesGrid() {
		variablesStore = new ListStore<VariableDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();

		column = new ColumnConfig("reference", I18N.CONSTANTS.importVariableReference(), 75);
		configs.add(column);

		column = new ColumnConfig("name", I18N.CONSTANTS.importVariableName(), 300);
		configs.add(column);

		column = new ColumnConfig();
		column.setWidth(50);
		column.setAlignment(Style.HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<VariableDTO>() {

			@Override
			public Object render(final VariableDTO model, final String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<VariableDTO> store, Grid<VariableDTO> grid) {

				Button buttonEdit = new Button(I18N.CONSTANTS.edit());
				buttonEdit.setItemId(UIActions.edit);
				buttonEdit.addListener(Events.OnClick, new Listener<ButtonEvent>() {

					@Override
					public void handleEvent(ButtonEvent be) {
						AdminImportVariableActionListener actionListener = new AdminImportVariableActionListener(
						                AdminImportView.this, dispatcher, model, currentSchemeDTO);
						actionListener.onUIAction(UIActions.edit);
					}
				});
				return buttonEdit;
			}
		});
		configs.add(column);
		
		ColumnModel cm = new ColumnModel(configs);

		Grid<VariableDTO> variablesDTOGrid = new Grid<VariableDTO>(variablesStore, cm);
		variablesDTOGrid.setAutoHeight(true);
		variablesDTOGrid.setAutoWidth(false);
		variablesDTOGrid.getView().setForceFit(true);
		variablesDTOGrid.hide();
		return variablesDTOGrid;
	}

	private Grid<ImportationSchemeDTO> buildSchemasGrid() {
		schemesStore = new ListStore<ImportationSchemeDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();

		column = new ColumnConfig("name", I18N.CONSTANTS.importSchemeName(), 75);
		column.setRenderer(new GridCellRenderer<ImportationSchemeDTO>() {

			@Override
			public Object render(final ImportationSchemeDTO model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportationSchemeDTO> store, Grid<ImportationSchemeDTO> grid) {

				final ToggleAnchor anchor = new ToggleAnchor(model.getName());
				anchor.setAnchorMode(true);

				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						currentSchemeDTO = model;

						if (currentSchemeDTO.getFirstRow() != null) {
							firstRow.setValue(currentSchemeDTO.getFirstRow());
						} else {
							firstRow.clear();
						}
						if (currentSchemeDTO.getSheetName() != null) {
							sheetName.setValue(currentSchemeDTO.getSheetName());
						} else {
							sheetName.clear();
						}
						hideSheetNameFirstRow();

						switch (currentSchemeDTO.getImportType()) {
						case ROW:
							variablesGrid.getColumnModel().getColumnById("reference")
							                .setHeader(I18N.CONSTANTS.adminImportReferenceColumn());
							break;
						case SEVERAL:
							variablesGrid.getColumnModel().getColumnById("reference")
							                .setHeader(I18N.CONSTANTS.adminImportReferenceCell());
							break;
						case UNIQUE:
							variablesGrid.getColumnModel().getColumnById("reference")
							                .setHeader(I18N.CONSTANTS.adminImportReferenceSheetCell());
							break;
						default:
							break;

						}
						variablesGrid.show();
						variablesStore.removeAll();
						variablesStore.add(model.getVariablesDTO());
						variablesStore.commitChanges();
						addVariableButton.enable();
					}

				});

				final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 2);
				panel.setCellPadding(0);
				panel.setCellSpacing(5);
				panel.setWidget(0, 0, model.getFileFormatIcon());
				panel.setWidget(0, 1, anchor);

				return panel;
			}

		});

		configs.add(column);

		column = new ColumnConfig("importType", I18N.CONSTANTS.adminImportSchemeFileImportType(), 50);
		column.setRenderer(new GridCellRenderer<ImportationSchemeDTO>() {

			@Override
			public Object render(final ImportationSchemeDTO model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportationSchemeDTO> store, Grid<ImportationSchemeDTO> grid) {
				return ImportationSchemeImportType.getStringValue(model.getImportType());
			}

		});
		configs.add(column);

		column = new ColumnConfig("actions", 70);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<ImportationSchemeDTO>() {

			@Override
			public Object render(final ImportationSchemeDTO model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportationSchemeDTO> store, Grid<ImportationSchemeDTO> grid) {

				Button buttonEdit = new Button(I18N.CONSTANTS.edit());
				buttonEdit.addListener(Events.OnClick, new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {
						AdminImportSchemeActionListener actionListener = new AdminImportSchemeActionListener(
						                AdminImportView.this, dispatcher, model);
						actionListener.onUIAction(UIActions.edit);
					}
				});
				return buttonEdit;
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<ImportationSchemeDTO> grid = new Grid<ImportationSchemeDTO>(schemesStore, cm);
		grid.setAutoHeight(true);
		grid.getView().setForceFit(true);
		grid.setAutoWidth(false);
		grid.setWidth(450);
		return grid;
	}

	private ToolBar importationSchemeToolBar() {
		ToolBar toolbar = new ToolBar();

		addSchemeButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addSchemeButton.setItemId(UIActions.add);
		toolbar.add(addSchemeButton);

		deleteSchemeButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		toolbar.add(deleteSchemeButton);

		return toolbar;
	}

	private ToolBar variableToolBar() {

		ToolBar toolbar = new ToolBar();

		firstRowLabel = new Label();
		firstRowLabel.setText(I18N.CONSTANTS.adminImportationSchemeFirstRow());
		toolbar.add(firstRowLabel);

		firstRow = new NumberField();
		firstRow.setAllowBlank(false);
		firstRow.setWidth(50);
		toolbar.add(firstRow);

		sheetNameLabel = new Label();
		sheetNameLabel.setText(I18N.CONSTANTS.adminImportationSchemeSheetName());
		toolbar.add(sheetNameLabel);

		sheetName = new TextField<String>();
		sheetName.setAllowBlank(false);
		sheetName.setWidth(100);
	
		toolbar.add(sheetName);

		saveSheetNameFirstRowButton = new Button(I18N.CONSTANTS.save());
		saveSheetNameFirstRowButton.setIcon(IconImageBundle.ICONS.save());
		saveSheetNameFirstRowButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				Map<String, Object> newSchemaProperties = new HashMap<String, Object>();
				if (isFirstRowSheetNameValid()) {
					newSchemaProperties.put(AdminUtil.ADMIN_SCHEMA, currentSchemeDTO);
					newSchemaProperties.put(AdminUtil.PROP_SCH_FIRST_ROW, firstRow.getValue().intValue());

					newSchemaProperties.put(AdminUtil.PROP_SCH_SHEET_NAME, sheetName.getValue());
					CreateEntity cmd = new CreateEntity("ImportationScheme", newSchemaProperties);
					dispatcher.execute(cmd, null, new AsyncCallback<CreateResult>() {

						@Override
						public void onSuccess(CreateResult result) {
							Notification.show(I18N.CONSTANTS.infoConfirmation(),
							                I18N.CONSTANTS.adminImportationSchemeUpdateConfirm());
							ImportationSchemeDTO schemaUpdated = (ImportationSchemeDTO) result.getEntity();
							currentSchemeDTO.setFirstRow(schemaUpdated.getFirstRow());
							currentSchemeDTO.setSheetName(schemaUpdated.getSheetName());
							schemesStore.update(currentSchemeDTO);
							schemesStore.commitChanges();
						}

						@Override
						public void onFailure(Throwable caught) {

						}
					});
				}

			}
		});
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

		hideSheetNameFirstRow();
		addVariableButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addVariableButton.setItemId(UIActions.add);

		toolbar.add(addVariableButton);

		deleteVariableButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());

		toolbar.add(deleteVariableButton);
		return toolbar;
	}

	@Override
	public void confirmDeleteSchemesSelected(final ConfirmCallback confirmCallback) {
		if (getSchemesSelection().size() == 0) {
			MessageBox.alert(I18N.CONSTANTS.delete(), I18N.CONSTANTS.adminImportationSchemesDeleteNone(), null);
		} else {
			String confirmMessage = "";
			for (ImportationSchemeDTO schemaToDelete : getSchemesSelection()) {
				confirmMessage += schemaToDelete.getName() + ", ";
			}
			if (!confirmMessage.isEmpty()) {
				confirmMessage = confirmMessage.substring(0, confirmMessage.lastIndexOf(", "));
			}
			confirmMessage = I18N.MESSAGES.confirmDeleteSchemes(confirmMessage);
			MessageBox.confirm(I18N.CONSTANTS.delete(), confirmMessage, new Listener<MessageBoxEvent>() {

				@Override
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().equals("yes")) {
						confirmCallback.confirmed();
					}
				}
			});
		}
	}

	@Override
	public void confirmDeleteVariablesSelected(final ConfirmCallback confirmCallback) {
		if (getVariablesSelection().size() == 0) {
			MessageBox.alert(I18N.CONSTANTS.delete(), I18N.CONSTANTS.adminVariablesDeleteNone(), null);
		} else {
			String confirmMessage = "";
			for (VariableDTO variableToDelete : getVariablesSelection()) {
				confirmMessage += variableToDelete.getName() + ", ";
			}
			if (!confirmMessage.isEmpty()) {
				confirmMessage = confirmMessage.substring(0, confirmMessage.lastIndexOf(", "));
			}
			confirmMessage = I18N.MESSAGES.confirmDeleteVariables(confirmMessage);
			MessageBox.confirm(I18N.CONSTANTS.delete(), confirmMessage, new Listener<MessageBoxEvent>() {

				@Override
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().equals("yes")) {
						confirmCallback.confirmed();
					}
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
	public Component getMainPanel() {
		return this;
	}

	@Override
	public MaskingAsyncMonitor getVariablesLoadingMonitor() {
		return new MaskingAsyncMonitor(variablesGrid, I18N.CONSTANTS.loading());
	}

	@Override
	public MaskingAsyncMonitor getSchemesLoadingMonitor() {
		return new MaskingAsyncMonitor(schemesGrid, I18N.CONSTANTS.loading());
	}

	/**
	 * @return the currentSchema
	 */
	@Override
	public ImportationSchemeDTO getCurrentSchemeDTO() {
		return currentSchemeDTO;
	}
	

	/**
	 * @param currentSchemeDTO
	 *            the currentSchema to set
	 */
	@Override
	public void setCurrentSchemeDTO(ImportationSchemeDTO currentSchemeDTO) {
		this.currentSchemeDTO = currentSchemeDTO;
	}


	@Override
	public ListStore<VariableDTO> getVariablesStore() {
		return variablesStore;
	}

	@Override
	public ListStore<ImportationSchemeDTO> getSchemesStore() {
		return schemesStore;
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

	/**
	 * Hides the specific fields for the ROW import type
	 */
	public void hideSheetNameFirstRow() {
		if (currentSchemeDTO != null && ImportationSchemeImportType.ROW.equals(currentSchemeDTO.getImportType())) {
			saveSheetNameFirstRowButton.show();
			firstRow.show();
			firstRowLabel.show();
			if (!ImportationSchemeFileFormat.CSV.equals(currentSchemeDTO.getFileFormat())) {
				sheetNameLabel.show();
				sheetName.show();
			}
		} else {
			firstRow.hide();
			sheetName.hide();
			saveSheetNameFirstRowButton.hide();
			firstRowLabel.hide();
			sheetNameLabel.hide();
		}
	}

	public boolean isFirstRowSheetNameValid() {
		return sheetName.isValid() && firstRow.isValid();
	}
}
