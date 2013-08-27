package org.sigmah.client.page.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.dashboard.CreateProjectWindow.CreateProjectListener;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.AmendmentAction;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ValueResultUtils;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.Amendment.Action;
import org.sigmah.shared.domain.ElementExtractedValue;
import org.sigmah.shared.domain.ImportDetails;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.ImportUtils.ImportStatusCode;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.handler.ValueEvent;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ImportDetailsGrid extends ContentPanel {
	private Dispatcher dispatcher;
	private Grid<ImportDetails> grid;
	private Button importButton;
	private ListStore<ImportDetails> entitiesStore;
	private Authentication authentication;
	private UserLocalCache cache;

	public ImportDetailsGrid(final Dispatcher dispatcher, Authentication authentication, UserLocalCache cache,
	                List<ImportDetails> entitiesExtracted) {
		this.dispatcher = dispatcher;
		this.authentication = authentication;
		this.cache = cache;
		grid = buildImportGrid();
		grid.setAutoHeight(true);
		grid.getView().setForceFit(true);

		entitiesStore.add(entitiesExtracted);
		entitiesStore.commitChanges();

		add(grid);

		setHeading(I18N.CONSTANTS.importProjectOrgUnitsPanelHeader());
		setHeaderVisible(true);
		setBottomComponent(buildBottomComponent());

		layout();

	}

	public Grid<ImportDetails> buildImportGrid() {
		entitiesStore = new ListStore<ImportDetails>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();

		CheckBoxSelectionModel<ImportDetails> checkBoxColumn = new CheckBoxSelectionModel<ImportDetails>();

		configs.add(checkBoxColumn.getColumn());

		column = new ColumnConfig("modelName", I18N.CONSTANTS.projectModel(), 100);
		column.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {

				return model.getModelName();
			}

		});
		configs.add(column);

		column = new ColumnConfig("idKey", I18N.CONSTANTS.adminImportKeyIdentification(), 70);
		column.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {

				return model.getKeyIdentification();
			}

		});
		configs.add(column);

		column = new ColumnConfig("status", I18N.CONSTANTS.importHeadingStatus(), 70);
		column.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {
				String statusMessage = I18N.CONSTANTS.UNAVAILABLE();
				if (model.getEntityStatus() != null) {
					statusMessage = ImportStatusCode.getStringValue(model.getEntityStatus());
				}

				return statusMessage;
			}

		});
		configs.add(column);

		column = new ColumnConfig("code", I18N.CONSTANTS.code(), 70);
		column.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {
				if (model.getEntitiesToImport().keySet().size() != 0) {
					Iterator<EntityDTO> it = model.getEntitiesToImport().keySet().iterator();
					EntityDTO entityFound = (EntityDTO) it.next();
					if (entityFound instanceof ProjectDTO) {
						ProjectDTO projectFound = (ProjectDTO) entityFound;
						return projectFound.getName();
					} else {
						OrgUnitDTOLight orgUnitFound = (OrgUnitDTOLight) entityFound;
						return orgUnitFound.getName();
					}
				} else {
					return null;
				}
			}

		});
		configs.add(column);

		column = new ColumnConfig("name", I18N.CONSTANTS.name(), 120);
		column.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {
				if (model.getEntitiesToImport().keySet().size() != 0) {
					Iterator<EntityDTO> it = model.getEntitiesToImport().keySet().iterator();
					EntityDTO entityFound = (EntityDTO) it.next();
					if (entityFound instanceof ProjectDTO) {
						ProjectDTO projectFound = (ProjectDTO) entityFound;
						return projectFound.getFullName();
					} else {
						OrgUnitDTOLight orgUnitFound = (OrgUnitDTOLight) entityFound;
						return orgUnitFound.getFullName();
					}
				} else {
					return null;
				}
			}

		});
		configs.add(column);

		column = new ColumnConfig("actions", 200);
		column.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, final Grid<ImportDetails> grid) {
				final Map<EntityDTO, List<ElementExtractedValue>> correspondancesFleValue = model.getEntitiesToImport();
				if (ImportStatusCode.PROJECT_NOT_FOUND_CODE.equals(model.getEntityStatus())) {
					Button createButton = new Button("CREATE");
					createButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							Iterator<EntityDTO> it = model.getEntitiesToImport().keySet().iterator();
							final EntityDTO notFoundEntity = (EntityDTO) it.next();
							List<ElementExtractedValue> correspondancesNotFound = correspondancesFleValue
							                .get(notFoundEntity);
							CreateProjectWindow createProjectWindow = new CreateProjectWindow(dispatcher,
							                authentication, cache);
							createProjectWindow.addListener(new CreateProjectListener() {

								@Override
								public void projectCreated(ProjectDTOLight project) {
									entitiesStore.remove(model);
									entitiesStore.commitChanges();
									// Show notification.
									Notification.show(I18N.CONSTANTS.createProjectSucceeded(),
									                I18N.CONSTANTS.createProjectSucceededDetails());
								}

								@Override
								public void projectCreatedAsFunded(ProjectDTOLight project, double percentage) {
									// nothing to do (must not be
									// called).
								}

								@Override
								public void projectCreatedAsFunding(ProjectDTOLight project, double percentage) {
									// nothing to do (must not be
									// called).
								}

								@Override
								public void projectCreatedAsTest(ProjectDTOLight project) {
									entitiesStore.remove(model);
									entitiesStore.commitChanges();
									// Show notification.
									Notification.show(I18N.CONSTANTS.createProjectSucceeded(),
									                I18N.CONSTANTS.createProjectSucceededDetails());
								}

								@Override
								public void projectDeletedAsTest(ProjectDTOLight project) {
									// nothing to do (must not be
									// called).
								}
							});
							if (model.getModelStatus() == ProjectModelStatus.DRAFT) {
								createProjectWindow.showProjectTest();
							} else if (!ProjectModelStatus.UNAVAILABLE.equals(model.getModelStatus())) {
								createProjectWindow.show();
							}
							for (ElementExtractedValue elementExtractedValue : correspondancesNotFound) {
								if (elementExtractedValue.getElement() instanceof DefaultFlexibleElementDTO) {
									DefaultFlexibleElementDTO defaultElementDTO = (DefaultFlexibleElementDTO) elementExtractedValue
									                .getElement();
									if (DefaultFlexibleElementType.CODE.equals(defaultElementDTO.getType())) {
										createProjectWindow.getNameField().setValue(
										                elementExtractedValue.getNewValue().toString());
									} else if (DefaultFlexibleElementType.TITLE.equals(defaultElementDTO.getType())) {
										createProjectWindow.getFullNameField().setValue(
										                elementExtractedValue.getNewValue().toString());
									}
								}
							}
						}
					});
					return createButton;
				} else if (ImportStatusCode.PROJECT_FOUND_CODE.equals(model.getEntityStatus())
				                || ImportStatusCode.ORGUNIT_FOUND_CODE.equals(model.getEntityStatus())) {
					grid.getSelectionModel().select(model, true);
					Button confirmButton = new Button(I18N.CONSTANTS.importButtonConfirmDetails());
					confirmButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							if (model.getEntitiesToImport().keySet().size() != 0) {
								Iterator<EntityDTO> it = model.getEntitiesToImport().keySet().iterator();
								final EntityDTO foundEntity = (EntityDTO) it.next();
								final Window window = new Window();
								window.setWidth(900);
								final ElementExtractedValueGrid imw = new ElementExtractedValueGrid(dispatcher,
								                correspondancesFleValue.get(foundEntity), foundEntity);

								imw.getConfirmButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

									@Override
									public void handleEvent(BaseEvent be) {

										window.hide();

										// FIXME
										// UpdateElements
									}
								});
								window.add(imw);
								window.setAutoHeight(true);

								window.setPlain(true);
								window.setModal(true);
								window.setBlinkModal(true);
								window.setLayout(new FitLayout());
								window.show();

							}
						}
					});

					return confirmButton;
				} else if (ImportStatusCode.PROJECT_LOCKED_CODE.equals(model.getEntityStatus())) {
					Button unlockButton = new Button(I18N.CONSTANTS.importButtonUnlock());
					unlockButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							Iterator<EntityDTO> it = model.getEntitiesToImport().keySet().iterator();
							EntityDTO entityLocked = (EntityDTO) it.next();
							final AmendmentAction amendmentAction = new AmendmentAction(entityLocked.getId(),
							                Action.UNLOCK);
							dispatcher.execute(amendmentAction, null, new AsyncCallback<ProjectDTO>() {

								@Override
								public void onFailure(Throwable caught) {
									// Failures may happen if an other
									// user changes
									// the
									// amendment state.
									// TODO: we should maybe refresh the
									// project or
									// tell
									// the user to refresh the page.
									MessageBox.alert(I18N.CONSTANTS.amendmentUnlock(),
									                I18N.CONSTANTS.amendmentActionError(), null);
								}

								@Override
								public void onSuccess(ProjectDTO result) {
									model.setEntityStatus(ImportStatusCode.PROJECT_FOUND_CODE);
									entitiesStore.update(model);
									entitiesStore.commitChanges();
								}
							});

						}

					});
					return unlockButton;
				} else {
					Button chooseButton = new Button("CHOOSE");

					final ComboBox<EntityDTO> projectsCombo = new ComboBox<EntityDTO>();
					final ListStore<EntityDTO> projectStore = new ListStore<EntityDTO>();
					for (EntityDTO entity : correspondancesFleValue.keySet()) {
						projectStore.add(entity);
					}
					projectsCombo.setDisplayField("name");
					projectsCombo.setStore(projectStore);
					projectsCombo.setEditable(true);
					projectsCombo.setEmptyText(I18N.CONSTANTS.formWindowListEmptyText());
					projectsCombo.setAllowBlank(false);
					projectsCombo.setWidth(150);
					HorizontalPanel panel = new HorizontalPanel();
					panel.setSpacing(5);
					panel.add(projectsCombo);
					panel.add(chooseButton);

					chooseButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							final EntityDTO chosenEntity = projectsCombo.getValue();
							if (chosenEntity != null) {
								List<ElementExtractedValue> correspondancesForChosen = model.getEntitiesToImport().get(
								                chosenEntity);
								Map<EntityDTO, List<ElementExtractedValue>> mapOfChosen = new HashMap<EntityDTO, List<ElementExtractedValue>>();
								mapOfChosen.put(chosenEntity, correspondancesForChosen);
								model.setEntitiesToImport(mapOfChosen);
								if (chosenEntity instanceof OrgUnitDTOLight) {
									model.setEntityStatus(ImportStatusCode.ORGUNIT_FOUND_CODE);
								} else {
									model.setEntityStatus(ImportStatusCode.PROJECT_FOUND_CODE);

								}
								grid.getSelectionModel().select(model, true);
								entitiesStore.update(model);
								entitiesStore.commitChanges();
							}

						}
					});
					return panel;
				}
			}
		});

		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);
		Grid<ImportDetails> entitiesToImportGrid = new Grid<ImportDetails>(entitiesStore, cm);
		entitiesToImportGrid.setSelectionModel(checkBoxColumn);
		entitiesToImportGrid.setBorders(true);
		entitiesToImportGrid.setBorders(true);
		entitiesToImportGrid.setAutoHeight(true);
		entitiesToImportGrid.setAutoWidth(false);
		entitiesToImportGrid.getView().setForceFit(true);
		return entitiesToImportGrid;

	}

	public ToolBar buildBottomComponent() {

		importButton = new Button(I18N.CONSTANTS.importItem());
		importButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				List<ImportDetails> selectedImportDetails = grid.getSelectionModel().getSelectedItems();
				if (selectedImportDetails.size() > 0) {
					for (ImportDetails importDetails : selectedImportDetails) {
						if (importDetails.getEntitiesToImport().keySet().size() != 0) {
							Iterator<EntityDTO> it = importDetails.getEntitiesToImport().keySet().iterator();
							EntityDTO selectedEntity = (EntityDTO) it.next();
							List<ValueEvent> eventValues = new ArrayList<ValueEvent>();
							for (ElementExtractedValue entry : importDetails.getEntitiesToImport().get(selectedEntity)) {
								if (entry.getElement() != null
								                && (entry.getNewValue() != null || entry.getNewBudgetValues() != null)) {
									switch (entry.getElement().getElementType()) {
									case CHECKBOX:
										// TODO Not implemented yet
										break;
									case DEFAULT:
										DefaultFlexibleElementDTO defaultElementDTO = (DefaultFlexibleElementDTO) entry
										                .getElement();
										switch (defaultElementDTO.getType()) {
										case BUDGET:
											for (Integer budgetFieldId : entry.getNewBudgetValues().keySet()) {
												entry.getOldBudgetValues().put(
												                budgetFieldId,
												                entry.getNewBudgetValues().get(budgetFieldId)
												                                .toString());
											}
											Map<BudgetSubFieldDTO, String> budgetSubFieldsValue = new HashMap<BudgetSubFieldDTO, String>();
											for (Entry<Integer, String> entryBudgetSubField : entry
											                .getOldBudgetValues().entrySet()) {
												BudgetSubFieldDTO budgetSubFieldDTO = new BudgetSubFieldDTO();
												budgetSubFieldDTO.setId(entryBudgetSubField.getKey());
												budgetSubFieldsValue.put(budgetSubFieldDTO,
												                entryBudgetSubField.getValue());
											}
											eventValues.add(new ValueEvent(entry.getElement(), ValueResultUtils
											                .mergeElements(budgetSubFieldsValue)));
											break;
										case CODE:
											eventValues.add(new ValueEvent(entry.getElement(), String.valueOf(entry
											                .getNewValue())));
											break;
										case END_DATE:
											Long longEndValue = ((Date) entry.getNewValue()).getTime();
											eventValues.add(new ValueEvent(entry.getElement(), String
											                .valueOf(longEndValue)));
											break;
										case START_DATE:
											Long longStartValue = ((Date) entry.getNewValue()).getTime();
											eventValues.add(new ValueEvent(entry.getElement(), String
											                .valueOf(longStartValue)));
											break;
										case TITLE:
											eventValues.add(new ValueEvent(entry.getElement(), String.valueOf(entry
											                .getNewValue())));
											break;
										default:
											break;

										}
										break;
									case MESSAGE:
										// TODO Not implemented yet
										break;
									case QUESTION:
										// TODO Not implemented yet
										break;
									case TEXT_AREA:
										// TODO Not implemented yet
										break;
									case TRIPLETS:
										// TODO Not implemented yet
										break;
									default:
										break;

									}

								}
							}

							final String entityName;
							if (selectedEntity instanceof ProjectDTO) {
								entityName = ((ProjectDTO) selectedEntity).getFullName();
							} else {
								entityName = ((OrgUnitDTOLight) selectedEntity).getFullName();
							}

							final UpdateProject cmd = new UpdateProject(selectedEntity.getId(), eventValues);
							dispatcher.execute(cmd, getGridMaskingMonitor(), new AsyncCallback<VoidResult>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onSuccess(VoidResult result) {
									MessageBox.alert(I18N.CONSTANTS.importItem(),
									                I18N.MESSAGES.importSuccessful(entityName), null);

								}
							});
						}
					}
				} else {
					MessageBox.alert(I18N.CONSTANTS.importItem(), I18N.CONSTANTS.importDetailsWindowSelectionEmpty(),
					                null);
				}

			}
		});

		ToolBar toolbar = new ToolBar();
		toolbar.setAlignment(HorizontalAlignment.CENTER);
		toolbar.add(importButton);

		return toolbar;
	}

	private MaskingAsyncMonitor getGridMaskingMonitor() {
		return new MaskingAsyncMonitor(grid, I18N.CONSTANTS.loading());
	}

	/**
	 * @return the importButton
	 */
	public Button getImportButton() {
		return importButton;
	}

}