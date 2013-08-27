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
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.DeleteImportationSchemeModels;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableBudgetElementDTO;
import org.sigmah.shared.dto.importation.VariableBudgetSubFieldDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

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
	private ImportationSchemeModelDTO currentImportationSchemeModel;

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
		addImportationSchemeModelButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				showNewImportationSchemeModelForm(true);
			}

		});
		toolbar.add(addImportationSchemeModelButton);

		deleteImportationSchemeModelButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteImportationSchemeModelButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				confirmDeleteSelected(new ConfirmCallback() {

					@Override
					public void confirmed() {
						deleteImportationSchemeModels();
					}
				});
			}
		});
		toolbar.add(deleteImportationSchemeModelButton);

		return toolbar;
	}

	private Component variableFlexibleElementToolBar() {
		ToolBar toolbar = new ToolBar();

		addVariableFlexibleElementButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addVariableFlexibleElementButton.setItemId(UIActions.add);
		addVariableFlexibleElementButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				showNewVariableFlexibleElementForm(false);
			}
		});
		addVariableFlexibleElementButton.disable();
		toolbar.add(addVariableFlexibleElementButton);

		deleteVariableFlexibleElementButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteVariableFlexibleElementButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				deleteVariableFlexibleElements();

			}
		});

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
						variableNames += varSubField.getVariableDTO().getName();
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
						currentImportationSchemeModel = model;
						variableFlexibleElementsGrid.show();
						variableFlexibleElementStore.removeAll();
						for (VariableFlexibleElementDTO variableFleDTO : model.getVariableFlexibleElementsDTO()) {
							variableFlexibleElementStore.add(variableFleDTO);
						}
						variableFlexibleElementStore.commitChanges();
						addVariableFlexibleElementButton.enable();
						if (currentImportationSchemeModel.getIdKey() == null) {
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
				                currentImportationSchemeModel = importationSchemeDTO;
				                getImportationSchemeModelsStore().update(currentImportationSchemeModel);
				                getImportationSchemeModelsStore().commitChanges();
				                importationSchemeModelsGrid.getSelectionModel().select(importationSchemeDTO, false);
				                getVariableFlexibleElementsStore().add(
				                                importationSchemeDTO.getVariableFlexibleElementsDTO());
				                getVariableFlexibleElementsStore().commitChanges();

			                }

			                @Override
			                public void onFailure(Throwable caught) {
				                window.hide();
			                }
		                }, currentImportationSchemeModel, forKey);
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
				                getVariableFlexibleElementsStore().removeAll();
				                getVariableFlexibleElementsStore().clearFilters();

				                final Window windowVarFle = new Window();
				                // TODO Add title
				                windowVarFle.setSize(400, 200);
				                windowVarFle.setPlain(true);
				                windowVarFle.setModal(true);
				                windowVarFle.setBlinkModal(true);
				                windowVarFle.setLayout(new FitLayout());
				                final VariableFlexibleElementForm variableFlexibleElementForm = new VariableFlexibleElementForm(
				                                dispatcher, getVariableFlexibleElementsLoadingMonitor(),
				                                new AsyncCallback<CreateResult>() {

					                                @Override
					                                public void onSuccess(CreateResult result) {
						                                windowVarFle.hide();
						                                getVariableFlexibleElementsStore().removeAll();
						                                getVariableFlexibleElementsStore().clearFilters();
						                                ImportationSchemeModelDTO importationSchemeDTO = (ImportationSchemeModelDTO) result
						                                                .getEntity();
						                                schemaDTOUpdated.setVariableFlexibleElementsDTO(importationSchemeDTO
						                                                .getVariableFlexibleElementsDTO());
						                                getImportationSchemeModelsStore().update(schemaDTOUpdated);
						                                getImportationSchemeModelsStore().commitChanges();
						                                importationSchemeModelsGrid.getSelectionModel().select(
						                                                schemaDTOUpdated, false);
						                                getVariableFlexibleElementsStore()
						                                                .add(importationSchemeDTO
						                                                                .getVariableFlexibleElementsDTO());
						                                getVariableFlexibleElementsStore().commitChanges();

					                                }

					                                @Override
					                                public void onFailure(Throwable caught) {
						                                windowVarFle.hide();
					                                }
				                                }, schemaDTOUpdated, forKey);
				                windowVarFle.add(variableFlexibleElementForm);
				                windowVarFle.show();

			                }

			                @Override
			                public void onFailure(Throwable caught) {
				                window.hide();
			                }
		                }, model);
		window.add(importationSchemeModelForm);
		window.show();

	}

	@Override
	public List<ImportationSchemeModelDTO> getImportationSchemeModelsSelection() {
		GridSelectionModel<ImportationSchemeModelDTO> sm = importationSchemeModelsGrid.getSelectionModel();
		return sm.getSelectedItems();
	}

	@Override
	public void confirmDeleteSelected(final ConfirmCallback confirmCallback) {
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
	public void enableToolBar() {
		// TODO Auto-generated method stub

	}

	@Override
	public ListStore<ImportationSchemeModelDTO> getImportationSchemeModelsStore() {
		return importationSchemeModelsStore;
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

	protected void deleteImportationSchemeModels() {
		List<Long> schemeModelIdsToDelete = new ArrayList<Long>();
		final List<ImportationSchemeModelDTO> importationSchemeSelection = getImportationSchemeModelsSelection();
		for (ImportationSchemeModelDTO importationSchemeModel : importationSchemeSelection) {
			schemeModelIdsToDelete.add(Long.valueOf(importationSchemeModel.getId()));
		}
		final DeleteImportationSchemeModels cmdDelete = new DeleteImportationSchemeModels();
		cmdDelete.setImportationSchemeIdsList(schemeModelIdsToDelete);
		dispatcher.execute(cmdDelete, getImportationSchemeModelsLoadingMonitor(), new AsyncCallback<VoidResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO
				// Auto-generated
				// method stub

			}

			@Override
			public void onSuccess(VoidResult result) {
				Notification.show(I18N.CONSTANTS.infoConfirmation(),
				                I18N.CONSTANTS.adminImportationSchemesDeleteConfirm());
				for (ImportationSchemeModelDTO importationSchemeModelDTO : importationSchemeSelection) {
					importationSchemeModelsStore.remove(importationSchemeModelDTO);
				}
			}
		});

	}

	protected void deleteVariableFlexibleElements() {
		List<Long> variableFlexibleElementIdsToDelete = new ArrayList<Long>();
		final List<VariableFlexibleElementDTO> variableFlexibleElementsSelection = getVariableFlexibleElementsSelection();
		for (VariableFlexibleElementDTO variableFlexibleElement : variableFlexibleElementsSelection) {
			variableFlexibleElementIdsToDelete.add(Long.valueOf(variableFlexibleElement.getId()));
		}
		final DeleteImportationSchemeModels cmdDelete = new DeleteImportationSchemeModels();
		cmdDelete.setImportationSchemeIdsList(variableFlexibleElementIdsToDelete);
		dispatcher.execute(cmdDelete, getImportationSchemeModelsLoadingMonitor(), new AsyncCallback<VoidResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO
				// Auto-generated
				// method stub

			}

			@Override
			public void onSuccess(VoidResult result) {
				Notification.show(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminVariableDeleteConfirm());
				for (VariableFlexibleElementDTO variableFlexibleElement : variableFlexibleElementsSelection) {
					variableFlexibleElementStore.remove(variableFlexibleElement);
				}
			}
		});

	}

}
