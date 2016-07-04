package org.sigmah.client.ui.presenter.project;

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


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.widget.Layout;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchQueue;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.view.project.ProjectDetailsView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.IterableGroupPanel;
import org.sigmah.client.ui.widget.form.IterableGroupPanel.IterableGroupItem;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateLayoutGroupIterations;
import org.sigmah.shared.command.UpdateLayoutGroupIterations.IterationChange;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectDetailsDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.RequiredValueHandler;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueHandler;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;
import org.sigmah.shared.util.ValueResultUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.computation.ComputationTriggerManager;

/**
 * Project's details presenter which manages the {@link ProjectDetailsView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectDetailsPresenter extends AbstractProjectPresenter<ProjectDetailsPresenter.View> implements IterableGroupPanel.Delegate {

	// CSS style names.
	private static final String STYLE_PROJECT_LABEL_10 = "project-label-10";

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectDetailsView.class)
	public static interface View extends AbstractProjectPresenter.View {

		LayoutContainer getMainPanel();

		void setMainPanelWidget(Widget widget);

		Button getSaveButton();

	}

	/**
	 * List of values changes.
	 */
	private final ArrayList<ValueEvent> valueChanges = new ArrayList<ValueEvent>();

	private final Map<Integer, IterationChange> iterationChanges = new HashMap<Integer, IterationChange>();

	private final Map<Integer, IterableGroupItem> newIterationsTabItems = new HashMap<Integer, IterableGroupItem>();
	
	/**
	 * Listen to the values of flexible elements to update computated values.
	 */
	@Inject
	private ComputationTriggerManager computationTriggerManager;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ProjectDetailsPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.PROJECT_DETAILS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Save action.
		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				onSaveAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		load(getProject().getProjectModel().getProjectDetails());

		valueChanges.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasValueChanged() {
		return !valueChanges.isEmpty() || !iterationChanges.isEmpty();
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Loads the presenter with the given project {@code details}.
	 * 
	 * @param details
	 *          The project details.
	 */
	private void load(final ProjectDetailsDTO details) {

		// Prepare the manager of computation elements
		computationTriggerManager.prepareForProject(getProject());

		// Clear panel.
		view.getMainPanel().removeAll();

		// Layout.
		final LayoutDTO layout = details.getLayout();

		// Counts elements.
		int count = 0;
		for (final LayoutGroupDTO groupDTO : layout.getGroups()) {
			count += groupDTO.getConstraints().size();
		}

		if (count == 0) {
			// Default details page.
			final Label label = new Label(I18N.CONSTANTS.projectDetailsNoDetails());
			label.addStyleName(STYLE_PROJECT_LABEL_10);
			view.setMainPanelWidget(label);
			return;
		}

		final Grid gridLayout = (Grid) layout.getWidget();

		final DispatchQueue queue = new DispatchQueue(dispatch, true);

		for (final LayoutGroupDTO groupLayout : layout.getGroups()) {

			// simple group
			if(!groupLayout.getHasIterations()) {

				FieldSet fieldSet = createGroupLayoutFieldSet(getProject(), groupLayout, queue, null, null, null);
				gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), fieldSet);
				continue;
			}

			final FieldSet fieldSet = (FieldSet) groupLayout.getWidget();
			gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), fieldSet);

			// iterative group
			final IterableGroupPanel tabPanel = Forms.iterableGroupPanel(dispatch, groupLayout, getProject(), ProfileUtils.isGranted(auth(), GlobalPermissionEnum.CREATE_ITERATIONS) && getProject().getCurrentAmendment() == null);
			tabPanel.setDelegate(this);
			fieldSet.add(tabPanel);

			tabPanel.setAutoHeight(true);
			tabPanel.setAutoWidth(true);
			tabPanel.setTabScroll(true);
			tabPanel.addStyleName("white-tab-body");
			tabPanel.setBorders(true);
			tabPanel.setBodyBorder(false);

			Integer amendmentId;

			if (getProject().getCurrentAmendment() != null) {
				amendmentId = getProject().getCurrentAmendment().getId();
			} else {
				amendmentId = -1;
			}
			GetLayoutGroupIterations getIterations = new GetLayoutGroupIterations(groupLayout.getId(), getProject().getId(), amendmentId);

			queue.add(getIterations, new CommandResultHandler<ListResult<LayoutGroupIterationDTO>>() {

				@Override
				public void onCommandFailure(final Throwable throwable) {
					if (Log.isErrorEnabled()) {
						Log.error("Error, layout group iterations not loaded.", throwable);
					}
					throw new RuntimeException(throwable);
				}

				@Override
				protected void onCommandSuccess(ListResult<LayoutGroupIterationDTO> result) {
					DispatchQueue iterationsQueue = new DispatchQueue(dispatch, true);

					for(final LayoutGroupIterationDTO iteration : result.getList()) {

						final IterableGroupItem tab = new IterableGroupItem(tabPanel, iteration.getId(), iteration.getName());
						tabPanel.addIterationTab(tab);

						Layout tabLayout = Layouts.fitLayout();

						tab.setLayout(tabLayout);

						FieldSet tabSet = createGroupLayoutFieldSet(getProject(), groupLayout, iterationsQueue, iteration == null ? null : iteration.getId(), tabPanel, tab);

						tab.add(tabSet);
					}

					iterationsQueue.start();

					if(tabPanel.getItemCount() > 0) {
						tabPanel.setSelection(tabPanel.getItem(0));
					}

				}
			}, new LoadingMask(view.getMainPanel()));

			fieldSet.layout();
		}

		queue.start();

		view.setMainPanelWidget(gridLayout);
	}

	@Override
	public IterationChange getIterationChange(int iterationId) {
		return iterationChanges.get(iterationId);
	}

	@Override
	public void setIterationChange(IterationChange iterationChange) {
		iterationChanges.put(iterationChange.getIterationId(), iterationChange);

		if (!getParentPresenter().getCurrentDisplayedPhase().isEnded()) {
			view.getSaveButton().enable();
		}
	}

	@Override
	public void addIterationTabItem(int iterationId, IterableGroupItem tab) {
		newIterationsTabItems.put(iterationId, tab);
	}

	@Override
	public FieldSet createGroupLayoutFieldSet(FlexibleElementContainer container, LayoutGroupDTO groupLayout, DispatchQueue queue, final Integer iterationId, final IterableGroupPanel tabPanel, final IterableGroupItem tabItem) {
		final ProjectDTO project = (ProjectDTO)container;

		// Creates the fieldset and positions it.
		final FieldSet fieldSet = (FieldSet) groupLayout.getWidget();

		// For each constraint in the current layout group.
		if (ClientUtils.isEmpty(groupLayout.getConstraints())) {
			return fieldSet;
		}

		for (final LayoutConstraintDTO constraintDTO : groupLayout.getConstraints()) {

			// Gets the element managed by this constraint.
			final FlexibleElementDTO elementDTO = constraintDTO.getFlexibleElementDTO();

			// --
			// -- DISABLED ELEMENTS
			// --

			if(elementDTO.isDisabled()) {
				continue;
			}

			// --
			// -- ELEMENT VALUE
			// --

			// Retrieving the current amendment id.
			final Integer amendmentId;
			if (project.getCurrentAmendment() != null) {
				amendmentId = project.getCurrentAmendment().getId();
			} else {
				amendmentId = null;
			}

			// Remote call to ask for this element value.
			GetValue getValue;

  		getValue = new GetValue(project.getId(), elementDTO.getId(), elementDTO.getEntityName(), amendmentId, iterationId);

			queue.add(getValue, new CommandResultHandler<ValueResult>() {

				@Override
				public void onCommandFailure(final Throwable throwable) {
					if (Log.isErrorEnabled()) {
						Log.error("Error, element value not loaded.", throwable);
					}
					throw new RuntimeException(throwable);
				}

				@Override
				public void onCommandSuccess(final ValueResult valueResult) {

					if (Log.isDebugEnabled()) {
						Log.debug("Element value(s) object : " + valueResult);
					}

					// --
					// -- ELEMENT COMPONENT
					// --

					// Configures the flexible element for the current application state before generating its component.
					elementDTO.setService(dispatch);
					elementDTO.setAuthenticationProvider(injector.getAuthenticationProvider());
					elementDTO.setEventBus(eventBus);
					elementDTO.setCache(injector.getClientCache());
					elementDTO.setCurrentContainerDTO(project);
					elementDTO.setTransfertManager(injector.getTransfertManager());
					elementDTO.assignValue(valueResult);
					elementDTO.setTabPanel(tabPanel);

					final ProjectPresenter projectPresenter = injector.getProjectPresenter();

					// Generates element component (with the value).
					elementDTO.init();
					final Component elementComponent = elementDTO.getElementComponent(valueResult);

					if(elementDTO.getAmendable() && projectPresenter.projectIsLocked() && projectPresenter.canUnlockProject() && !ProfileUtils.isGranted(auth(), GlobalPermissionEnum.MODIFY_LOCKED_CONTENT)) {
						projectPresenter.addUnlockProjectPopup(elementDTO, elementComponent, new LoadingMask(view.getMainPanel()));
					}

					// Component width.
					final FormData formData;
					if (elementDTO.getPreferredWidth() == 0) {
						formData = new FormData("100%");
					} else {
						formData = new FormData(elementDTO.getPreferredWidth(), -1);
					}

					if (elementComponent != null) {
						fieldSet.add(elementComponent, formData);
					}
					fieldSet.layout();

					// --
					// -- ELEMENT HANDLERS
					// --

					// Adds a value change handler if this element is a dependency of a ComputationElementDTO.
					computationTriggerManager.listenToValueChangesOfElement(elementDTO, elementComponent, valueChanges);

					// Adds a value change handler to this element.
					elementDTO.addValueHandler(new ValueHandler() {

						@Override
						public void onValueChange(final ValueEvent event) {

							if(tabPanel != null) {
								event.setIterationId(tabPanel.getCurrentIterationId());
							}

							// TODO: Find linked computation fields if any and recompute the value.

							// Stores the change to be saved later.
							valueChanges.add(event);

							if (!getParentPresenter().getCurrentDisplayedPhase().isEnded()) {

								// Enables the save action.
								view.getSaveButton().enable();
							}
						}
					});


					if(elementDTO.getValidates() && tabItem != null) {
						tabItem.setElementValidity(elementDTO, elementDTO.isCorrectRequiredValue(valueResult));
						tabItem.refreshTitle();
						elementDTO.addRequiredValueHandler(new RequiredValueHandlerImpl(elementDTO));
					}
				}
			}, new LoadingMask(view.getMainPanel()));
		}

		fieldSet.setCollapsible(false);
		fieldSet.setAutoHeight(true);
		fieldSet.setBorders(false);
		fieldSet.setHeadingHtml("");

		return fieldSet;
	}

	/**
	 * Internal class handling the value changes of the required elements.
	 */
	private class RequiredValueHandlerImpl implements RequiredValueHandler {

		private final FlexibleElementDTO elementDTO;

		public RequiredValueHandlerImpl(FlexibleElementDTO elementDTO) {
			this.elementDTO = elementDTO;
		}

		@Override
		public void onRequiredValueChange(RequiredValueEvent event) {

			// Refresh the panel's header
			elementDTO.getTabPanel().setElementValidity(elementDTO, event.isValueOn());
			elementDTO.getTabPanel().validateElements();
		}
	}

	/**
	 * Method executed on save button action.
	 */
	private void onSaveAction() {

		// Checks if there are any changes regarding layout group iterations
		dispatch.execute(new UpdateLayoutGroupIterations(new ArrayList<IterationChange>(iterationChanges.values()), getProject().getId()), new CommandResultHandler<ListResult<IterationChange>>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError());
			}

			@Override
			protected void onCommandSuccess(ListResult<IterationChange> result) {

        for(IterationChange iterationChange : result.getList()) {
					if(iterationChange.isDeleted()) {
						// remove corresponding valueEvents

						Iterator<ValueEvent> valuesIterator = valueChanges.iterator();
						while(valuesIterator.hasNext()) {
							ValueEvent valueEvent = valuesIterator.next();

							if(valueEvent.getIterationId() == iterationChange.getIterationId()) {
								valuesIterator.remove();
							}
						}
					} else if(iterationChange.isCreated()) {
						// change ids in valueEvents
						int oldId = iterationChange.getIterationId();
						int newId = iterationChange.getNewIterationId();

						// updating tabitem id
						newIterationsTabItems.get(oldId).setIterationId(newId);

						for(ValueEvent valueEvent : valueChanges) {
							if(valueEvent.getIterationId() == oldId) {
								valueEvent.setIterationId(newId);
							}
						}
					}
				}

				iterationChanges.clear();
				newIterationsTabItems.clear();

				dispatch.execute(new UpdateProject(getProject().getId(), valueChanges), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						N10N.error(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError());
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {

						N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.saveConfirm());

						// Checks if there is any update needed to the local project instance.
						boolean refreshBanner = false;
						boolean coreVersionUpdated = false;
						ProjectDTO newProject = null;

						for (final ValueEvent event : valueChanges) {
							if (event.getSource() instanceof DefaultFlexibleElementDTO) {
								newProject = updateCurrentProject(((DefaultFlexibleElementDTO) event.getSource()), event.getSingleValue(), event.isProjectCountryChanged());
								getParentPresenter().setCurrentProject(newProject);
								refreshBanner = true;
							}
							coreVersionUpdated |= event.getSourceElement().getAmendable();
						}

						valueChanges.clear();

						if (refreshBanner) {
							eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROJECT_BANNER_UPDATE));
						}

						if(coreVersionUpdated) {
							eventBus.fireEvent(new UpdateEvent(UpdateEvent.CORE_VERSION_UPDATED));
						}

						// Avoid tight coupling with other project events.
						// FIXME (from v1.3) eventBus.fireEvent(new ProjectEvent(ProjectEvent.CHANGED, getProject().getId()));

						if (newProject != null) {
							load(newProject.getProjectModel().getProjectDetails());
						}
					}

				}, view.getSaveButton(), new LoadingMask(view.getMainPanel()));
			}
		}, view.getSaveButton(), new LoadingMask(view.getMainPanel()));
	}

	/**
	 * Updates locally the DTO to avoid a remote server call.
	 * 
	 * @param element
	 *          The default flexible element.
	 * @param value
	 *          The new value.
	 * @param isProjectCountryChanged
	 *          If the the project country has changed.
	 */
	private ProjectDTO updateCurrentProject(final DefaultFlexibleElementDTO element, final String value, final boolean isProjectCountryChanged) {

		final ProjectDTO currentProjectDTO = getProject();

		switch (element.getType()) {

			case CODE:
				currentProjectDTO.setName(value);
				break;

			case TITLE:
				currentProjectDTO.setFullName(value);
				break;

			case START_DATE:
				if ("".equals(value)) {
					currentProjectDTO.setStartDate(null);
				} else {
					try {
						final long timestamp = Long.parseLong(value);
						currentProjectDTO.setStartDate(new Date(timestamp));
					} catch (NumberFormatException e) {
						// nothing, invalid date.
					}
				}
				break;

			case END_DATE:
				if ("".equals(value)) {
					currentProjectDTO.setEndDate(null);
				} else {
					try {
						final long timestamp = Long.parseLong(value);
						currentProjectDTO.setEndDate(new Date(timestamp));
					} catch (NumberFormatException e) {
						// nothing, invalid date.
					}
				}
				break;

			case BUDGET:
				try {

					final BudgetElementDTO budgetElement = (BudgetElementDTO) element;
					final Map<Integer, String> values = ValueResultUtils.splitMapElements(value);

					double plannedBudget = 0.0;
					double spendBudget = 0.0;
					double receivedBudget = 0.0;

					for (final BudgetSubFieldDTO bf : budgetElement.getBudgetSubFields()) {
						if (bf.getType() != null) {
							switch (bf.getType()) {
								case PLANNED:
									plannedBudget = Double.parseDouble(values.get(bf.getId()));
									break;
								case RECEIVED:
									receivedBudget = Double.parseDouble(values.get(bf.getId()));
									break;
								case SPENT:
									spendBudget = Double.parseDouble(values.get(bf.getId()));
									break;
								default:
									break;

							}

						}
					}

					currentProjectDTO.setPlannedBudget(plannedBudget);
					currentProjectDTO.setSpendBudget(spendBudget);
					currentProjectDTO.setReceivedBudget(receivedBudget);

					/**
					 * Update funding projects - Reflect to funded project in funding projects currentProjectDTO |-- getFunding()
					 * → <ProjectFundingDTO> // list of funding projects |-- getPercentage() // no updates from here |--
					 * getFunded() → ProjectDTOLight // funded project details light |--getPlannedBudget() // update budget
					 * details
					 */
					final List<ProjectFundingDTO> fundingProjects = currentProjectDTO.getFunding();
					if (ClientUtils.isNotEmpty(fundingProjects)) {
						for (final ProjectFundingDTO projectFundingDTO : fundingProjects) {
							final ProjectDTO fundedProject = projectFundingDTO.getFunded();
							if (fundedProject != null && fundedProject.getId().equals(currentProjectDTO.getId())) {
								fundedProject.setPlannedBudget(plannedBudget);
								fundedProject.setSpendBudget(spendBudget);
								fundedProject.setReceivedBudget(receivedBudget);
							}
						}
					}

				} catch (Exception e) {
					// nothing, invalid budget.
				}
				break;

			case COUNTRY:
				final CountryDTO country = element.getCountriesStore().findModel(EntityDTO.ID, Integer.parseInt(value));
				if (country != null) {
					currentProjectDTO.setCountry(country);
				} else {
					// nothing, invalid country.
				}
				break;

			case OWNER:
				// The owner component doesn't fire any event for now.
				break;

			case MANAGER:
				final UserDTO manager = element.getManagersStore().findModel(EntityDTO.ID, Integer.parseInt(value));
				if (manager != null) {
					currentProjectDTO.setManager(manager);
				} else {
					// nothing, invalid user.
				}
				break;

			case ORG_UNIT:
				currentProjectDTO.setOrgUnitId(Integer.parseInt(value));

				if (isProjectCountryChanged) {
					dispatch.execute(new GetOrgUnit(currentProjectDTO.getOrgUnitId(), OrgUnitDTO.Mode.BASE), new CommandResultHandler<OrgUnitDTO>() {

						@Override
						public void onCommandSuccess(final OrgUnitDTO result) {
							if (result != null) {
								currentProjectDTO.setCountry(result.getCountry());
							}
						}
					});
				}
				break;

			default:
				// Nothing, unknown type.
				break;
		}

		return currentProjectDTO;
	}

}
