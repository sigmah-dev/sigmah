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

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
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

		Button getSaveSheetNameFirstRowButton();

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
	public void onPageRequest(PageRequest request) {

		hideSheetNameFirstRow();

	}

	@Override
	public void onBind() {

		// LOAD IMPORATATION SCHEMES

		loadImportationShemes();

		// ADD IMPORTATION SHEME BUTTON

		view.getAddSchemeButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				eventBus.navigate(Page.ADMIN_ADD_IMPORTATION_SCHEME);
			}
		});

		// DELETE IMPORTATIONB SHEME BUTTON

		view.getDeleteSchemeButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				N10N.message("Delete Importation Scheme", MessageType.ERROR);
			}
		});

		// ADD VARIBALE IMPORTATION SHEME BUTTON

		view.getAddVariableButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				if (currentImportationSchemeDTO != null) {
					eventBus.navigateRequest(Page.ADMIN_ADD_VARIABLE_IMPORTATION_SCHEME.request()
						.addData(RequestParameter.IMPORATION_SCHEME, currentImportationSchemeDTO));
				} else {
					N10N.message(I18N.MESSAGES.importSchemenNoSelected(), MessageType.INFO);
				}

			}

		});

		// Save sheet name Importation Scheme Variable

		view.getSaveSheetNameFirstRowButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

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
						}

					});
				} else {

					N10N.message(I18N.MESSAGES.importationSchemeVariableInvalidValues(), MessageType.ERROR);

				}

			}

		});

		// Delete Variable Importation Scheme

		view.getDeleteVariableButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				N10N.message("DELETE IMPORATION SCHEME VARIABLE", MessageType.INFO);

			}
		});

		// Presenter Handler

		view.setImportationSchemePresenterHandler(new ImportationSchemePresenterHandler() {

			// Edit Importation Scheme Button
			@Override
			public void onImportationSchemeEdit(ImportationSchemeDTO importationSchemeDTO) {
				eventBus.navigateRequest(Page.ADMIN_ADD_IMPORTATION_SCHEME.request().addData(RequestParameter.DTO, importationSchemeDTO));
			}

			// Edit Variable Importation Scheme
			@Override
			public void onVariableImportationSchemeEdit(VariableDTO variableDTO) {

				PageRequest request = Page.ADMIN_ADD_VARIABLE_IMPORTATION_SCHEME.request().addData(RequestParameter.IMPORATION_SCHEME, currentImportationSchemeDTO);
				request.addData(RequestParameter.VARIABLE_IMPORTATION_SCHEME, variableDTO);
				eventBus.navigateRequest(request);

			}
		});

		// Select Importation Sheme
		view.getSchemesGrid().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				currentImportationSchemeDTO = view.getSchemesGrid().getSelectionModel().getSelectedItem();

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

			}
		});

		// register Handler

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.UPDATE_LISTE_IMPORTATION_SCHEME)) {

					loadImportationShemes();

				}

				if (event.concern(UpdateEvent.UPDATE_LISTE_VARIABLE_SCHEME)) {

					currentImportationSchemeDTO = event.getParam(0);

					loadImportationShemes();

					view.getSchemesGrid().getSelectionModel().select(currentImportationSchemeDTO, true);

					view.getVariablesGrid().show();
					view.getVariablesStore().removeAll();
					view.getVariablesStore().add(currentImportationSchemeDTO.getVariables());
					view.getVariablesStore().commitChanges();

				}

			}
		}));

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
	public void loadImportationShemes() {

		dispatch.execute(new GetImportationSchemes(), new CommandResultHandler<ListResult<ImportationSchemeDTO>>() {

			@Override
			public void onCommandSuccess(ListResult<ImportationSchemeDTO> result) {

				view.getSchemesStore().removeAll();
				view.getSchemesStore().clearFilters();

				if (result.getList() != null && !result.getList().isEmpty()) {

					view.getSchemesStore().add(result.getList());
					view.getSchemesStore().commitChanges();

				}

			}

		}, view.getSchemesLoadingMonitor());

	}
}
