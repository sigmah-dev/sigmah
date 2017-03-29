package org.sigmah.client.ui.presenter.admin.models.importer;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.admin.models.importer.AddImportationSchemeModelsAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

public class AddImportationSchemeModelsAdminPresenter extends AbstractPagePresenter<AddImportationSchemeModelsAdminPresenter.View> {

	private EntityDTO<Integer> model;

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends ViewPopupInterface {

		ComboBox<ImportationSchemeDTO> getSchemasCombo();

		FormPanel getMainPanel();

		Button getSubmitButton();

		ListStore<ImportationSchemeDTO> getSchemasStore();

		void clearForm();

	}

	public AddImportationSchemeModelsAdminPresenter(View view, ClientFactory factory) {
		super(view, factory);
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

				// Validate Form

				if (!view.getMainPanel().isValid()) {

					N10N.message(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminImportationScheme()),
						MessageType.INFO);
					return;

				}

				// Create the Command

				Map<String, Object> newImportationSchemeModelProperties = new HashMap<String, Object>();
				newImportationSchemeModelProperties.put(AdminUtil.ADMIN_SCHEMA, view.getSchemasCombo().getValue());

				if (model instanceof ProjectModelDTO) {
					newImportationSchemeModelProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, model);
				} else {
					newImportationSchemeModelProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, model);
				}

				newImportationSchemeModelProperties.put(AdminUtil.ADMIN_IMPORTATION_SCHEME_MODEL, new ImportationSchemeModelDTO());
				CreateEntity cmd = new CreateEntity(ImportationSchemeModelDTO.ENTITY_NAME, newImportationSchemeModelProperties);

				// run the command

				dispatch.execute(cmd, new CommandResultHandler<CreateResult>() {

					@Override
					protected void onCommandSuccess(CreateResult result) {

						// update Importation Scheme Store

						eventBus.fireEvent(new UpdateEvent(UpdateEvent.IMPORTATION_SCHEME_MODEL_UPDATE, model, result.getEntity()));

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
