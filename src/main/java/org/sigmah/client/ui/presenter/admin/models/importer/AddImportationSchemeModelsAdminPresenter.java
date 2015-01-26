package org.sigmah.client.ui.presenter.admin.models.importer;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.admin.models.importer.AddImportationSchemeModelsAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

@Singleton
public class AddImportationSchemeModelsAdminPresenter extends AbstractPagePresenter<AddImportationSchemeModelsAdminPresenter.View> {

	private EntityDTO<Integer> model;

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(AddImportationSchemeModelsAdminView.class)
	public static interface View extends ViewPopupInterface {

		ComboBox<ImportationSchemeDTO> getSchemasCombo();

		ContentPanel getMainPanel();

		Button getSubmitButton();

		ListStore<ImportationSchemeDTO> getSchemasStore();

		void clearForm();

	}

	@Inject
	protected AddImportationSchemeModelsAdminPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public Page getPage() {

		return Page.ADMIN_ADD_IMPORTATION_SCHEME_MODEL;

	}

	@Override
	public void onPageRequest(PageRequest request) {

		model = request.getData(RequestParameter.DTO);

		view.clearForm();

		LoadImportationScheme(model);

		setPageTitle(I18N.CONSTANTS.adminAddImportationSchemeModel());

	}

	@Override
	public void onBind() {

		view.getSubmitButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				CreateEntity cmd = new CreateEntity();

				dispatch.execute(cmd, new CommandResultHandler<CreateResult>() {

					@Override
					protected void onCommandSuccess(CreateResult result) {

						// update Importation Scheme Store

						/*
						 * final ImportationSchemeModelDTO schemaDTOUpdated = (ImportationSchemeModelDTO) result.getEntity();
						 * getImportationSchemeModelsStore().add(schemaDTOUpdated);
						 * getImportationSchemeModelsStore().commitChanges(); currentImportationSchemeModelDTO = schemaDTOUpdated;
						 * getVariableFlexibleElementsStore().removeAll(); getVariableFlexibleElementsStore().clearFilters();
						 */

						// add variable
						eventBus.navigateRequest(Page.ADMIN_ADD_IMPORTATION_SCHEME_MODEL_MATCHING_RULE.request().addData(RequestParameter.DTO, model));

						// hide PopUp

						hideView();

					}

					@Override
					protected void onCommandFailure(Throwable caught) {

						hideView();

					}

				}, new LoadingMask(view.getMainPanel()));

			}

		});

	}

	/**
	 * Loading Importation Scheme how have Variables
	 * 
	 * @param model
	 */
	public void LoadImportationScheme(EntityDTO<Integer> model) {

		GetImportationSchemes cmd = new GetImportationSchemes();

		cmd.setExcludeExistent(true);

		if (model instanceof OrgUnitDTO) {

			cmd.setOrgUnitModelId(model.getId());

		} else {

			cmd.setProjectModelId(model.getId());

		}

		dispatch.execute(cmd, new CommandResultHandler<ListResult<ImportationSchemeDTO>>() {

			@Override
			public void onCommandSuccess(ListResult<ImportationSchemeDTO> result) {

				view.getSchemasStore().removeAll();

				if (result.getList() != null && !result.getList().isEmpty()) {

					for (ImportationSchemeDTO importationScheme : result.getList()) {

						if (importationScheme.getVariables().size() > 0) {
							view.getSchemasStore().add(importationScheme);
						}
					}

					view.getSchemasStore().commitChanges();

				} else {

					N10N.message(I18N.CONSTANTS.adminAddImportationSchemeModel(), "No importation schemes available.", MessageType.INFO);
					hideView();
				}
			}

			@Override
			protected void onCommandFailure(Throwable caught) {
				hideView();
			}

		}, new LoadingMask(view.getMainPanel()));

	}

}
