package org.sigmah.client.ui.presenter.admin.models.project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.view.admin.models.project.EditPhaseModelAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin create/edit phase model which manages {@link EditPhaseModelAdminView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class EditPhaseModelAdminPresenter extends AbstractPagePresenter<EditPhaseModelAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(EditPhaseModelAdminView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		Field<String> getNameField();

		Field<Boolean> getRootField();

		Field<Number> getDisplayOrderField();

		Field<String> getGuideField();

		Button getSaveButton();

		void clearSuccessors();
		
		void addSuccessor(PhaseModelDTO successor, boolean selected);

		List<PhaseModelDTO> getSelectedSuccessors();

	}

	/**
	 * The parent project model.<br>
	 * Should never be {@code null}.
	 */
	private ProjectModelDTO parentProjectModel;

	/**
	 * The edited phase model.<br>
	 * Set to {@code null} in case of creation.
	 */
	private PhaseModelDTO phaseModelUpdate;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected EditPhaseModelAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_EDIT_PHASE_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel[] getForms() {
		return new FormPanel[] { view.getForm()
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onSaveAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		parentProjectModel = request.getData(RequestParameter.MODEL);
		phaseModelUpdate = request.getData(RequestParameter.DTO);

		if (parentProjectModel == null) {
			hideView();
			throw new IllegalArgumentException("Invalid parent project model.");
		}

		// The existing phase models list (used to manage successors).
		final List<PhaseModelDTO> phaseModels = request.getData(RequestParameter.CONTENT);

		view.getForm().clearAll();
		view.clearSuccessors();

		setPageTitle(phaseModelUpdate == null ? I18N.CONSTANTS.adminPhaseAdd() : I18N.CONSTANTS.adminPhaseEdit());
		view.getSaveButton().setText(phaseModelUpdate == null ? I18N.CONSTANTS.adminOrgUnitCreateButton() : I18N.CONSTANTS.edit());

		// --
		// Form loading (in case of edition only).
		// --

		if (phaseModelUpdate != null) {
			view.getNameField().setValue(phaseModelUpdate.getName());
			view.getRootField().setValue(phaseModelUpdate.getRoot());
			view.getDisplayOrderField().setValue(phaseModelUpdate.getDisplayOrder());
			view.getGuideField().setValue(phaseModelUpdate.getGuide());
		} else {
			view.getDisplayOrderField().setValue(displayOrderForANewPhase());
		}

		// --
		// Potential successors initialization.
		// --

		if (phaseModels != null) {

			// Collects all successors.
			final Set<PhaseModelDTO> allSuccessors = new HashSet<PhaseModelDTO>();

			for (final PhaseModelDTO phaseModel : phaseModels) {
				if (phaseModel.getSuccessors() != null) {
					allSuccessors.addAll(phaseModel.getSuccessors());
				}
			}

			// Initializes potential successors.
			for (final PhaseModelDTO phaseModel : phaseModels) {
				if (iValidSuccessor(phaseModel, phaseModelUpdate, allSuccessors)) {
					view.addSuccessor(phaseModel, phaseModelUpdate != null ? phaseModelUpdate.getSuccessors().contains(phaseModel) : false);
				}
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Returns if the given {@code phaseModel} is a valid potential successor.
	 * 
	 * @param phaseModel
	 *          The phase model that may be a valid potential successor.
	 * @param phaseModelUpdate
	 *          The edited phase model that may contain {@code phaseModel} among its successors. May be {@code null}.
	 * @param allSuccessors
	 *          All the phase models successors.
	 * @return {@code true} if the given {@code phaseModel} is a valid potential successor, {@code false} otherwise.
	 */
	private static boolean iValidSuccessor(final PhaseModelDTO phaseModel, final PhaseModelDTO phaseModelUpdate, final Set<PhaseModelDTO> allSuccessors) {

		if (phaseModel == null) {
			return false;
		}

		if (phaseModelUpdate != null) {

			if (ClientUtils.isNotEmpty(phaseModelUpdate.getSuccessors()) && phaseModelUpdate.getSuccessors().contains(phaseModel)) {
				// Already a successor of the updated phase model.
				return true;

			} else {
				// Not the current updated phase model, not a root phase and not a successor yet.
				return !phaseModelUpdate.equals(phaseModel) && ClientUtils.isNotTrue(phaseModel.getRoot()) && !allSuccessors.contains(phaseModel);
			}

		} else {
			// Not a root phase and not a successor yet.
			return ClientUtils.isNotTrue(phaseModel.getRoot()) && !allSuccessors.contains(phaseModel);
		}
	}

	/**
	 * Find the highest current display order and return this value + 1.
	 * 
	 * @return Default display order of a new phase.
	 */
	private int displayOrderForANewPhase() {
		int highestDisplayOrder = 0;
		for (final PhaseModelDTO phaseModel : parentProjectModel.getPhaseModels()) {
			final Integer phaseDisplayOrder = phaseModel.getDisplayOrder();
			if (phaseDisplayOrder != null && phaseDisplayOrder > highestDisplayOrder) {
				highestDisplayOrder = phaseDisplayOrder;
			}
		}
		return highestDisplayOrder + 1;
	}

	/**
	 * Callback executed on save button action.
	 */
	private void onSaveAction() {

		if (!view.getForm().isValid()) {
			return;
		}

		final String name = view.getNameField().getValue();
		final Boolean root = view.getRootField().getValue();
		final Number displayOrder = view.getDisplayOrderField().getValue();
		final String guide = view.getGuideField().getValue();

		final PhaseModelDTO phaseToSave = new PhaseModelDTO();
		phaseToSave.setId(phaseModelUpdate != null ? phaseModelUpdate.getId() : null);
		phaseToSave.setName(name);
		phaseToSave.setSuccessors(view.getSelectedSuccessors());

		final Map<String, Object> newPhaseProperties = new HashMap<String, Object>();
		newPhaseProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, parentProjectModel);
		newPhaseProperties.put(AdminUtil.PROP_PHASE_MODEL, phaseToSave);
		newPhaseProperties.put(AdminUtil.PROP_PHASE_ORDER, displayOrder != null ? displayOrder.intValue() : null);
		newPhaseProperties.put(AdminUtil.PROP_PHASE_ROOT, root);
		newPhaseProperties.put(AdminUtil.PROP_PHASE_GUIDE, guide);

		// Use 'CreateEntity' to obtain a result.
		dispatch.execute(new CreateEntity(ProjectModelDTO.ENTITY_NAME, newPhaseProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminPhaseCreationBox(), I18N.MESSAGES.adminStandardCreationFailureF(I18N.MESSAGES.adminStandardPhase() + " '" + name + "'"));
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				if (result == null) {
					N10N.warn(I18N.CONSTANTS.adminPhaseCreationBox(), I18N.MESSAGES.adminStandardCreationNullF(I18N.MESSAGES.adminStandardPhase() + " '" + name + "'"));
					return;
				}

				final ProjectModelDTO projectModelUpdated = (ProjectModelDTO) result.getEntity();

				if (phaseModelUpdate != null) {
					// Update case.
					N10N.infoNotif(I18N.CONSTANTS.adminPhaseCreationBox(),
						I18N.MESSAGES.adminStandardUpdateSuccessF(I18N.MESSAGES.adminStandardPhase() + " '" + name + "'"));

				} else {
					// Creation case.
					N10N.infoNotif(I18N.CONSTANTS.adminPhaseCreationBox(),
						I18N.MESSAGES.adminStandardCreationSuccessF(I18N.MESSAGES.adminStandardPhase() + " '" + name + "'"));
				}

				// Sends an update event to notify registered elements.
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.PHASE_MODEL_UPDATE, projectModelUpdated));

				hideView();
			}

		}, view.getSaveButton());
	}

}
