package org.sigmah.client.ui.presenter.admin.models.orgunit;

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

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.models.FlexibleElementsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.base.AbstractModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.LayoutGroupAdminPresenter;
import org.sigmah.client.ui.view.admin.models.orgunit.OrgUnitModelsAdminView;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetOrgUnitModel;
import org.sigmah.shared.command.GetOrgUnitModelCopy;
import org.sigmah.shared.command.GetOrgUnitModels;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Admin OrgUnit models Presenter which manages {@link OrgUnitModelsAdminView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 */
@Singleton
public class OrgUnitModelsAdminPresenter extends AbstractModelsAdminPresenter<OrgUnitModelDTO, OrgUnitModelsAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(OrgUnitModelsAdminView.class)
	public static interface View extends AbstractModelsAdminPresenter.View<OrgUnitModelDTO> {

		Field<String> getNameField();

		Field<String> getTypeField();

		Field<Boolean> getHasBudgetField();

		Field<Boolean> getCanContainProjectsField();

	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 * @param flexibleElementsProvider
	 *          The {@link FlexibleElementsAdminPresenter} provider.
     	 * @param layoutGroupAdminPresenterProvider
     	 *          The {@link layoutGroupAdminPresenterProvider} provider.
     	 * @param importationSchemeModelsAdminPresenterProvider
     	 *          The {@link importationSchemeModelsAdminPresenterProvider} provider.
	 */
	@Inject
	protected OrgUnitModelsAdminPresenter(final View view, final Injector injector, final Provider<FlexibleElementsAdminPresenter<OrgUnitModelDTO>> flexibleElementsProvider,  final Provider<LayoutGroupAdminPresenter<OrgUnitModelDTO>> layoutGroupAdminPresenterProvider, final Provider<ImportationSchemeModelsAdminPresenter<OrgUnitModelDTO>> importationSchemeModelsAdminPresenterProvider) {
 		super(view, injector, flexibleElementsProvider.get(), layoutGroupAdminPresenterProvider.get(), importationSchemeModelsAdminPresenterProvider.get());
  	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_ORG_UNITS_MODELS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Parent initialization: MANDATORY!
		super.onBind();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getUpdateEventKey() {
		return UpdateEvent.ORG_UNIT_MODEL_ADD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Page getAddModelPage() {
		return Page.ADMIN_ADD_ORG_UNIT_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getModelToImport() {
		return AdminUtil.ADMIN_ORG_UNIT_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command<ListResult<OrgUnitModelDTO>> getLoadModelsCommand() {
		return new GetOrgUnitModels(OrgUnitModelDTO.Mode.BASE, ProjectModelStatus.values());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command<OrgUnitModelDTO> getLoadModelCommand(final Integer modelId) {
		return new GetOrgUnitModel(modelId, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStatusChangeEvent(final Integer modelId, final ProjectModelStatus currentStatus, final ProjectModelStatus targetStatus,
			final AsyncCallback<Void> callback) {

		if (currentStatus == ProjectModelStatus.DRAFT) {
			// The "DRAFT" project model needs to be checked again.
			N10N.confirmation(I18N.MESSAGES.adminModelStatusChangeBox(), I18N.MESSAGES.adminModelDraftStatusChange(ProjectModelStatus.getName(targetStatus)),
				(ConfirmCallback) null, new ConfirmCallback() {

					/**
					 * On CANCEL.
					 */
					@Override
					public void onAction() {
						callback.onFailure(null);
					}
				});

		} else if (currentStatus == ProjectModelStatus.UNAVAILABLE) {
			// "UNAVAILABLE" model needs to be checked in a different way.
			// isValidUnavailableStatusChange(ProjectModelStatus.getStatus(view.getStatusList().getValue().getValue()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onSaveAction(final OrgUnitModelDTO currentModel, final AsyncCallback<OrgUnitModelDTO> callback) {

		final String nameValue = view.getNameField().getValue();
		final String title = view.getTypeField().getValue();
		final Boolean hasBudget = view.getHasBudgetField().getValue();
		final Boolean containsProjects = view.getCanContainProjectsField().getValue();
		final EnumModel<ProjectModelStatus> statusModel = view.getHeaderStatusField().getValue();
		final ProjectModelStatus status = statusModel != null ? statusModel.getEnum() : null;

		final Map<String, Object> modelProperties = new HashMap<String, Object>();
		modelProperties.put(AdminUtil.PROP_OM_NAME, view.getNameField().getValue());
		modelProperties.put(AdminUtil.PROP_OM_STATUS, status);
		modelProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, currentModel);
		modelProperties.put(AdminUtil.PROP_OM_NAME, nameValue);
		modelProperties.put(AdminUtil.PROP_OM_TITLE, title);
		modelProperties.put(AdminUtil.PROP_OM_HAS_BUDGET, hasBudget);
		modelProperties.put(AdminUtil.PROP_OM_CONTAINS_PROJECTS, containsProjects);
		modelProperties.put(AdminUtil.PROP_OM_MAINTENANCE_DATE, getMaintenanceDate());

		dispatch.execute(new CreateEntity(OrgUnitModelDTO.ENTITY_NAME, modelProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminOrgUnitsModelStandard() + " '" + view.getNameField().getValue() + "'"));
				callback.onFailure(caught);
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				if (result == null || result.getEntity() == null) {
					N10N.warn(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(),
						I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminOrgUnitsModelStandard() + " '" + view.getNameField().getValue() + "'"));
					return;
				}

				final OrgUnitModelDTO updatedOrgUnitModel = (OrgUnitModelDTO) result.getEntity();

				callback.onSuccess(updatedOrgUnitModel);

				N10N.infoNotif(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(),
					I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminOrgUnitsModelStandard() + " '" + view.getNameField().getValue() + "'"));
			}
		}, view.getHeaderSaveButton(), new LoadingMask(view.getHeaderForm()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDuplicateAction(final OrgUnitModelDTO model) {

		dispatch.execute(new GetOrgUnitModelCopy(model.getId(), I18N.MESSAGES.copyOf(model.getName()), OrgUnitModelDTO.Mode.BASE),
			new CommandResultHandler<OrgUnitModelDTO>() {

				@Override
				public void onCommandFailure(final Throwable caught) {
					N10N.error(I18N.CONSTANTS.adminOrgUnitsModelCopy(), I18N.CONSTANTS.adminOrgUnitsModelCopyError());
				}

				@Override
				public void onCommandSuccess(final OrgUnitModelDTO result) {
					if (result == null) {
						return;
					}

					view.getStore().add(result);
					view.getStore().commitChanges();

					// Selects the model in the grid.
					view.getGrid().getSelectionModel().select(result, false);

					// Shows notification.
					N10N.infoNotif(I18N.CONSTANTS.adminOrgUnitsModelCopy(), I18N.CONSTANTS.adminOrgUnitsModelCopyDetail());
				}
			}, view.getGridDuplicateButton(), view.getGridMask());
	}

}
