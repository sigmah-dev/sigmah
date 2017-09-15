package org.sigmah.client.ui.presenter;

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


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.CreateProjectView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.GetProjectModels;
import org.sigmah.shared.command.GetTestProjects;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Create project presenter.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class CreateProjectPresenter extends AbstractPagePresenter<CreateProjectPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(CreateProjectView.class)
	public static interface View extends ViewInterface {

		void setTitle(String title);

		FormPanel getFormPanel();

		TextField<String> getNameField();

		TextField<String> getFullNameField();

		NumberField getBudgetField();

		ComboBox<OrgUnitDTO> getOrgUnitsField();

		ComboBox<ProjectModelDTO> getModelsField();

		void setProjectModelType(ProjectModelType type);

		LabelField getBaseProjectBudgetField();
		
		NumberField getAmountField();

		LabelField getPercentageField();

		Button getCreateButton();

		void setDeleteTestProjectAction(DeleteTestProjectAction deleteTestProjectAction);

		Grid<ProjectDTO> getTestProjectsField();

	}

	/**
	 * Defines the different modes for the creation.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	public static enum Mode {

		/**
		 * Create project mode.
		 */
		PROJECT,

		/**
		 * Create a project which is funding the base project (must be passed through the page request).
		 */
		FUNDING_ANOTHER_PROJECT,

		/**
		 * Create a project which is funded by the base project (must be passed through the page request).
		 */
		FUNDED_BY_ANOTHER_PROJECT,

		/**
		 * Create a test project.
		 */
		TEST_PROJECT;

		private static final Mode DEFAULT_MODE = PROJECT;

		private static Mode fromRequestParameterValue(String param) {

			if (ClientUtils.isNotBlank(param)) {
				for (final Mode m : Mode.values()) {
					if (param.equalsIgnoreCase(m.name())) {
						return m;
					}
				}
			}

			return null;

		}

	}

	/**
	 * The action to delete a test project. This action is passed to the view.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	public static interface DeleteTestProjectAction {

		/**
		 * Deletes the given test project.
		 * 
		 * @param testProject
		 *          The test project.
		 * @param loadable
		 *          The corresponding {@link Loadable} component.
		 */
		void deleteTestProject(ProjectDTO testProject, Loadable loadable);

	}

	// Utility constants.
	private static final double ZERO = 0.0d;
	private static final String ZERO_PERCENT = "0 %";

	/**
	 * The current creation mode.
	 */
	private Mode currentMode;

	/**
	 * The base project for funding / funded case.
	 */
	private ProjectDTO baseProject;

	@Inject
	public CreateProjectPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.CREATE_PROJECT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Project models store listeners.
		view.getModelsField().getStore().addStoreListener(new StoreListener<ProjectModelDTO>() {

			@Override
			public void storeClear(StoreEvent<ProjectModelDTO> se) {
				view.getModelsField().setEnabled(false);
			}

			@Override
			public void storeAdd(StoreEvent<ProjectModelDTO> se) {
				view.getModelsField().setEnabled(true);
			}

		});

		// Org unit models store listeners.
		view.getOrgUnitsField().getStore().addStoreListener(new StoreListener<OrgUnitDTO>() {

			@Override
			public void storeClear(StoreEvent<OrgUnitDTO> se) {
				view.getOrgUnitsField().setEnabled(false);
			}

			@Override
			public void storeAdd(StoreEvent<OrgUnitDTO> se) {
				view.getOrgUnitsField().setEnabled(true);
			}

		});

		// Project models field listener.
		view.getModelsField().addSelectionChangedListener(new SelectionChangedListener<ProjectModelDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ProjectModelDTO> se) {

				final ProjectModelType type;
				if (se != null && se.getSelectedItem() != null) {
					type = se.getSelectedItem().getVisibility(injector.getAuthenticationProvider().get().getOrganizationId());
				} else {
					type = null;
				}

				view.setProjectModelType(type);

			}

		});

		// Amount field listener.
		view.getAmountField().addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				final NumberField budgetField = view.getBudgetField();
				final NumberField amountField = view.getAmountField();
				final LabelField percentageField = view.getPercentageField();

				// Checks that values are filled.
				if (amountField.getValue() == null || amountField.getValue().doubleValue() < ZERO) {
					amountField.setValue(ZERO);
				}
				if (budgetField.getValue() == null || budgetField.getValue().doubleValue() < ZERO) {
					budgetField.setValue(ZERO);
					amountField.setValue(ZERO);
				}

				// If there is no base project, the percentage cannot be computed.
				if (baseProject == null) {
					percentageField.setValue(I18N.CONSTANTS.createProjectPercentageNotAvailable());
					return;
				}

				// --
				// -- Computes the ratio between the amount and the base project.
				// --

				final double baseBudget = baseProject.getPlannedBudget() != null ? baseProject.getPlannedBudget().doubleValue() : ZERO;
				final double budget = budgetField.getValue().doubleValue();
				final double amountBudget = amountField.getValue().doubleValue();
				final double minBudget = Math.min(baseBudget, budget);

				switch (currentMode) {

				// --
				// -- CASE: the created project is funded by the base project.
				// --
					case FUNDED_BY_ANOTHER_PROJECT:

						// If the budget of the created project is zero, the it is funded to the amount of 0%.
						if (budget <= ZERO) {
							amountField.setValue(ZERO);
							percentageField.setValue(ZERO_PERCENT);
						}
						// Else if the amount budget is higher than the minimum budget between the created project and the base
						// project, the amount budget is set the that minimum. Then, computes the ratio.
						else if (minBudget < amountBudget) {
							amountField.setValue(minBudget);
							percentageField.setValue(NumberUtils.ratioAsString(minBudget, budget));
						}
						// Else, simply computes the ratio.
						else {
							percentageField.setValue(NumberUtils.ratioAsString(amountBudget, budget));
						}

						break;

					// --
					// -- CASE: the created project is funding the base project.
					// --
					case FUNDING_ANOTHER_PROJECT:

						// If the budget of the base project is zero, then it is funded to the amount of 0%.
						if (baseBudget <= ZERO) {
							percentageField.setValue(ZERO_PERCENT);
						}
						// If the budget of the created project is zero, the it is funding the base project to the amount of 0%.
						else if (budget <= ZERO) {
							amountField.setValue(ZERO);
							percentageField.setValue(ZERO_PERCENT);
						}
						// Else if the amount budget is higher than the minimum budget between the created project and the base
						// project, the amount budget is set the that minimum. Then, computes the ratio.
						else if (minBudget < amountBudget) {
							amountField.setValue(minBudget);
							percentageField.setValue(NumberUtils.ratioAsString(minBudget, baseBudget));
						}
						// Else, simply computes the ratio.
						else {
							percentageField.setValue(NumberUtils.ratioAsString(amountBudget, baseBudget));

						}

						break;

					// --
					// -- CASE: creates a test project (must not happen).
					// --
					case TEST_PROJECT:

						budgetField.setValue(ZERO);
						amountField.setValue(ZERO);
						percentageField.setValue(I18N.CONSTANTS.createProjectPercentageNotAvailable());
						break;

					// --
					// -- CASE: creates a simple project (must not happen).
					// --
					default:

						amountField.setValue(ZERO);
						percentageField.setValue(I18N.CONSTANTS.createProjectPercentageNotAvailable());
						break;

				}

			}

		});

		// Delete button action.
		view.setDeleteTestProjectAction(new DeleteTestProjectAction() {

			@Override
			public void deleteTestProject(final ProjectDTO testProject, final Loadable loadable) {

				// Request confirm test project delete
				N10N.confirmation(I18N.CONSTANTS.deleteTestProjectHeader(), I18N.CONSTANTS.deleteTestProjectConfirm(), new ConfirmCallback() {

					@Override
					public void onAction() {

						// Builds the command.
						final Map<String, Object> changes = new HashMap<String, Object>();
						changes.put("dateDeleted", new Date());

						// Executes the command.
						dispatch.execute(new UpdateEntity(testProject, changes), new CommandResultHandler<VoidResult>() {

							@Override
							public void onCommandFailure(final Throwable arg0) {
								N10N.error(I18N.CONSTANTS.createProjectFailed(), I18N.CONSTANTS.deleteTestProject());
							}

							@Override
							public void onCommandSuccess(final VoidResult result) {
								view.getTestProjectsField().getStore().remove(testProject);
								view.getTestProjectsField().getStore().commitChanges();
								N10N.notification(I18N.CONSTANTS.deleteProjectNotificationTitle(), I18N.CONSTANTS.deleteProjectNotificationContent(), MessageType.VALID);

								eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROJECT_DRAFT_DELETE, testProject));
							}

						}, loadable, view.getCreateButton());

					}

				});

			}

		});

		// Create button action.
		view.getCreateButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				createProject();
			}

		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// Retrieves the current creation mode.
		currentMode = Mode.fromRequestParameterValue(request != null ? request.getParameter(RequestParameter.TYPE) : null);
		if (currentMode == null) {
			currentMode = Mode.DEFAULT_MODE;
		}

		// Retrieves the funded / funding project.
		baseProject = null;
		if (currentMode == Mode.FUNDED_BY_ANOTHER_PROJECT || currentMode == Mode.FUNDING_ANOTHER_PROJECT) {
			baseProject = (ProjectDTO) (request != null ? request.getData(RequestParameter.DTO) : null);
		}

		// Clears the form.
		view.getFormPanel().clearAll();
		view.getModelsField().setEnabled(false);
		view.getOrgUnitsField().setEnabled(false);
		view.getTestProjectsField().getStore().removeAll();

		// Fill pre-defined values if provided.
		String preselectedModelName = null;
		
		if(request != null) {
			// Code.
			if(request.getData(RequestParameter.CODE) != null) {
				view.getNameField().setValue((String) request.getData(RequestParameter.CODE));
			}
			
			// Title.
			if(request.getData(RequestParameter.TITLE) != null) {
				view.getFullNameField().setValue((String) request.getData(RequestParameter.TITLE));
			}
			
			// Planned budget.
			if(request.getData(RequestParameter.BUDGET) != null) {
				view.getBudgetField().setValue((Double) request.getData(RequestParameter.BUDGET));
			}
			
			// Project model name.
			if(request.getData(RequestParameter.MODEL) != null) {
				preselectedModelName = (String) request.getData(RequestParameter.MODEL);
			}
		}
		
		// Loads the lists.
		loadOrgUnits(currentMode);
		loadProjectModels(currentMode, preselectedModelName);
		loadTestProjects(currentMode);

		// --
		// -- Customizes the form for the current mode.
		// --

		final boolean showBudgetField;
		final boolean showOrgUnitsField;
		final boolean showAmountField;
		final String amountFieldLabel;
		final boolean showTestProjectsField;
		final String viewTitle;
		final String baseProjectBudgetLabel;
		final Double baseProjectBudget;

		switch (currentMode) {

		// --
		// -- CASE: the created project is funded by the base project.
		// --
			case FUNDED_BY_ANOTHER_PROJECT:

				showBudgetField = true;

				showOrgUnitsField = true;

				showAmountField = true;
				amountFieldLabel = I18N.MESSAGES.projectFinancesDetails(baseProject.getName()) + " (" + I18N.CONSTANTS.currencyEuro() + ')';
				baseProjectBudgetLabel = null;
				baseProjectBudget = null;

				showTestProjectsField = false;

				viewTitle = I18N.CONSTANTS.createProjectTypePartnerCreateDetails();

				break;

			// --
			// -- CASE: the created project is funding the base project.
			// --
			case FUNDING_ANOTHER_PROJECT:

				showBudgetField = true;

				showOrgUnitsField = true;

				showAmountField = true;
				amountFieldLabel = I18N.MESSAGES.projectFundedByDetails(baseProject.getName()) + " (" + I18N.CONSTANTS.currencyEuro() + ')';
				baseProjectBudgetLabel = I18N.MESSAGES.projectFundsDetails(baseProject.getName()) + " (" + I18N.CONSTANTS.currencyEuro() + ')' + I18N.CONSTANTS.form_label_separator();
				baseProjectBudget = baseProject.getPlannedBudget();

				showTestProjectsField = false;

				viewTitle = I18N.CONSTANTS.createProjectTypeFundingCreateDetails();

				break;

			// --
			// -- CASE: creates a test project.
			// --
			case TEST_PROJECT:

				showBudgetField = false;

				showOrgUnitsField = false;

				showAmountField = false;
				amountFieldLabel = null;
				baseProjectBudgetLabel = null;
				baseProjectBudget = null;

				showTestProjectsField = true;

				viewTitle = I18N.CONSTANTS.createProjectTest();

				break;

			// --
			// -- CASE: creates a simple project.
			// --
			default:

				showBudgetField = true;

				showOrgUnitsField = true;

				showAmountField = false;
				amountFieldLabel = null;
				baseProjectBudgetLabel = null;
				baseProjectBudget = null;

				showTestProjectsField = false;

				viewTitle = I18N.CONSTANTS.createProject();

				break;

		}

		// Budget field.
		view.getBudgetField().setVisible(showBudgetField);
		view.getBudgetField().setAllowBlank(!showBudgetField);

		// Org units field.
		view.getOrgUnitsField().setVisible(showOrgUnitsField);
		view.getOrgUnitsField().setAllowBlank(!showOrgUnitsField);

		// Linked budget field.
		view.getBaseProjectBudgetField().setFieldLabel(baseProjectBudgetLabel);
		view.getBaseProjectBudgetField().setValue(baseProjectBudget != null ? baseProjectBudget : ZERO);
		view.getBaseProjectBudgetField().setVisible(baseProjectBudget != null);
		
		// Amount field.
		view.getAmountField().setValue(ZERO);
		view.getAmountField().setVisible(showAmountField);
		view.getAmountField().setAllowBlank(!showAmountField);
		view.getAmountField().setFieldLabel(amountFieldLabel);

		// Percentage field.
		view.getPercentageField().setVisible(showAmountField);

		// Test projects field.
		view.getTestProjectsField().setVisible(showTestProjectsField);

		// View title.
		view.setTitle(viewTitle);

	}

	/**
	 * Loads the organization units list.
	 * 
	 * @param mode
	 *          The current creation mode.
	 */
	private void loadOrgUnits(final Mode mode) {
		dispatch.execute(new GetOrgUnits(OrgUnitDTO.Mode.WITH_TREE), new CommandResultHandler<ListResult<OrgUnitDTO>>() {
			@Override
			public void onCommandFailure(Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while getting the organization units list.", e);
				}
				N10N.error(I18N.CONSTANTS.createProjectDisableOrgUnitError());
				hideView();
			}

			@Override
			public void onCommandSuccess(final ListResult<OrgUnitDTO> result) {
				if (result == null) {
					N10N.warn(I18N.CONSTANTS.createProjectDisableOrgUnit());
					hideView();
					return;
				}

				for (OrgUnitDTO orgUnitDTO : result.getData()) {
					fillOrgUnitsCombobox(orgUnitDTO);
				}
			}
		}, view.getCreateButton());

	}

	/**
	 * Fills combobox with given the children of the given root org units.
	 * 
	 * @param unit
	 *          The root org unit.
	 */
	private void fillOrgUnitsCombobox(OrgUnitDTO unit) {

		if (unit.isCanContainProjects() && view.getOrgUnitsField().getStore().findModel(OrgUnitDTO.ID, unit.getId()) == null) {
			view.getOrgUnitsField().getStore().add(unit);
		}

		final Set<OrgUnitDTO> children = unit.getChildrenOrgUnits();
		if (children != null && !children.isEmpty()) {
			for (final OrgUnitDTO child : children) {
				fillOrgUnitsCombobox(child);
			}
		}

	}

	/**
	 * Loads the project models list.
	 * 
	 * @param mode
	 *          The current creation mode.
	 * @param preselectedModelName 
	 *			If not <code>null</code> and found, select this model in the list.
	 */
	private void loadProjectModels(final Mode mode, final String preselectedModelName) {

		final GetProjectModels command;
		if (mode == Mode.TEST_PROJECT) {
			command = new GetProjectModels(ProjectModelDTO.Mode.WITH_VISIBILITIES, ProjectModelStatus.DRAFT);
		} else {
			command = new GetProjectModels(ProjectModelDTO.Mode.WITH_VISIBILITIES);
		}

		dispatch.execute(command, new CommandResultHandler<ListResult<ProjectModelDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while getting the project models list.", e);
				}
				N10N.error(I18N.CONSTANTS.createProjectDisableModelError());
				hideView();
			}

			@Override
			public void onCommandSuccess(final ListResult<ProjectModelDTO> result) {

				if (result.isEmpty()) {
					N10N.warn(I18N.CONSTANTS.createProjectDisableModel());
					hideView();
					return;
				}

				view.getModelsField().getStore().add(result.getList());
				view.getModelsField().getStore().commitChanges();

				if(preselectedModelName != null) {
					for(final ProjectModelDTO projectModel : result.getList()) {
						if(preselectedModelName.equals(projectModel.getName())) {
							view.getModelsField().setValue(projectModel);
						}
					}
				}
			}

		}, view.getCreateButton());

	}

	/**
	 * Loads the test projects list.
	 * 
	 * @param mode
	 *          The current creation mode.
	 */
	private void loadTestProjects(final Mode mode) {

		if (mode != Mode.TEST_PROJECT) {
			// Nothing to do.
			return;
		}

		dispatch.execute(new GetTestProjects(), new CommandResultHandler<ListResult<ProjectDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while getting the test projects list.", e);
				}
				N10N.error(I18N.CONSTANTS.createProjectDisableModelError());
				hideView();
			}

			@Override
			public void onCommandSuccess(final ListResult<ProjectDTO> result) {
				view.getTestProjectsField().getStore().add(result.getList());
				view.getTestProjectsField().getStore().commitChanges();
			}

		}, view.getCreateButton());

	}
	
	/**
	 * Create the project with the current form values.
	 */
	private void createProject() {

		// Checks that the form is valid.
		if (!view.getFormPanel().isValid()) {
			return;
		}

		// Retrieves values.
		final String name = view.getNameField().getValue();
		final String fullName = view.getFullNameField().getValue();
		final double budget = view.getBudgetField().getValue() != null ? view.getBudgetField().getValue().doubleValue() : ZERO;
		final Integer projectModelId = view.getModelsField().getValue() != null ? view.getModelsField().getValue().getId() : null;
		final String orgUnitId = view.getOrgUnitsField().getValue() != null ? String.valueOf(view.getOrgUnitsField().getValue().getId()) : null;

		if (Log.isDebugEnabled()) {

			final StringBuilder sb = new StringBuilder();
			sb.append("Create a new project with parameters: ");
			sb.append("name = ");
			sb.append(name);
			sb.append(" ; full name = ");
			sb.append(fullName);
			sb.append(" ; budget = ");
			sb.append(budget);
			sb.append(" ; model id = ");
			sb.append(projectModelId);
			sb.append(" ; org unit id = ");
			sb.append(orgUnitId);

			Log.debug(sb.toString());
		}

		// Stores the project properties in a map to be send to the server.
		final HashMap<String, Object> projectProperties = new HashMap<String, Object>();
		projectProperties.put(ProjectDTO.NAME, name);
		projectProperties.put(ProjectDTO.FULL_NAME, fullName);
		projectProperties.put(ProjectDTO.BUDGET, budget);
		projectProperties.put(ProjectDTO.MODEL_ID, projectModelId);
		projectProperties.put(ProjectDTO.ORG_UNIT_ID, orgUnitId);
		projectProperties.put(ProjectDTO.CALENDAR_NAME, I18N.CONSTANTS.calendarDefaultName());
		projectProperties.put(ProjectDTO.CREATION_MODE, currentMode);
		
		if(currentMode == Mode.FUNDING_ANOTHER_PROJECT || currentMode == Mode.FUNDED_BY_ANOTHER_PROJECT) {
			projectProperties.put(ProjectDTO.AMOUNT, view.getAmountField().getValue().doubleValue());
			projectProperties.put(ProjectDTO.BASE_PROJECT, baseProject);
		}

		// Creates the project.
		dispatch.execute(new CreateEntity(ProjectDTO.ENTITY_NAME, projectProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(Throwable arg0) {
				N10N.error(I18N.CONSTANTS.createProjectFailed(), I18N.CONSTANTS.createProjectFailedDetails(), null);
			}

			@Override
			public void onCommandSuccess(CreateResult result) {

				final ProjectDTO project = (ProjectDTO) result.getEntity();

				if (Log.isDebugEnabled()) {
					Log.debug("Project created with id #" + project.getId() + ".");
				}

				// --
				// -- Launches the creation event.
				// --

				final Object[] eventParams = new Object[3];

				eventParams[0] = currentMode; // the creation mode.
				eventParams[1] = project; // The created project.

				switch (currentMode) {
					case FUNDED_BY_ANOTHER_PROJECT:

						eventParams[2] = view.getAmountField().getValue().doubleValue(); // The funded amount.
						break;

					case FUNDING_ANOTHER_PROJECT:

						eventParams[2] = view.getAmountField().getValue().doubleValue(); // The funding amount.
						break;

					case TEST_PROJECT:

						// Add the new test project to the test projects field.
						view.getTestProjectsField().getStore().add(project);
						view.getTestProjectsField().getStore().commitChanges();
						break;

					default:

						break;

				}

				// Hides the view unless the test mode is active.
				if (currentMode != Mode.TEST_PROJECT) {
					hideView();
				}

				// Notifies the project creation.
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROJECT_CREATE, eventParams));
				N10N.notification(I18N.CONSTANTS.createProjectSucceeded(),I18N.CONSTANTS.createProjectSucceededDetails(),MessageType.INFO);
			}
		}, view.getCreateButton());
	}

}
