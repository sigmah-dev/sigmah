package org.sigmah.client.ui.presenter.admin.models.importer;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.models.base.IsModelTabPresenter;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.admin.models.importer.ImportationSchemeModelsAdminView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.shared.command.GetImportationSchemeModels;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.shared.command.DeleteImportationSchemeModels;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Presenter of tab "importation schemes" from the project models and org unit 
 * models administration.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <E> Type of model.
 */
public class ImportationSchemeModelsAdminPresenter<E extends IsModel> extends AbstractPresenter<ImportationSchemeModelsAdminPresenter.View> 
implements IsModelTabPresenter<E, ImportationSchemeModelsAdminPresenter.View> {

	private E currentModel;

	private ImportationSchemeModelDTO currentImportationSchemeModel;

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ImportationSchemeModelsAdminView.class)
	public static interface View extends ViewInterface {

		ListStore<ImportationSchemeModelDTO> getImportationSchemeModelsStore();

		ListStore<VariableFlexibleElementDTO> getVariableFlexibleElementStore();

		Grid<ImportationSchemeModelDTO> getImportationSchemeModelsGrid();

		Grid<VariableFlexibleElementDTO> getVariableFlexibleElementsGrid();

		Button getAddVariableFlexibleElementButton();

		Button getDeleteVariableFlexibleElementButton();

		Button getAddImportationSchemeModelButton();

		Button getDeleteImportationSchemeModelButton();

		void setToolbarEnabled(Boolean enable);

		void setImportationSchemeModelsAdminPresenterHandler(ImportationSchemeModelsAdminPresenterHandler importationSchemeModelsAdminPresenterHandler);

	}

	/**
	 * ImportationSchemeModelsAdminPresenterHandler
	 * 
	 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
	 */
	public interface ImportationSchemeModelsAdminPresenterHandler {

		/**
		 * Click Handler
		 * 
		 * @param ImportationSchemeModelDTO
		 */
		void onClick(ImportationSchemeModelDTO ImportationSchemeModelDTO);

	}

	@Inject
	protected ImportationSchemeModelsAdminPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.adminImportationSchemes();
	}

	@Override
	public boolean hasValueChanged() {
		return false;
	}

	@Override
	public void loadTab(E model) {

		currentModel = model;

		loadImportationSchemeModel();

	}

	@Override
	public void onBind() {

		// --
		// Add importation scheme model.
		// --
		view.getAddImportationSchemeModelButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				eventBus.navigateRequest(Page.ADMIN_ADD_IMPORTATION_SCHEME_MODEL.request().addData(RequestParameter.DTO, currentModel));
			}
		});

		// --
		// Delete importation scheme model.
		// --
		view.getImportationSchemeModelsGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ImportationSchemeModelDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ImportationSchemeModelDTO> se) {
				view.getDeleteImportationSchemeModelButton().setEnabled(se.getSelectedItem() != null);
			}
		});
		
		view.getDeleteImportationSchemeModelButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				final ImportationSchemeModelDTO model = view.getImportationSchemeModelsGrid().getSelectionModel().getSelectedItem();
				N10N.confirmation(I18N.CONSTANTS.delete(), I18N.MESSAGES.confirmDeleteSchemeModels(model.getImportationSchemeDTO().getName()), new ConfirmCallback() {

					@Override
					public void onAction() {
						deleteImportationSchemeModel(model);
					}
				});
			}
		});

		// --
		// Add matching rule.
		// --
		view.getAddVariableFlexibleElementButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				PageRequest request = Page.ADMIN_ADD_IMPORTATION_SCHEME_MODEL_MATCHING_RULE.request();

				request.addData(RequestParameter.MODEL, currentModel);
				request.addData(RequestParameter.IMPORTATION_SCHEME_MODEL, currentImportationSchemeModel);

				eventBus.navigateRequest(request);

			}
		});

		// --
		// Delete matching rule.
		// --
		view.getVariableFlexibleElementsGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<VariableFlexibleElementDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<VariableFlexibleElementDTO> se) {
				view.getDeleteVariableFlexibleElementButton().setEnabled(!se.getSelection().isEmpty());
			}
		});
		
		view.getDeleteVariableFlexibleElementButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				final List<VariableFlexibleElementDTO> variables = view.getVariableFlexibleElementsGrid().getSelectionModel().getSelection();
				
				final StringBuilder variableNames = new StringBuilder();
				for(final VariableFlexibleElementDTO variable : variables) {
					variableNames.append(variable.getFlexibleElementDTO().getFormattedLabel()).append(", ");
				}
				if(variableNames.length() > 0) {
					variableNames.setLength(variableNames.length() - 2);
				}
				
				N10N.confirmation(I18N.CONSTANTS.delete(), I18N.MESSAGES.confirmDeleteVariables(variableNames.toString()), new ConfirmCallback() {

					@Override
					public void onAction() {
						deleteVariableFlexibleElements(variables);
					}
				});
			}
		});

		// --
		// Importation Scheme click Handler.
		// --
		view.setImportationSchemeModelsAdminPresenterHandler(new ImportationSchemeModelsAdminPresenterHandler() {

			@Override
			public void onClick(ImportationSchemeModelDTO importationSchemeModel) {
				displayImportationSchemeModel(importationSchemeModel);
			}
		});

		// --
		// Register Handlers.
		// --
		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.IMPORTATION_SCHEME_MODEL_UPDATE)) {
					currentImportationSchemeModel = event.getParam(1);
					loadImportationSchemeModel();

				} else if(event.concern(UpdateEvent.IMPORTATION_MATCHING_RULE_UPDATE)) {
					loadImportationSchemeModel();
				}

			}
		}));
	}

	/**
	 * Load Importation Schemes.
	 */
	public void loadImportationSchemeModel() {

		dispatch.execute(new GetImportationSchemeModels(currentModel), new CommandResultHandler<ListResult<ImportationSchemeModelDTO>>() {

			@Override
			public void onCommandSuccess(ListResult<ImportationSchemeModelDTO> result) {

				view.getImportationSchemeModelsStore().removeAll();
				view.getImportationSchemeModelsStore().clearFilters();

				if (result.getList() != null && !result.getList().isEmpty()) {
					view.getImportationSchemeModelsStore().add(result.getList());
					view.getImportationSchemeModelsStore().commitChanges();
				}
				
				// Update display
				boolean displayed = false;
				if(currentImportationSchemeModel != null && currentImportationSchemeModel.getId() != null) {
					for(final ImportationSchemeModelDTO model : result.getList()) {
						if(currentImportationSchemeModel.getId().equals(model.getId())) {
							displayImportationSchemeModel(model);
							displayed = true;
						}
					}
				}
				
				if(!displayed) {
					clearCurrentImportationSchemeModel();
				}
			}
		}, 
			new LoadingMask(view.getImportationSchemeModelsGrid()), 
			new LoadingMask(view.getVariableFlexibleElementsGrid()));
	}
	
	private void clearCurrentImportationSchemeModel() {
		currentImportationSchemeModel = null;
		view.getVariableFlexibleElementStore().removeAll();
		view.getAddVariableFlexibleElementButton().setEnabled(false);
	}
	
	private void deleteImportationSchemeModel(final ImportationSchemeModelDTO model) {
		dispatch.execute(new DeleteImportationSchemeModels(Collections.singletonList(model.getId()), null), new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
				N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminImportationSchemesDeleteConfirm());
				view.getImportationSchemeModelsStore().remove(model);
				
				if(model.equals(currentImportationSchemeModel)) {
					clearCurrentImportationSchemeModel();
				}
			}
		}, 
			new LoadingMask(view.getImportationSchemeModelsGrid()), 
			new LoadingMask(view.getVariableFlexibleElementsGrid()));
	}
	
	private void deleteVariableFlexibleElements(final List<VariableFlexibleElementDTO> variables) {
		final ArrayList<Integer> ids = new ArrayList<Integer>();
		for(final VariableFlexibleElementDTO variable : variables) {
			ids.add(variable.getId());
		}
		
		dispatch.execute(new DeleteImportationSchemeModels(null, ids), new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
				N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminVariableDeleteConfirm());
				
				loadImportationSchemeModel();
			}
		}, new LoadingMask(view.getVariableFlexibleElementsGrid()));
	}
	
	private void displayImportationSchemeModel(ImportationSchemeModelDTO importationSchemeModel) {
		currentImportationSchemeModel = importationSchemeModel;

		view.getVariableFlexibleElementsGrid().show();
		view.getVariableFlexibleElementStore().removeAll();
		view.getVariableFlexibleElementStore().add(importationSchemeModel.getVariableFlexibleElementsDTO());
		view.getVariableFlexibleElementStore().commitChanges();
		view.getAddVariableFlexibleElementButton().enable();

		if (currentImportationSchemeModel.getIdKey() == null) {
			 eventBus.navigateRequest(Page.ADMIN_ADD_IMPORTATION_SCHEME_MODEL_MATCHING_RULE.request()
				.addData(RequestParameter.MODEL, currentModel)
				.addData(RequestParameter.IMPORTATION_SCHEME_MODEL, importationSchemeModel)
				.addData(RequestParameter.FOR_KEY, true));
		}
	}
}
