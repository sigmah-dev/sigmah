package org.sigmah.client.ui.view.admin.importation;

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
import org.sigmah.client.ui.widget.ToggleAnchor;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

@Singleton
public class ImportationSchemeAdminView extends AbstractView implements ImportationSchemeAdminPresenter.View {

	private Grid<ImportationSchemeDTO> schemesGrid;
	private Grid<VariableDTO> variablesGrid;
	private ContentPanel schemePanel;
	private ContentPanel variablePanel;
	private ListStore<VariableDTO> variablesStore;
	private ListStore<ImportationSchemeDTO> schemesStore;
	private Button addVariableButton;
	private Button deleteVariableButton;
	private Button deleteSchemeButton;
	private Button addSchemeButton;
	private NumberField firstRow;
	private TextField<String> sheetName;
	private Button saveSheetNameFirstRowButton;
	private Label firstRowLabel;
	private Label sheetNameLabel;
	private ImportationSchemePresenterHandler importationShemePresenterHandler;

	@Override
	public void initialize() {

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

	/**
	 * Variable Importation Scheme Grid
	 * 
	 * @return Grid<VariableDTO>
	 */
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
			public Object render(final VariableDTO model, final String property, ColumnData config, int rowIndex, int colIndex, ListStore<VariableDTO> store,
					Grid<VariableDTO> grid) {

				final Button buttonEdit = new Button(I18N.CONSTANTS.edit());
				buttonEdit.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						importationShemePresenterHandler.onVariableImportationSchemeEdit(model);
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

	/**
	 * Build Importation Scheme Grid
	 * 
	 * @return Grid<ImportationSchemeDTO>
	 */
	private Grid<ImportationSchemeDTO> buildSchemasGrid() {

		schemesStore = new ListStore<ImportationSchemeDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();

		column = new ColumnConfig("name", I18N.CONSTANTS.importSchemeName(), 75);

		column.setRenderer(new GridCellRenderer<ImportationSchemeDTO>() {

			@Override
			public Object render(final ImportationSchemeDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ImportationSchemeDTO> store, Grid<ImportationSchemeDTO> grid) {

				final ToggleAnchor anchor = new ToggleAnchor(model.getName());
				anchor.setAnchorMode(false);
				final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 2);
				panel.setCellPadding(0);
				panel.setCellSpacing(5);
				panel.setWidget(0, 0, ImportationSchemeDTO.getFileFormatIcon(model));
				panel.setWidget(0, 1, anchor);

				return panel;
			}

		});

		configs.add(column);

		column = new ColumnConfig("importType", I18N.CONSTANTS.adminImportSchemeFileImportType(), 50);
		column.setRenderer(new GridCellRenderer<ImportationSchemeDTO>() {

			@Override
			public Object render(final ImportationSchemeDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ImportationSchemeDTO> store, Grid<ImportationSchemeDTO> grid) {
				return ImportationSchemeImportType.getStringValue(model.getImportType());
			}

		});
		configs.add(column);

		column = new ColumnConfig("actions", 70);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<ImportationSchemeDTO>() {

			@Override
			public Object render(final ImportationSchemeDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ImportationSchemeDTO> store, Grid<ImportationSchemeDTO> grid) {

				final Button buttonEdit = new Button(I18N.CONSTANTS.edit());

				buttonEdit.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						importationShemePresenterHandler.onImportationSchemeEdit(model);
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

	/**
	 * Importation Scheme Tool Bar
	 * 
	 * @return ToolBar
	 */
	private ToolBar importationSchemeToolBar() {

		ToolBar toolbar = new ToolBar();
		addSchemeButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());

		toolbar.add(addSchemeButton);
		deleteSchemeButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		toolbar.add(deleteSchemeButton);

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
		toolbar.add(deleteVariableButton);

		return toolbar;
	}

	@Override
	public void confirmDeleteSchemesSelected(final ConfirmCallback confirmCallback) {

		if (getSchemesSelection().size() == 0) {

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

		if (getVariablesSelection().size() == 0) {

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
