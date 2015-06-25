package org.sigmah.client.ui.presenter.admin.importation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.AbstractAdminPresenter;
import org.sigmah.client.ui.view.admin.importation.ImportationSchemeAdminView;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import org.sigmah.shared.command.DeleteImportationSchemes;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Presenter for the administration page of importation schemes.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ImportationSchemeAdminPresenter extends AbstractAdminPresenter<ImportationSchemeAdminPresenter.View> {

	/**
	 * Current selected Importation Scheme DTO.
	 */
	private ImportationSchemeDTO currentImportationSchemeDTO;

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ImportationSchemeAdminView.class)
	public static interface View extends AbstractAdminPresenter.View {

		void setImportationSchemePresenterHandler(ImportationSchemePresenterHandler importationSchemePresenterHandler);

		ListStore<VariableDTO> getVariablesStore();

		ListStore<ImportationSchemeDTO> getSchemesStore();

		void confirmDeleteSchemesSelected(ConfirmCallback confirmCallback);

		void confirmDeleteVariablesSelected(ConfirmCallback confirmCallback);

		List<VariableDTO> getVariablesSelection();

		List<ImportationSchemeDTO> getSchemesSelection();

		LoadingMask getVariablesLoadingMonitor();

		LoadingMask getSchemesLoadingMonitor();

		Button getAddVariableButton();

		Button getDeleteVariableButton();

		Button getDeleteSchemeButton();

		Button getAddSchemeButton();
		
		Button getEditSchemeButton();

		Button getSaveSheetNameFirstRowButton();
		
		IconButton getVariablesCloseButton();

		Grid<ImportationSchemeDTO> getSchemesGrid();

		Grid<VariableDTO> getVariablesGrid();

		ContentPanel getSchemePanel();

		ContentPanel getVariablePanel();

		NumberField getFirstRow();

		TextField<String> getSheetName();

		Label getFirstRowLabel();

		Label getSheetNameLabel();

		boolean isFirstRowSheetNameValid(ImportationSchemeFileFormat format);

	}

	/**
	 * ImportationSchemePresenterHandler Grid Importation Scheme & Grid Imporation Scheme Varaible
	 * 
	 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr).
	 */

	public interface ImportationSchemePresenterHandler {

		/**
		 * Edit Importation Scheme
		 * 
		 * @param importationSchemeDTO
		 */
		void onImportationSchemeEdit(ImportationSchemeDTO importationSchemeDTO);

		/**
		 * Edit Variable Importation Scheme
		 * 
		 * @param variableDTO
		 */
		void onVariableImportationSchemeEdit(VariableDTO variableDTO);

	}

	@Inject
	protected ImportationSchemeAdminPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public Page getPage() {
		return Page.ADMIN_IMPORTATION_SCHEME;
	}

	@Override
	public void onBind() {
		// --
		// Add importation sheme button.
		// --
		view.getAddSchemeButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				eventBus.navigate(Page.ADMIN_ADD_IMPORTATION_SCHEME);
			}
		});

		
		// --
		// Defining delete and edit button enable rule.
		// --
		view.getSchemesGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ImportationSchemeDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ImportationSchemeDTO> se) {
				final boolean enabled = se.getSelectedItem() != null;
				view.getDeleteSchemeButton().setEnabled(enabled);
				view.getEditSchemeButton().setEnabled(enabled);
			}
		});
		
		// --
		// Delete importation sheme button.
		// --
		view.getDeleteSchemeButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final ImportationSchemeDTO scheme = view.getSchemesGrid().getSelectionModel().getSelectedItem();
				N10N.confirmation(I18N.CONSTANTS.delete(), I18N.MESSAGES.confirmDeleteSchemes(scheme.getName()), new ConfirmCallback() {

					@Override
					public void onAction() {
						deleteImportationScheme(scheme);
					}
				});
			}
		});
		
		// --
		// Edit importation sheme button.
		// --
		view.getEditSchemeButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final ImportationSchemeDTO scheme = view.getSchemesGrid().getSelectionModel().getSelectedItem();
				eventBus.navigateRequest(Page.ADMIN_ADD_IMPORTATION_SCHEME.request()
					.addData(RequestParameter.DTO, scheme));
			}
		});

		// --
		// Add importation sheme variable button.
		// --
		view.getAddVariableButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if (currentImportationSchemeDTO != null) {
					eventBus.navigateRequest(Page.ADMIN_ADD_VARIABLE_IMPORTATION_SCHEME.request()
						.addData(RequestParameter.IMPORTATION_SCHEME, currentImportationSchemeDTO));
				} else {
					N10N.message(I18N.MESSAGES.importSchemenNoSelected(), MessageType.INFO);
				}

			}

		});

		// --
		// Save sheet name Importation Scheme Variable.
		// --
		view.getSaveSheetNameFirstRowButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				Map<String, Object> newSchemaProperties = new HashMap<String, Object>();

				if (view.isFirstRowSheetNameValid(currentImportationSchemeDTO.getFileFormat())) {

					newSchemaProperties.put(AdminUtil.ADMIN_SCHEMA, currentImportationSchemeDTO);
					newSchemaProperties.put(AdminUtil.PROP_SCH_FIRST_ROW, view.getFirstRow().getValue().intValue());
					newSchemaProperties.put(AdminUtil.PROP_SCH_SHEET_NAME, view.getSheetName().getValue());

					CreateEntity cmd = new CreateEntity(ImportationSchemeDTO.ENTITY_NAME, newSchemaProperties);

					dispatch.execute(cmd, new CommandResultHandler<CreateResult>() {

						@Override
						public void onCommandSuccess(CreateResult result) {

							N10N.message(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminImportationSchemeUpdateConfirm(), MessageType.INFO);

							ImportationSchemeDTO schemaUpdated = (ImportationSchemeDTO) result.getEntity();
							currentImportationSchemeDTO.setFirstRow(schemaUpdated.getFirstRow());
							currentImportationSchemeDTO.setSheetName(schemaUpdated.getSheetName());
							view.getSchemesStore().update(currentImportationSchemeDTO);
							view.getSchemesStore().commitChanges();
							
							view.getSaveSheetNameFirstRowButton().disable();
						}

					});
				} else {
					N10N.message(I18N.MESSAGES.importationSchemeVariableInvalidValues(), MessageType.ERROR);
				}
			}
		});

		// --
		// Delete Variable Importation Scheme.
		// --
		view.getVariablesGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<VariableDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<VariableDTO> se) {
				view.getDeleteVariableButton().setEnabled(!se.getSelection().isEmpty());
			}
		});
		
		view.getDeleteVariableButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final List<VariableDTO> variables = view.getVariablesGrid().getSelectionModel().getSelection();
				
				final StringBuilder variableNames = new StringBuilder();
				for(final VariableDTO variable : variables) {
					variableNames.append(variable.getName()).append(", ");
				}
				if(variableNames.length() > 0) {
					variableNames.setLength(variableNames.length() - 2);
				}
				
				N10N.confirmation(I18N.CONSTANTS.delete(), I18N.MESSAGES.confirmDeleteVariables(variableNames.toString()), new ConfirmCallback() {

					@Override
					public void onAction() {
						deleteVariables(variables);
					}
				});
			}
		});
		
		view.getVariablesCloseButton().addSelectionListener(new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(IconButtonEvent ce) {
				eventBus.navigateRequest(Page.ADMIN_IMPORTATION_SCHEME.request()
					.addData(RequestParameter.NO_REFRESH, true));
			}
		});

		// --
		// Presenter Handler.
		// --
		view.setImportationSchemePresenterHandler(new ImportationSchemePresenterHandler() {

			// Edit Importation Scheme Button
			@Override
			public void onImportationSchemeEdit(ImportationSchemeDTO importationSchemeDTO) {
				eventBus.navigateRequest(Page.ADMIN_IMPORTATION_SCHEME.request()
					.addParameter(RequestParameter.MODEL, importationSchemeDTO.getId())
					.addData(RequestParameter.DTO, importationSchemeDTO));
			}

			// Edit Variable Importation Scheme
			@Override
			public void onVariableImportationSchemeEdit(VariableDTO variableDTO) {
				PageRequest request = Page.ADMIN_ADD_VARIABLE_IMPORTATION_SCHEME.request().addData(RequestParameter.IMPORTATION_SCHEME, currentImportationSchemeDTO);
				request.addData(RequestParameter.VARIABLE_IMPORTATION_SCHEME, variableDTO);
				eventBus.navigateRequest(request);
			}
		});
		
		// --
		// Register Handler
		// --
		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {
				
				if (event.concern(UpdateEvent.IMPORTATION_SCHEME_UPDATE)) {
					final Integer selection = currentImportationSchemeDTO != null ? currentImportationSchemeDTO.getId() : null;
					loadImportationShemes(selection);
				}

				if (event.concern(UpdateEvent.VARIABLE_SCHEME_UPDATE)) {
					currentImportationSchemeDTO = event.getParam(0);
					final Integer selection = currentImportationSchemeDTO != null ? currentImportationSchemeDTO.getId() : null;

					loadImportationShemes(selection);
				}
			}
		}));

	}
	
	@Override
	public void onPageRequest(PageRequest request) {

		final Integer currentSchemeId = request.getParameterInteger(RequestParameter.MODEL);
		final ImportationSchemeDTO scheme = request.getData(RequestParameter.DTO);
		final Boolean dontRefresh = request.getData(RequestParameter.NO_REFRESH);
		
		view.getVariablePanel().setVisible(false);
		
		if(dontRefresh != null && dontRefresh) {
			return;
		}
		
		if(scheme == null) {
			loadImportationShemes(currentSchemeId);
			
		} else {
			loadImportationScheme(scheme);
		}
		
	}

	@Override
	protected boolean hasValueChanged() {
		return view.getSaveSheetNameFirstRowButton().isEnabled();
	}

	/**
	 * Hides the specific fields for the ROW import type
	 */
	public void hideSheetNameFirstRow() {

		view.getFirstRow().hide();
		view.getSheetName().hide();
		view.getSaveSheetNameFirstRowButton().hide();
		view.getFirstRowLabel().hide();
		view.getSheetNameLabel().hide();
		
		view.getSaveSheetNameFirstRowButton().disable();

		if (currentImportationSchemeDTO != null && ImportationSchemeImportType.ROW.equals(currentImportationSchemeDTO.getImportType())) {

			view.getSaveSheetNameFirstRowButton().show();
			view.getFirstRow().show();
			view.getFirstRowLabel().show();
			
			if (!ImportationSchemeFileFormat.CSV.equals(currentImportationSchemeDTO.getFileFormat())) {

				view.getSheetNameLabel().show();
				view.getSheetName().show();

			}

		}

	}

	/**
	 * Load Importation Scheme
	 */
	public void loadImportationShemes(final Integer selection) {
		
		view.getSchemesStore().removeAll();
		view.getSchemesStore().clearFilters();

		dispatch.execute(new GetImportationSchemes(), new CommandResultHandler<ListResult<ImportationSchemeDTO>>() {

			@Override
			public void onCommandSuccess(ListResult<ImportationSchemeDTO> result) {

				if (result.getList() != null && !result.getList().isEmpty()) {

					view.getSchemesStore().add(result.getList());

					if(selection != null) {
						for(final ImportationSchemeDTO scheme : result.getList()) {
							if(selection.equals(scheme.getId())) {
								loadImportationScheme(scheme);
							}
						}
					} else {
						view.getSaveSheetNameFirstRowButton().disable();
					}
				}

			}

		}, view.getSchemesLoadingMonitor());

	}
	
	private void loadImportationScheme(ImportationSchemeDTO scheme) {
		currentImportationSchemeDTO = scheme;
		view.getSchemesGrid().getSelectionModel().select(scheme, false);
		
		view.getVariablePanel().setHeadingText(scheme.getName());
		
		if (currentImportationSchemeDTO.getFirstRow() != null) {

			view.getFirstRow().setValue(currentImportationSchemeDTO.getFirstRow());

		} else {
			view.getFirstRow().clear();
		}

		if (currentImportationSchemeDTO.getSheetName() != null) {

			view.getSheetName().setValue(currentImportationSchemeDTO.getSheetName());

		} else {

			view.getSheetName().clear();

		}

		hideSheetNameFirstRow();

		switch (currentImportationSchemeDTO.getImportType()) {

			case ROW:
				view.getVariablesGrid().getColumnModel().getColumnById("reference").setHeaderHtml(I18N.CONSTANTS.adminImportReferenceColumn());
				break;
			case SEVERAL:
				view.getVariablesGrid().getColumnModel().getColumnById("reference").setHeaderHtml(I18N.CONSTANTS.adminImportReferenceCell());
				break;
			case UNIQUE:
				view.getVariablesGrid().getColumnModel().getColumnById("reference").setHeaderHtml(I18N.CONSTANTS.adminImportReferenceSheetCell());
				break;
			default:
				break;

		}

		view.getVariablesGrid().show();
		view.getVariablesStore().removeAll();
		view.getVariablesStore().add(currentImportationSchemeDTO.getVariables());
		view.getVariablesStore().commitChanges();
		view.getAddVariableButton().enable();
		
		view.getVariablePanel().setVisible(true);
	}
	
	private void deleteImportationScheme(final ImportationSchemeDTO scheme) {
		dispatch.execute(new DeleteImportationSchemes(scheme.getId()), new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
				view.getSchemesStore().remove(scheme);
				view.getSchemesStore().commitChanges();
				
				// BUGFIX #799 : Verifying if an importation scheme has been selected before using it.
				if(currentImportationSchemeDTO != null && currentImportationSchemeDTO.equals(scheme)) {
					eventBus.navigateRequest(Page.ADMIN_IMPORTATION_SCHEME.request()
						.addData(RequestParameter.NO_REFRESH, true));
				}
				
				N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminImportationSchemesDeleteConfirm());
			}
		}, view.getSchemesLoadingMonitor());
	}
	
	private void deleteVariables(final List<VariableDTO> variables) {
		final ArrayList<Integer> ids = new ArrayList<Integer>();
		for(final VariableDTO variable : variables) {
			ids.add(variable.getId());
		}
		
		dispatch.execute(new DeleteImportationSchemes(ids), new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
				for(final VariableDTO variable : variables) {
					view.getVariablesStore().remove(variable);
				}
				view.getSchemesStore().commitChanges();
				
				N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminVariableDeleteConfirm());
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.VARIABLE_SCHEME_UPDATE, currentImportationSchemeDTO));
			}
		}, view.getSchemesLoadingMonitor());
	}
}
