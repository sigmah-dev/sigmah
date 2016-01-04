package org.sigmah.client.ui.presenter.importation;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.CreateProjectPresenter;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.presenter.project.AbstractProjectPresenter;
import org.sigmah.client.ui.view.importation.ElementExtractedValuePopup;
import org.sigmah.client.ui.view.importation.ImportDetailsPopup;
import org.sigmah.client.ui.view.importation.ImportationView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.offline.fileapi.Blob;
import org.sigmah.shared.command.AmendmentActionCommand;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GetImportInformation;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ImportInformationResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dto.ElementExtractedValue;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementContainer;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.AmendmentAction;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementExtractedValueStatus;
import org.sigmah.shared.dto.referential.ImportStatusCode;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.servlet.ServletConstants;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * Data import presenter.
 * <p/>
 * Based on work done by Guerline Jean-Baptiste.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ImportationPresenter extends AbstractPagePresenter<ImportationPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ImportationView.class)
	public static interface View extends AbstractProjectPresenter.View, HasForm {
		Field<ImportationSchemeDTO> getSchemeField();
		FileUploadField getFileField();
		Button getImportButton();
		
		ImportDetailsPopup getImportDetailsPopup();
		ElementExtractedValuePopup getElementExtractedValuePopup();
		
		ListStore<ImportationSchemeDTO> getSchemeListStore();
        
        void hide();
	}
	
	private HandlerRegistration handlerRegistration;
	private final Map<Integer, List<ElementExtractedValue>> changes;
	private String fileName;
	private String lastFileId;
	
	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ImportationPresenter(View view, Injector injector) {
		super(view, injector);
		changes = new HashMap<Integer, List<ElementExtractedValue>>();
	}
	
	@Override
	public Page getPage() {
		return Page.IMPORT_VALUES;
	}

	@Override
	public void onBind() {
		setPageTitle(I18N.CONSTANTS.importItem());
		
		// --
		// Action when submitting the form.
		// --
		final ServletUrlBuilder servletUrlBuilder = new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), 
			ServletConstants.Servlet.IMPORT, ServletConstants.ServletMethod.IMPORT_STORE_FILE);
		
		final FormPanel form = view.getForms()[0];
		form.setEncoding(FormPanel.Encoding.MULTIPART);
		form.setMethod(Method.POST);
		form.setAction(servletUrlBuilder.toString());
		
		form.addListener(Events.Submit, new Listener<FormEvent>() {

			@Override
			public void handleEvent(FormEvent be) {
				onSubmit(be.getResultHtml());
			}
		});
		
		// --
		// Action of the import button.
		// --
		view.getImportButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				view.getForms()[0].submit();
				
				final Blob blob = Blob.getBlobFromInputFileElement(view.getFileField().getFileInput());
				if(blob != null) {
					fileName = blob.getName();
				}
			}
		});
		
		// --
		// Import details popup.
		// --
		final ImportDetailsPopup popup = view.getImportDetailsPopup();
		popup.setActionRenderer(new ActionRenderer() {

			@Override
			public Widget renderActionsForModel(ImportDetails model) {
				switch(model.getEntityStatus()) {
					case PROJECT_NOT_FOUND_CODE:
						return renderCreateButton(model);
						
					case PROJECT_FOUND_CODE:
					case ORGUNIT_FOUND_CODE:
						addAllChanges(model);
						popup.addModelToSelection(model);
						return renderConfirmButton(model);
						
					case PROJECT_LOCKED_CODE:
						return renderUnlockButton(model);
						
					case SEVERAL_ORGUNITS_FOUND_CODE:
					case SEVERAL_PROJECTS_FOUND_CODE:
						return renderChoosePanel(model);
						
					default:
						return null;
				}
			}
		});
		
		popup.getImportButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onImport();
				// Closing the file selection popup.
                view.hide();
			}
		});
		
		// --
		// Extracted value popup.
		// --
		final ElementExtractedValuePopup elementExtractedValuePopup = view.getElementExtractedValuePopup();
		elementExtractedValuePopup.getConfirmButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final List<ElementExtractedValue> selection = elementExtractedValuePopup.getSelection();
				changes.put((Integer)elementExtractedValuePopup.getEntity().getId(), selection);
				
				if (!selection.isEmpty()) {
					view.getImportDetailsPopup().getGrid().getSelectionModel().select(elementExtractedValuePopup.getParentModel(), true);
				} else {
					view.getImportDetailsPopup().getGrid().getSelectionModel().deselect(elementExtractedValuePopup.getParentModel());
				}
                      
                elementExtractedValuePopup.hide();
                       
			}
		});
	}

	@Override
	public void onPageRequest(PageRequest request) {
		this.lastFileId = null;
		this.fileName = null;
		
		view.getSchemeListStore().removeAll();
		view.getSchemeField().clear();
		if(view.getFileField().isRendered()) {
			view.getFileField().clear();
		}
		
		changes.clear();
		
		dispatch.execute(new GetImportationSchemes(), new CommandResultHandler<ListResult<ImportationSchemeDTO>>() {

			@Override
			protected void onCommandSuccess(ListResult<ImportationSchemeDTO> result) {
				view.getSchemeListStore().add(result.getList());
			}
		}, view.getImportButton());
		
		// Register events.
		registerEvents();
	}

	@Override
	public void beforeLeaving(EventBus.LeavingCallback callback) {
		// Stop listener to creation events.
		deregisterEvents();
		
		callback.leavingOk();
	}
	
	// --
	// Event handling.
	// --
	
	private void registerEvents() {
		handlerRegistration = eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(UpdateEvent event) {
				if(event.concern(UpdateEvent.PROJECT_CREATE)) {
					final ListStore<ImportDetails> store = view.getImportDetailsPopup().getStore();
					
					final ProjectDTO project = event.getParam(1);
					
					ImportDetails toBeRemoved = null;
					for(final ImportDetails importDetails : store.getModels()) {
						// TODO: KeyIdentification may be something other than the code.
						if(project.getName().equals(importDetails.getKeyIdentification()) &&
							project.getProjectModel().getName().equals(importDetails.getModelName())) {
							toBeRemoved = importDetails;
						}
					}
					
					store.remove(toBeRemoved);
					store.commitChanges();
					
					// BUGFIX #774: Reloading results to take created projects in account.
					refreshResults();
				}
			}
		});
	}
	
	private void deregisterEvents() {
		handlerRegistration.removeHandler();
		handlerRegistration = null;
	}
	
	// --
	// Utility methods.
	// --
	
	private void onSubmit(String result) {
		switch (ServletConstants.getErrorCode(result)) {
			case Response.SC_OK:
				loadImportResults(result);
				break;

			default:
				N10N.error(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES.importFormIncompleteDetails(""));
				break;
		}
	}
	
	private void loadImportResults(String fileId) {
		this.lastFileId = fileId;
		
		dispatch.execute(new GetImportInformation(fileId, view.getSchemeField().getValue()), new CommandResultHandler<ImportInformationResult>() {

			@Override
			protected void onCommandSuccess(ImportInformationResult result) {
				// Shows result popup.
				final ImportDetailsPopup popup = view.getImportDetailsPopup();
				popup.getStore().removeAll();
				popup.getStore().add(result.getEntitiesToImport());
				popup.center();
			}
		}, view.getImportButton());
	}
	
	private void refreshResults() {
		if(lastFileId != null) {
			final ImportDetailsPopup popup = view.getImportDetailsPopup();
			
			dispatch.execute(new GetImportInformation(lastFileId, view.getSchemeField().getValue()), new CommandResultHandler<ImportInformationResult>() {

				@Override
				protected void onCommandSuccess(ImportInformationResult result) {
					popup.getStore().removeAll();
					popup.getStore().add(result.getEntitiesToImport());
				}
				
			}, view.getImportButton(), new LoadingMask(popup.getGrid()));
		}
	}
	
	private Button renderCreateButton(final ImportDetails model) {
		final Button createButton = Forms.button(I18N.CONSTANTS.createProjectCreateButton());
		
		createButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final Map<EntityDTO<?>, List<ElementExtractedValue>> entities = model.getEntitiesToImport();
				
				final Iterator<EntityDTO<?>> iterator = entities.keySet().iterator();
				final EntityDTO<?> notFoundEntity = iterator.next();

				// Prepare the page request and select the creation type based 
				// on the status of the project model.
				final PageRequest request = Page.CREATE_PROJECT.requestWith(
					RequestParameter.TYPE, model.getModelStatus() == ProjectModelStatus.DRAFT ? 
						CreateProjectPresenter.Mode.TEST_PROJECT : 
						CreateProjectPresenter.Mode.PROJECT
				);
				
				// Pre-defines the extracted values in the create project popup.
				final List<ElementExtractedValue> extractedValues = entities.get(notFoundEntity);
				for(final ElementExtractedValue extractedValue : extractedValues) {
					// --					
					// Budget.
					// --
					if(extractedValue.getElement() instanceof BudgetElementDTO) {
						final BudgetElementDTO element = (BudgetElementDTO)extractedValue.getElement();
						
						for (BudgetSubFieldDTO budgetSubField : element.getBudgetSubFields()) {
							final Double value = (Double) extractedValue.getNewBudgetValues().get(budgetSubField.getId());
							
							if(budgetSubField.getType() == BudgetSubFieldType.PLANNED && value != null) {
								request.addData(RequestParameter.BUDGET, value);
							}
						}
						
					} else if(extractedValue.getElement() instanceof DefaultFlexibleElementDTO) {
						final DefaultFlexibleElementDTO element = (DefaultFlexibleElementDTO)extractedValue.getElement();
					
						// --
						// Code.
						// --
						if(element.getType() == DefaultFlexibleElementType.CODE) {
							request.addData(RequestParameter.CODE, extractedValue.getNewValue());
							
						// --
						// Title.
						// --
						} else if(element.getType() == DefaultFlexibleElementType.TITLE) {
							request.addData(RequestParameter.TITLE, extractedValue.getNewValue());
						}
					}
				}
				// --
				// Project Model.
				// --
				request.addData(RequestParameter.MODEL, model.getModelName());
				
				eventBus.navigateRequest(request, createButton);
			}
		});
		
		return createButton;
	}
	
	private void addAllChanges(final ImportDetails model) {
		final Map<EntityDTO<?>, List<ElementExtractedValue>> entities = model.getEntitiesToImport();
		
		if(!entities.keySet().isEmpty()) {
			final Iterator<EntityDTO<?>> iterator = entities.keySet().iterator();
			final EntityDTO<?> entity = iterator.next();
			
			changes.put((Integer) entity.getId(), entities.get(entity));
		}
	}
	
	private Button renderConfirmButton(final ImportDetails model) {
		final Button confirmButton = Forms.button(I18N.CONSTANTS.importButtonConfirmDetails());
		
		confirmButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final Map<EntityDTO<?>, List<ElementExtractedValue>> entities = model.getEntitiesToImport();
				
				if(!entities.keySet().isEmpty()) {
					// Displaying the "select changes" popup.
					final ElementExtractedValuePopup popup = view.getElementExtractedValuePopup();
					
					final Iterator<EntityDTO<?>> iterator = entities.keySet().iterator();
					final EntityDTO<?> entity = iterator.next();
					popup.setEntity(entity);
                    popup.setParentModel(model);
					
					popup.getStore().removeAll();
					popup.getStore().add(entities.get(entity));
					
					popup.getSelectionModel().select(changes.get((Integer)entity.getId()), false);
					
					popup.center();
				}
			}
		});
		
		return confirmButton;
	}
	
	private Button renderUnlockButton(final ImportDetails model) {
		final Button unlockButton = Forms.button(I18N.CONSTANTS.projectCoreUnlockButton());
		
		unlockButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final Iterator<EntityDTO<?>> iterator = model.getEntitiesToImport().keySet().iterator();
				final EntityDTO<?> entityLocked = iterator.next();
				
				dispatch.execute(new AmendmentActionCommand((Integer)entityLocked.getId(), AmendmentAction.UNLOCK), new CommandResultHandler<ProjectDTO>() {

					@Override
					protected void onCommandSuccess(ProjectDTO result) {
						model.setEntityStatus(ImportStatusCode.PROJECT_FOUND_CODE);
						
						final ImportDetailsPopup popup = view.getImportDetailsPopup();
						popup.addModelToSelection(model);
						popup.getStore().update(model);
						popup.getStore().commitChanges();
					}
					
				}, unlockButton);
			}
		});
		
		return unlockButton;
	}
	
	private Panel renderChoosePanel(final ImportDetails model) {
		// Project/orgUnit combobox.
		final ComboBox<DefaultFlexibleElementContainer> comboBox = Forms.combobox(null, false, ProjectDTO.ID, ProjectDTO.NAME, I18N.CONSTANTS.formWindowListEmptyText());
		for(final EntityDTO<?> entity : model.getEntitiesToImport().keySet()) {
			comboBox.getStore().add((DefaultFlexibleElementContainer) entity);
		}
		
		// Choose button.
		final Button chooseButton = Forms.button(I18N.CONSTANTS.importButtonChoose());
		
		chooseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final DefaultFlexibleElementContainer container = comboBox.getValue();
				if (container != null) {
					// Rebuild the "entitiesToImport" map.
					final EntityDTO<?> entity = (EntityDTO<?>)container;
					final List<ElementExtractedValue> extractedValues = model.getEntitiesToImport().get(entity);
					
					final HashMap<EntityDTO<?>, List<ElementExtractedValue>> entitiesToImport = new HashMap<EntityDTO<?>, List<ElementExtractedValue>>();
					entitiesToImport.put(entity, extractedValues);
					
					// Update the importDetails.
					model.setEntitiesToImport(entitiesToImport);
					model.setEntityStatus(entity instanceof ProjectDTO ?
						ImportStatusCode.PROJECT_FOUND_CODE :
						ImportStatusCode.ORGUNIT_FOUND_CODE);
					
					// Update the grid.
					view.getImportDetailsPopup().getGrid().getSelectionModel().select(model, true);
					view.getImportDetailsPopup().getStore().update(model);
					view.getImportDetailsPopup().getStore().commitChanges();
				}
			}
		});
		
		// Creating the panel.
		final HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(5);
		panel.add(comboBox);
		panel.add(chooseButton);
		
		return panel;
	}
	
	/**
	 * Apply all the selected changes.
	 */
	private void onImport() {
		final List<ImportDetails> selection = view.getImportDetailsPopup().getSelection();
		
		if(!selection.isEmpty()) {
			final BatchCommand updates = new BatchCommand();
			final ArrayList<String> names = new ArrayList<String>();
			
			for(final ImportDetails importDetails : selection) {
				if(ImportStatusCode.isFound(importDetails.getEntityStatus()) && 
					!importDetails.getEntitiesToImport().isEmpty()) {
					// Prepare an UpdateProject command for this ImportDetails object.
					final Iterator<EntityDTO<?>> iterator = importDetails.getEntitiesToImport().keySet().iterator();
					final EntityDTO<?> selectedEntity = iterator.next();
					final List<ValueEvent> values = toValueEvent(changes.get((Integer)selectedEntity.getId()));
					
					// Save the name of the project (to notify that this project has been updated).
					if(selectedEntity instanceof ProjectDTO) {
						names.add(((ProjectDTO)selectedEntity).getFullName());
						
					} else if(selectedEntity instanceof OrgUnitDTO) {
						names.add(((OrgUnitDTO)selectedEntity).getFullName());
					}
					
					// Add the update project to the batch.
					// TODO: I18N
					updates.add(new UpdateProject((Integer) selectedEntity.getId(), values, "Imported from file '" + fileName + "'."));
				}
			}
			
			updates.add(new GetImportInformation(fileName, true));
			
			if(!updates.getCommands().isEmpty()) {
				dispatch.execute(updates, new CommandResultHandler<ListResult<Result>>() {

					@Override
					protected void onCommandSuccess(ListResult<Result> result) {
						// Display a "save success" notification for each modified project.
						for(final String name : names) {
							N10N.infoNotif(I18N.CONSTANTS.importItem(), I18N.MESSAGES.importSuccessful(name));
						}
						view.getImportDetailsPopup().hide();
					}

				}, view.getImportDetailsPopup().getImportButton());
			}
			
		} else {
			// Warn the user that no change has been selected.
			N10N.warn(I18N.CONSTANTS.importItem(), I18N.CONSTANTS.importDetailsWindowSelectionEmpty());
		}
	}
	
	/**
	 * Convert every given {@link ElementExtractedValue} to an instance of {@link ValueEvent}.
	 * 
	 * @param extractedValues <code>ElementExtractedValue</code> to convert.
	 * @return A list of <code>ValueEvent</code>.
	 */
	private List<ValueEvent> toValueEvent(List<ElementExtractedValue> extractedValues) {
		final ArrayList<ValueEvent> valueEvents = new ArrayList<ValueEvent>();
		
		for(final ElementExtractedValue extractedValue : extractedValues) {
			if(extractedValue.getStatus() != ElementExtractedValueStatus.VALID_VALUE) {
				continue;
			}
			
			final FlexibleElementDTO element = extractedValue.getElement();
			
			switch(element.getElementType()) {
				case CHECKBOX:
					if(extractedValue.getNewValue() instanceof Boolean) {
						valueEvents.add(new ValueEvent(element, extractedValue.getNewValue().toString()));
					}
					break;
					
				case DEFAULT:
					final DefaultFlexibleElementDTO defaultFlexibleElement = (DefaultFlexibleElementDTO) element;
					switch(defaultFlexibleElement.getType()) {
						case BUDGET:
							final HashMap<BudgetSubFieldDTO, String> budgetMap = new HashMap<BudgetSubFieldDTO, String>();
							
							// Putting old values first.
							for(final Map.Entry<Integer, String> entry : extractedValue.getOldBudgetValues().entrySet()) {
								budgetMap.put(new BudgetSubFieldDTO(entry.getKey()), entry.getValue());
							}
							
							// Replacing old values with the new ones.
							for (final Map.Entry<Integer, Serializable> entry : extractedValue.getNewBudgetValues().entrySet()) {
								budgetMap.put(new BudgetSubFieldDTO(entry.getKey()), entry.getValue().toString()); 
							}
							
							valueEvents.add(new ValueEvent(element, ValueResultUtils.mergeElements(budgetMap)));
							break;
							
						case MANAGER:
						case OWNER:
						case ORG_UNIT:
						case COUNTRY:
						case CODE:
						case TITLE:
							valueEvents.add(new ValueEvent(element, String.valueOf(extractedValue.getNewValue())));
							break;
							
						case START_DATE:
						case END_DATE:
							valueEvents.add(new ValueEvent(element, dateToString(extractedValue.getNewValue())));
							break;
							
						default:
							break;
					}
					break;
					
				case QUESTION:
					valueEvents.add(new ValueEvent(element, String.valueOf(extractedValue.getNewValue())));
					break;
					
				case TEXT_AREA:
					if(Character.valueOf('D').equals(((TextAreaElementDTO)element).getType())) {
						valueEvents.add(new ValueEvent(element, dateToString(extractedValue.getNewValue())));
					} else {
						valueEvents.add(new ValueEvent(element, String.valueOf(extractedValue.getNewValue())));
					}
					break;
					
				case TRIPLETS:
					final String[] tripletValues = (String[]) extractedValue.getNewValue();
					
					final TripletValueDTO addedValue = new TripletValueDTO();
					addedValue.setCode(tripletValues[0]);
					addedValue.setName(tripletValues[1]);
					addedValue.setPeriod(tripletValues[2]);

					valueEvents.add(new ValueEvent(extractedValue.getElement(), addedValue, ValueEventChangeType.ADD));
					break;
					
				default:
					break;
			}
		}
		
		return valueEvents;
	}
	
	private String dateToString(Serializable serializable) {
		if(serializable instanceof Date) {
			final Date date = (Date) serializable;
			return Long.toString(date.getTime());
			
		} else {
			return "null";
		}
	}
}
