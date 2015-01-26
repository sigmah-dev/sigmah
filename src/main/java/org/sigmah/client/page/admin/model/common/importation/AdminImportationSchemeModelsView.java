package org.sigmah.client.page.admin.model.common.importation;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.model.common.element.ElementTypeEnum;
import org.sigmah.client.page.admin.model.common.importation.AdminImportationSchemeModelsPresenter.View;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.ui.ToggleAnchor;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ImportationSchemeListResult;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableBudgetElementDTO;
import org.sigmah.shared.dto.importation.VariableBudgetSubFieldDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * View for the importation schemas linked to a particular project model
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * 
 */
public class AdminImportationSchemeModelsView extends View {
	private final Dispatcher dispatcher;
	private ListStore<ImportationSchemeModelDTO> importationSchemeModelsStore;
	private ListStore<VariableFlexibleElementDTO> variableFlexibleElementStore;
	private Grid<ImportationSchemeModelDTO> importationSchemeModelsGrid;
	private Grid<VariableFlexibleElementDTO> variableFlexibleElementsGrid;
	private Button addVariableFlexibleElementButton;
	private Button deleteVariableFlexibleElementButton;
	private Button addImportationSchemeModelButton;
	private Button deleteImportationSchemeModelButton;
	private ImportationSchemeModelDTO currentImportationSchemeModelDTO;

	public AdminImportationSchemeModelsView(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
		this.setLayout(new FitLayout());
		setHeaderVisible(false);
		setBorders(false);
		setBodyBorder(false);

		HorizontalPanel panel = new HorizontalPanel();

		final VBoxLayoutData topVBoxLayoutData2 = new VBoxLayoutData();
		topVBoxLayoutData2.setMargins(new Margins(0, 0, 0, 2));
		topVBoxLayoutData2.setFlex(1.0);

		ContentPanel importationSchemeModelsPanel = new ContentPanel(new FitLayout());
		importationSchemeModelsPanel.setHeaderVisible(false);
		importationSchemeModelsPanel.setScrollMode(Scroll.AUTOY);
		importationSchemeModelsPanel.setBorders(false);

		importationSchemeModelsGrid = buildImportationSchemeModelsGrid();
		
		importationSchemeModelsPanel.add(importationSchemeModelsGrid, topVBoxLayoutData2);
		importationSchemeModelsPanel.setTopComponent(importationSchemeModelToolBar());
		importationSchemeModelsPanel.layout();

		panel.add(importationSchemeModelsPanel);

		ContentPanel variableFlexibleElementPanel = new ContentPanel(new FitLayout());
		variableFlexibleElementPanel.setScrollMode(Scroll.AUTOY);
		variableFlexibleElementPanel.setHeaderVisible(false);
		variableFlexibleElementPanel.setBorders(false);
		variableFlexibleElementsGrid = buildVariableFlexibleElementsGrid();

		final VBoxLayoutData topVBoxLayoutData = new VBoxLayoutData();
		topVBoxLayoutData.setMargins(new Margins(0, 0, 0, 2));
		topVBoxLayoutData.setFlex(1.0);

		variableFlexibleElementPanel.add(variableFlexibleElementsGrid, topVBoxLayoutData);
		variableFlexibleElementPanel.setTopComponent(variableFlexibleElementToolBar());
		variableFlexibleElementPanel.layout();

		panel.setWidth("600px");

		panel.add(variableFlexibleElementPanel);

		add(panel);

		layout();
	}

	private Component importationSchemeModelToolBar() {
		ToolBar toolbar = new ToolBar();

		addImportationSchemeModelButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addImportationSchemeModelButton.setItemId(UIActions.add);

		toolbar.add(addImportationSchemeModelButton);

		deleteImportationSchemeModelButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());

		toolbar.add(deleteImportationSchemeModelButton);

