package org.sigmah.client.ui.presenter.admin.models.project;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.models.base.IsModelTabPresenter;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.admin.models.project.PhaseModelsAdminView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.HasGrid;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Phase models administration presenter that manages the {@link PhaseModelsAdminView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PhaseModelsAdminPresenter extends AbstractPresenter<PhaseModelsAdminPresenter.View>
																																																implements
																																																IsModelTabPresenter<ProjectModelDTO, PhaseModelsAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(PhaseModelsAdminView.class)
	public static interface View extends ViewInterface, HasGrid<PhaseModelDTO> {

		Button getAddButton();

		Button getDeleteButton();

		void setToolbarEnabled(boolean enabled);

	}

	/**
	 * The provided current model.
	 */
	private ProjectModelDTO currentModel;

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view managed by this presenter.
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	public PhaseModelsAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Grid events handler.
		// --

		view.setGridEventHandler(new GridEventHandler<PhaseModelDTO>() {

			@Override
			public void onRowClickEvent(final PhaseModelDTO rowElement) {
				eventBus.navigateRequest(Page.ADMIN_EDIT_PHASE_MODEL.request().addData(RequestParameter.DTO, rowElement)
					.addData(RequestParameter.CONTENT, view.getStore().getModels()).addData(RequestParameter.MODEL, currentModel));
			}
		});

		// --
		// Grid selection change handler.
		// --

		view.getGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<PhaseModelDTO>() {

			@Override
			public void selectionChanged(final SelectionChangedEvent<PhaseModelDTO> event) {
				final boolean singleSelection = ClientUtils.isNotEmpty(event.getSelection()) && event.getSelection().size() == 1;
				view.getDeleteButton().setEnabled(singleSelection && currentModel.isEditable());
			}
		});

		// --
		// Add button handler.
		// --

		view.getAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				eventBus.navigateRequest(Page.ADMIN_EDIT_PHASE_MODEL.request().addData(RequestParameter.MODEL, currentModel));
			}
		});

		// --
		// Delete button handler.
		// --

		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onDeleteAction(view.getGrid().getSelectionModel().getSelectedItem());
			}
		});

		// --
		// On phase model creation/update event.
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {
				if (event.concern(UpdateEvent.PHASE_MODEL_UPDATE)) {
					final ProjectModelDTO projectModel = event.getParam(0);
					loadTab(projectModel);
				}
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.adminProjectModelPhases();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadTab(final ProjectModelDTO model) {

		this.currentModel = model;
		view.setToolbarEnabled(currentModel.isEditable());

		// --
		// Populating the store and initializing phase models 'root' property.
		// --

		view.getStore().removeAll();

		final PhaseModelDTO rootPhase = model.getRootPhaseModel();
		for (final PhaseModelDTO phaseModel : model.getPhaseModels()) {
			phaseModel.setRoot(rootPhase != null && rootPhase.equals(phaseModel));
			view.getStore().add(phaseModel);
		}

		view.getStore().commitChanges();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasValueChanged() {
		return false;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Callback executed on delete button action.
	 * 
	 * @param phaseModel
	 *          The selected phase model to delete.
	 */
	private void onDeleteAction(final PhaseModelDTO phaseModel) {

		if (currentModel == null || phaseModel == null) {
			return;
		}

		// Only a phase model within a project model in 'DRAFT' status is allowed to be deleted.
		if (!currentModel.isEditable()) {
			return;
		}

		// Cannot delete root phase model.
		if (currentModel.getRootPhaseModel() != null) {
			if (currentModel.getRootPhaseModel().getId().equals(phaseModel.getId())) {
				N10N.warn(I18N.CONSTANTS.deletionError(), I18N.CONSTANTS.deleteRootPhaseModelError());
				return;
			}
		}

		final String confirmMessageDetials;
		if (currentModel.getPhaseModels() == null || currentModel.getPhaseModels().size() == 1) {
			// Cannot delete the last phase model.
			confirmMessageDetials = I18N.CONSTANTS.deleteRootPhaseModelConfirm();
		} else {
			confirmMessageDetials = I18N.CONSTANTS.deletePhaseModelConfirm();
		}

		N10N.confirmation(I18N.CONSTANTS.deleteConfirm(), confirmMessageDetials, new ConfirmCallback() {

			@Override
			public void onAction() {

				dispatch.execute(new Delete(PhaseModelDTO.ENTITY_NAME, phaseModel.getId()), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						N10N.error(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(phaseModel.getName()));
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {

						// Updates the phase models store.
						updateStore(phaseModel);

						// Updates the current project model.
						currentModel.getPhaseModels().clear();
						currentModel.getPhaseModels().addAll(view.getStore().getModels());

						// Shows notification.
						N10N.infoNotif(I18N.CONSTANTS.deleteConfirm(), I18N.CONSTANTS.adminPhaseModelDeleteDetail());
					}
				});
			}
		});
	}

	/**
	 * Updates the phase models store after a model is deleted.<br>
	 * Removes the given deleted {@code phaseModel} and updates the entire store's successors.
	 * 
	 * @param phaseModelDeleted
	 *          The deleted phase model.
	 */
	private void updateStore(final PhaseModelDTO phaseModelDeleted) {

		view.getStore().remove(phaseModelDeleted);

		// Check if the deleted model is other phase models' successors,if so, delete that relation
		for (final PhaseModelDTO phaseModel : view.getStore().getModels()) {

			if (ClientUtils.isEmpty(phaseModel.getSuccessors())) {
				continue;
			}

			if (phaseModel.getSuccessors().contains(phaseModelDeleted)) {
				phaseModel.getSuccessors().remove(phaseModelDeleted);
				view.getStore().update(phaseModel);
			}
		}

		view.getStore().commitChanges();
	}

}
