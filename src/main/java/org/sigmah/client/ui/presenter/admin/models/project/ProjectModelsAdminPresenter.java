package org.sigmah.client.ui.presenter.admin.models.project;

import java.util.ArrayList;
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
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.models.FlexibleElementsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.base.AbstractModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.view.admin.models.project.ProjectModelsAdminView;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetProjectModel;
import org.sigmah.shared.command.GetProjectModelCopy;
import org.sigmah.shared.command.GetProjectModels;
import org.sigmah.shared.command.GetProjectsByModel;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Admin project models Presenter which manages {@link ProjectModelsAdminView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class ProjectModelsAdminPresenter extends AbstractModelsAdminPresenter<ProjectModelDTO, ProjectModelsAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectModelsAdminView.class)
	public static interface View extends AbstractModelsAdminPresenter.View<ProjectModelDTO> {

		void setProjectTypeProvider(ProjectTypeProvider provider);

		Field<String> getNameField();

		Field<ProjectModelType> getProjectModelTypeField();

	}

	public static interface ProjectTypeProvider {

		/**
		 * Returns the given {@code projectModel} corresponding {@link ProjectModelType}.
		 * 
		 * @param projectModel
		 *          The project model.
		 * @return The given {@code projectModel} corresponding {@link ProjectModelType}, or {@code null}.
		 */
		ProjectModelType getProjectModelType(ProjectModelDTO projectModel);

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
	 * @param phaseModelsAdminPresenterProvider
	 *          The {@link PhaseModelsAdminPresenter} provider.
	 * @param logFrameModelsAdminPresenterProvider
	 *          The {@link LogFrameModelsAdminPresenter} provider.
	 * @param importationSchemeModelsAdminPresenterProvider
	 *			The {@link ImportationSchemeModelsAdminPresenter} provider.
	 */
	@Inject
	protected ProjectModelsAdminPresenter(final View view, final Injector injector, final Provider<FlexibleElementsAdminPresenter<ProjectModelDTO>> flexibleElementsProvider, final Provider<PhaseModelsAdminPresenter> phaseModelsAdminPresenterProvider, final Provider<LogFrameModelsAdminPresenter> logFrameModelsAdminPresenterProvider, final Provider<ImportationSchemeModelsAdminPresenter<ProjectModelDTO>> importationSchemeModelsAdminPresenterProvider) {
		super(view, injector, flexibleElementsProvider.get(), phaseModelsAdminPresenterProvider.get(), logFrameModelsAdminPresenterProvider.get(),
			importationSchemeModelsAdminPresenterProvider.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_PROJECTS_MODELS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Parent initialization: MANDATORY!
		super.onBind();

		// ProjectModelType provider implementation.
		view.setProjectTypeProvider(new ProjectTypeProvider() {

			@Override
			public ProjectModelType getProjectModelType(final ProjectModelDTO projectModel) {
				return projectModel.getVisibility(auth().getOrganizationId());
			}
		});

		// On phase model creation/update event.
		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {
				if (event.concern(UpdateEvent.PHASE_MODEL_UPDATE)) {
					final ProjectModelDTO projectModel = event.getParam(0);
					updateModel(projectModel);
				}
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getUpdateEventKey() {
		return UpdateEvent.PROJECT_MODEL_ADD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Page getAddModelPage() {
		return Page.ADMIN_ADD_PROJECT_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getModelToImport() {
		return AdminUtil.ADMIN_PROJECT_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command<ListResult<ProjectModelDTO>> getLoadModelsCommand() {
		return new GetProjectModels(ProjectModelDTO.Mode.WITH_VISIBILITIES, ProjectModelStatus.values());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command<ProjectModelDTO> getLoadModelCommand(final Integer modelId) {
		return new GetProjectModel(modelId, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStatusChangeEvent(final Integer modelId, final ProjectModelStatus currentStatus, final ProjectModelStatus targetStatus,
			final AsyncCallback<Void> callback) {

		if (currentStatus == ProjectModelStatus.DRAFT) {

			N10N.confirmation(I18N.MESSAGES.adminModelStatusChangeBox(), I18N.MESSAGES.adminModelDraftStatusChange(ProjectModelStatus.getName(targetStatus)),
				new ConfirmCallback() {

					/**
					 * On OK.
					 */
					@Override
					public void onAction() {

						dispatch.execute(new GetProjectsByModel(modelId, ProjectDTO.Mode.BASE), new CommandResultHandler<ListResult<ProjectDTO>>() {

							@Override
							public void onCommandFailure(final Throwable caught) {
								N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.serverError());
								callback.onFailure(null);
							}

							@Override
							public void onCommandSuccess(final ListResult<ProjectDTO> result) {

								if (result == null || result.isEmpty()) {
									// Change event is allowed.
									return;
								}

								final List<String> testProjectNames = new ArrayList<String>();
								for (final ProjectDTO testProject : result.getList()) {
									testProjectNames.add(testProject.getName());
								}

								final String confirmMessage = I18N.MESSAGES.DraftProjectModelChangeStatusDetails(ProjectModelStatus.getName(targetStatus));

								N10N.confirmation(I18N.CONSTANTS.projectChangeStatus(), confirmMessage, testProjectNames, (ConfirmCallback) null, new ConfirmCallback() {

									// On OK, change event is allowed.

									/**
									 * On CANCEL.
									 */
									@Override
									public void onAction() {
										callback.onFailure(null);
									}
								});
							}
						}, new LoadingMask(view.getHeaderForm()));
					}

				}, new ConfirmCallback() {

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
	protected void onSaveAction(final ProjectModelDTO currentModel, final AsyncCallback<ProjectModelDTO> callback) {

		final String name = view.getNameField().getValue();
		final EnumModel<ProjectModelStatus> statusModel = view.getHeaderStatusField().getValue();

		final Map<String, Object> modelProperties = new HashMap<String, Object>();
		modelProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, currentModel);
		modelProperties.put(AdminUtil.PROP_PM_NAME, name);
		modelProperties.put(AdminUtil.PROP_PM_STATUS, EnumModel.getEnum(statusModel));
		modelProperties.put(AdminUtil.PROP_PM_USE, view.getProjectModelTypeField().getValue());

		dispatch.execute(new CreateEntity(ProjectModelDTO.ENTITY_NAME, modelProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminProjectModelUpdateBox(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminProjectModelStandard() + " '" + name + "'"));
				callback.onFailure(caught);
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				if (result == null || result.getEntity() == null) {
					N10N.warn(I18N.CONSTANTS.adminProjectModelUpdateBox(),
						I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminProjectModelStandard() + " '" + name + "'"));
					return;
				}

				final ProjectModelDTO updatedModel = (ProjectModelDTO) result.getEntity();

				callback.onSuccess(updatedModel);

				N10N.infoNotif(I18N.CONSTANTS.adminProjectModelUpdateBox(),
					I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminProjectModelStandard() + " '" + name + "'"));
			}
		}, view.getHeaderSaveButton(), new LoadingMask(view.getHeaderForm()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDuplicateAction(final ProjectModelDTO model) {

		dispatch.execute(new GetProjectModelCopy(model.getId(), I18N.MESSAGES.copyOf(model.getName()), ProjectModelDTO.Mode.WITH_VISIBILITIES),
			new CommandResultHandler<ProjectModelDTO>() {

				@Override
				public void onCommandFailure(final Throwable caught) {
					N10N.error(I18N.CONSTANTS.adminProjectModelCopy(), I18N.CONSTANTS.adminProjectModelCopyError());
				}

				@Override
				public void onCommandSuccess(final ProjectModelDTO result) {
					if (result == null) {
						return;
					}

					view.getStore().add(result);
					view.getStore().commitChanges();

					// Selects the model in the grid.
					view.getGrid().getSelectionModel().select(result, false);

					// Shows notification.
					N10N.infoNotif(I18N.CONSTANTS.adminProjectModelCopy(), I18N.CONSTANTS.adminProjectModelCopyDetail());
				}
			}, view.getGridDuplicateButton(), view.getGridMask());
	}

}