		return toolbar;
	}

	private Component variableFlexibleElementToolBar() {
		ToolBar toolbar = new ToolBar();

		addVariableFlexibleElementButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addVariableFlexibleElementButton.setItemId(UIActions.add);

		addVariableFlexibleElementButton.disable();
		toolbar.add(addVariableFlexibleElementButton);

		deleteVariableFlexibleElementButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());

		toolbar.add(deleteVariableFlexibleElementButton);
		return toolbar;
	}

	private Grid<VariableFlexibleElementDTO> buildVariableFlexibleElementsGrid() {
		variableFlexibleElementStore = new ListStore<VariableFlexibleElementDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();

		column = new ColumnConfig("icon", "", 50);
		column.setRenderer(new GridCellRenderer<VariableFlexibleElementDTO>() {
			@Override
			public Object render(VariableFlexibleElementDTO model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<VariableFlexibleElementDTO> store,
			                Grid<VariableFlexibleElementDTO> grid) {
				if (model.getIsKey()) {
					return IconImageBundle.ICONS.login().createImage();
				}
				return null;

			}
		});
		configs.add(column);

		column = new ColumnConfig("field", I18N.CONSTANTS.adminFlexible(), 200);
		column.setRenderer(new GridCellRenderer<VariableFlexibleElementDTO>() {
			@Override
			public Object render(VariableFlexibleElementDTO model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<VariableFlexibleElementDTO> store,
			                Grid<VariableFlexibleElementDTO> grid) {
				if (model.getFlexibleElementDTO().getElementType() == ElementTypeEnum.DEFAULT) {
					DefaultFlexibleElementDTO defaultElementDTO = (DefaultFlexibleElementDTO) model
					                .getFlexibleElementDTO();
					return DefaultFlexibleElementType.getName(defaultElementDTO.getType());
				} else {
					return model.getFlexibleElementDTO().getLabel();
				}
			}
		});
		configs.add(column);

		column = new ColumnConfig("variable", I18N.CONSTANTS.adminImportationSchemeModelVariableHeading(), 200);
		column.setRenderer(new GridCellRenderer<VariableFlexibleElementDTO>() {
			@Override
			public Object render(VariableFlexibleElementDTO model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<VariableFlexibleElementDTO> store,
			                Grid<VariableFlexibleElementDTO> grid) {
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
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<VariableFlexibleElementDTO> variableFlexibleElementsDTOGrid = new Grid<VariableFlexibleElementDTO>(
		                variableFlexibleElementStore, cm);
		variableFlexibleElementsDTOGrid.setAutoHeight(true);
		variableFlexibleElementsDTOGrid.setAutoWidth(true);
		variableFlexibleElementsDTOGrid.addStyleName("importation-scheme-models-grid");
		variableFlexibleElementsDTOGrid.getView().setForceFit(true);

		return variableFlexibleElementsDTOGrid;
	}

	private Grid<ImportationSchemeModelDTO> buildImportationSchemeModelsGrid() {
		importationSchemeModelsStore = new ListStore<ImportationSchemeModelDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();

		column = new ColumnConfig("importationScheme", I18N.CONSTANTS.adminImportationScheme(), 250);
		column.setRenderer(new GridCellRenderer<ImportationSchemeModelDTO>() {

			@Override
			public Object render(final ImportationSchemeModelDTO model, String property, ColumnData config,
			                int rowIndex, int colIndex, ListStore<ImportationSchemeModelDTO> store,
			                Grid<ImportationSchemeModelDTO> grid) {

				final ToggleAnchor anchor = new ToggleAnchor(model.getImportationSchemeDTO().getName());
				anchor.setAnchorMode(true);

				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						currentImportationSchemeModelDTO = model;
						variableFlexibleElementsGrid.show();
						variableFlexibleElementStore.removeAll();
						variableFlexibleElementStore.add(model.getVariableFlexibleElementsDTO());
						variableFlexibleElementStore.commitChanges();
						addVariableFlexibleElementButton.enable();
						if (currentImportationSchemeModelDTO.getIdKey() == null) {
							showNewVariableFlexibleElementForm(true);
						}
					}

				});
				return anchor;
			}

		});

		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<ImportationSchemeModelDTO> importationSchemeModelsDTOGrid = new Grid<ImportationSchemeModelDTO>(
		                importationSchemeModelsStore, cm);
		importationSchemeModelsDTOGrid.getView().setForceFit(true);
		importationSchemeModelsDTOGrid.setAutoHeight(true);
		importationSchemeModelsDTOGrid.addStyleName("importation-scheme-models-grid");
		return importationSchemeModelsDTOGrid;
	}

	@Override
	public Component getMainPanel() {
		return this;
	}

	@Override
	public void showNewVariableFlexibleElementForm(Boolean forKey) {
		final Window window = new Window();
		window.setHeading(I18N.CONSTANTS.adminAddKeyVariableFlexibleElementHeading());
		window.setSize(400, 200);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());
		final VariableFlexibleElementForm variableFlexibleElementForm = new VariableFlexibleElementForm(dispatcher,
		                getVariableFlexibleElementsLoadingMonitor(), new AsyncCallback<CreateResult>() {

			                @Override
			                public void onSuccess(CreateResult result) {
				                window.hide();
				                getVariableFlexibleElementsStore().removeAll();
				                getVariableFlexibleElementsStore().clearFilters();
				                ImportationSchemeModelDTO importationSchemeDTO = (ImportationSchemeModelDTO) result
				                                .getEntity();
				                currentImportationSchemeModelDTO.setVariableFlexibleElementsDTO(importationSchemeDTO.getVariableFlexibleElementsDTO());
				                getImportationSchemeModelsStore().update(currentImportationSchemeModelDTO);
				                getImportationSchemeModelsStore().commitChanges();
				                importationSchemeModelsGrid.getSelectionModel().select(currentImportationSchemeModelDTO, false);
				                getVariableFlexibleElementsStore().add(
				                				currentImportationSchemeModelDTO.getVariableFlexibleElementsDTO());
				                getVariableFlexibleElementsStore().commitChanges();

			                }

			                @Override
			                public void onFailure(Throwable caught) {
				                window.hide();
			                }
		                }, currentImportationSchemeModelDTO, forKey);
		window.add(variableFlexibleElementForm);
		window.show();
	}

	@Override
	public void showNewImportationSchemeModelForm(final Boolean forKey) {
		final Window window = new Window();
		window.setHeading(I18N.CONSTANTS.adminAddImportationSchemeModel());
		window.setSize(400, 150);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());
		EntityDTO model;
		if (projectModel != null) {
			model = projectModel;
		} else {
			model = orgUnitModel;
		}
		final ImportationSchemeModelForm importationSchemeModelForm = new ImportationSchemeModelForm(dispatcher,
		                getImportationSchemeModelsLoadingMonitor(), new AsyncCallback<CreateResult>() {

			                @Override
			                public void onSuccess(CreateResult result) {
				                window.hide();
				                final ImportationSchemeModelDTO schemaDTOUpdated = (ImportationSchemeModelDTO) result
				                                .getEntity();
				                getImportationSchemeModelsStore().add(schemaDTOUpdated);
				                getImportationSchemeModelsStore().commitChanges();
				                currentImportationSchemeModelDTO = schemaDTOUpdated;
				                getVariableFlexibleElementsStore().removeAll();
				                getVariableFlexibleElementsStore().clearFilters();
				                showNewVariableFlexibleElementForm(forKey);

			                }

			                @Override
			                public void onFailure(Throwable caught) {
				                window.hide();
			                }
		                }, model);
		
		GetImportationSchemes cmd = new GetImportationSchemes();
		if (model instanceof OrgUnitDTO) {
			cmd.setOrgUnitModelId(Long.valueOf(model.getId()));
		} else {
			cmd.setProjectModelId(Long.valueOf(model.getId()));
		}
		cmd.setExcludeExistent(true);
		MaskingAsyncMonitor formMaskingMonitor = new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading());
		dispatcher.execute(cmd, formMaskingMonitor, new AsyncCallback<ImportationSchemeListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ImportationSchemeListResult result) {
				if (result.getList() != null && !result.getList().isEmpty()) {
					for (ImportationSchemeDTO importationScheme : result.getList()) {
						if (importationScheme.getVariablesDTO().size() > 0) {
							importationSchemeModelForm.getSchemasCombo().getStore().add(importationScheme);
						}
					}
					if (importationSchemeModelForm.getSchemasCombo().getStore().getModels().size() != 0) {
						window.add(importationSchemeModelForm);
						window.show();
					} else {
						MessageBox.alert(I18N.CONSTANTS.adminAddImportationSchemeModel(), "No importation schemes available.", null);
					}
				}
			}
		});
		

	}

	@Override
	public List<ImportationSchemeModelDTO> getImportationSchemeModelsSelection() {
		GridSelectionModel<ImportationSchemeModelDTO> sm = importationSchemeModelsGrid.getSelectionModel();
		return sm.getSelectedItems();
	}

	@Override
	public void confirmDeleteSchemeModelsSelected(final ConfirmCallback confirmCallback) {
		if (getImportationSchemeModelsSelection().size() == 0) {
			MessageBox.alert(I18N.CONSTANTS.delete(), I18N.CONSTANTS.adminVariablesDeleteNone(), null);
		} else {
			String confirmMessage = "";
			for (ImportationSchemeModelDTO importationSchemeModelToDelete : getImportationSchemeModelsSelection()) {
				confirmMessage += importationSchemeModelToDelete.getImportationSchemeDTO().getName() + ", ";
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

	@Override
	public void confirmDeleteVariableFlexibleElementsSelected(final ConfirmCallback confirmCallback) {
		if (getVariableFlexibleElementsSelection().size() == 0) {
			MessageBox.alert(I18N.CONSTANTS.delete(), I18N.CONSTANTS.adminVariablesDeleteNone(), null);
		} else {
			String confirmMessage = "";
			for (VariableFlexibleElementDTO variableFlexibleElementsToDelete : getVariableFlexibleElementsSelection()) {
				String elementLabel = "";
				if (ElementTypeEnum.DEFAULT.equals(variableFlexibleElementsToDelete.getFlexibleElementDTO()
				                .getElementType())) {
					elementLabel = DefaultFlexibleElementType
					                .getName(((DefaultFlexibleElementDTO) variableFlexibleElementsToDelete
					                                .getFlexibleElementDTO()).getType());
				} else {
					elementLabel = variableFlexibleElementsToDelete.getFlexibleElementDTO().getElementLabel();
				}
				confirmMessage += elementLabel + ", ";
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

	@Override
	public void enableToolBar() {
		// TODO Auto-generated method stub

	}

	@Override
	public ListStore<ImportationSchemeModelDTO> getImportationSchemeModelsStore() {
		return importationSchemeModelsStore;
	}

	/**
	 * @return the addVariableFlexibleElementButton
	 */
	@Override
	public Button getAddVariableFlexibleElementButton() {
		return addVariableFlexibleElementButton;
	}

	/**
	 * @return the deleteVariableFlexibleElementButton
	 */
	@Override
	public Button getDeleteVariableFlexibleElementButton() {
		return deleteVariableFlexibleElementButton;
	}

	/**
	 * @return the addImportationSchemeModelButton
	 */
	@Override
	public Button getAddImportationSchemeModelButton() {
		return addImportationSchemeModelButton;
	}

	/**
	 * @return the deleteImportationSchemeModelButton
	 */
	@Override
	public Button getDeleteImportationSchemeModelButton() {
		return deleteImportationSchemeModelButton;
	}

	/**
	 * @param deleteImportationSchemeModelButton
	 *            the deleteImportationSchemeModelButton to set
	 */
	public void setDeleteImportationSchemeModelButton(Button deleteImportationSchemeModelButton) {
		this.deleteImportationSchemeModelButton = deleteImportationSchemeModelButton;
	}

	@Override
	public ListStore<VariableFlexibleElementDTO> getVariableFlexibleElementsStore() {
		return variableFlexibleElementStore;
	}

	@Override
	public List<VariableFlexibleElementDTO> getVariableFlexibleElementsSelection() {
		GridSelectionModel<VariableFlexibleElementDTO> varFleGrid = variableFlexibleElementsGrid.getSelectionModel();
		return varFleGrid.getSelectedItems();
	}

	@Override
	public MaskingAsyncMonitor getVariableFlexibleElementsLoadingMonitor() {
		return new MaskingAsyncMonitor(variableFlexibleElementsGrid, I18N.CONSTANTS.loading());
	}

	@Override
	public MaskingAsyncMonitor getImportationSchemeModelsLoadingMonitor() {
		return new MaskingAsyncMonitor(importationSchemeModelsGrid, I18N.CONSTANTS.loading());
	}

	/**
	 * @return the currentImportationSchemeModel
	 */
	@Override
	public ImportationSchemeModelDTO getCurrentImportationSchemeModelDTO() {
		return currentImportationSchemeModelDTO;
	}

	/**
	 * @param currentImportationSchemeModel the currentImportationSchemeModel to set
	 */
	@Override
	public void setCurrentImportationSchemeModelDTO(ImportationSchemeModelDTO currentImportationSchemeModel) {
		this.currentImportationSchemeModelDTO = currentImportationSchemeModel;
	}

}
