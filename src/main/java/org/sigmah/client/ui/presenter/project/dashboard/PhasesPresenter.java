package org.sigmah.client.ui.presenter.project.dashboard;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchQueue;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.project.dashboard.PhasesView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.shared.command.ChangePhase;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.RequiredValueHandler;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueHandler;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;
import org.sigmah.shared.util.ValueResultUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import org.sigmah.client.computation.ComputationTriggerManager;
import org.sigmah.client.ui.presenter.project.ProjectPresenter;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;
import org.sigmah.shared.dispatch.FunctionalException;

/**
 * Phases presenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PhasesPresenter extends AbstractPresenter<PhasesPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(PhasesView.class)
	public static interface View extends ViewInterface, Loadable {

		/**
		 * Mask the main panel and set the mask counter.
		 * 
		 * @param count
		 *          The mask counter.
		 */
		void mask(int count);

		/**
		 * Decrements the mask counter and unmasks the main panel if the counter reaches <code>0</code>.
		 * 
		 * @return {@code true} if the mask counter's value is {@code 0}.
		 */
		boolean unmask();

		Button getButtonActivatePhase();

		Button getButtonPhaseGuide();

		Button getButtonSavePhase();

		com.extjs.gxt.ui.client.widget.grid.Grid<FlexibleElementDTO> getGridRequiredElements();

		LayoutContainer getPanelProjectModel();

		LayoutContainer getPanelSelectedPhase();

		TabPanel getTabPanelPhases();

		void flushToolbar();

		void fillToolbar(boolean changePhaseAuthorized);

		ContentPanel getRequiredElementContentPanel();
		
		void layout();

	}

	/**
	 * Internal class handling the value changes of the flexible elements.
	 */
	private class ValueHandlerImpl implements ValueHandler {

		@Override
		public void onValueChange(ValueEvent event) {

			// Stores the change to be saved later.
			valueChanges.add(event);

			// Enables the save action.
			view.getButtonSavePhase().enable();
		}
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

			// Updates the element state for the new value.
			elementDTO.setFilledIn(event.isValueOn());
			view.getGridRequiredElements().getStore().update(elementDTO);

			// Map the required element for the current displayed phase.
			currentPhaseRequiredElements.putActual(elementDTO.getId(), event.isValueOn());

			// If the current displayed phase is the active one,
			// map the required element for the active phase.
			if (isCurrentPhase(getCurrentProject().getCurrentPhase())) {
				activePhaseRequiredElements.putActual(elementDTO.getId(), event.isValueOn());
			}

			// The element is in charge of the saving of its values. The state
			// of the current project must be refreshed here.
			if (event.isImmediate()) {
				view.getButtonSavePhase().fireEvent(Events.OnClick);
			}

			// Refresh the panel's header
			refreshRequiredElementContentPanelHeader();
		}
	}

	/**
	 * List of values changes.
	 */
	private List<ValueEvent> valueChanges;

	/**
	 * Mapping between phases models ids and tabs items (to quickly get a tab).
	 */
	private Map<Integer, TabItem> tabItemsMap;

	/**
	 * A map to maintain the current active phase required elements states.
	 */
	private RequiredValueStateList activePhaseRequiredElements;

	/**
	 * A map to maintain the current displayed phase required elements states.
	 */
	private RequiredValueStateList currentPhaseRequiredElements;
	
	/**
	 * Listen to the values of flexible elements to update computated values.
	 */
	@Inject
	private ComputationTriggerManager computationTriggerManager;
	
	/**
	 * Presenters initialization.
	 * 
	 * @param view
	 *          The presenter's view.
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	protected PhasesPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		valueChanges = new ArrayList<ValueEvent>();
		tabItemsMap = new HashMap<Integer, TabItem>();
		activePhaseRequiredElements = new RequiredValueStateList();
		currentPhaseRequiredElements = new RequiredValueStateList();
	}
	
	/**
	 * Clear everything by removing all tabs and all required elements.
	 */
	public void clear() {
		clearChangedValues();
		
		// Clears the required elements maps.
		activePhaseRequiredElements.clear();
		currentPhaseRequiredElements.clear();

		// Removes old tabs configuration (from the previous displayed project).
		view.getTabPanelPhases().removeAll();
		view.getTabPanelPhases().removeAllListeners();
		tabItemsMap.clear();
	}

	/**
	 * <p>
	 * Refreshes the presenter with the given {@code project}.
	 * </p>
	 * <p>
	 * The project's following attributes must be loaded:
	 * <ul>
	 * <li>Base attributes (id, name, etc.)</li>
	 * <li>{@link ProjectDTO#PHASES}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param project
	 *          The loaded project DTO.
	 */
	public void refresh(final ProjectDTO project) {
		clearChangedValues();
		view.getButtonSavePhase().disable();
		setCurrentDisplayedPhase(project.getCurrentPhase());
		loadProjectDashboard(project);
	}

	private ProjectDTO getCurrentProject() {
		return injector.getProjectPresenter().getCurrentProject();
	}

	private void setCurrentProject(final ProjectDTO project) {
		injector.getProjectPresenter().setCurrentProject(project);
	}

	private PhaseDTO getCurrentDisplayedPhase() {
		return injector.getProjectPresenter().getCurrentDisplayedPhase();
	}

	private void setCurrentDisplayedPhase(final PhaseDTO phase) {
		injector.getProjectPresenter().setCurrentDisplayedPhase(phase);
	}

	/**
	 * Simply fires a {@link UpdateEvent#PROJECT_BANNER_UPDATE} event on the application event bus.
	 */
	private void refreshProjectBanner() {
		eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROJECT_BANNER_UPDATE));
	}

	/**
	 * Masks the phases widget.
	 * 
	 * @param count
	 *          The mask counter.
	 */
	private void mask(final int count) {
		view.mask(count);
	}

	/**
	 * Unmasks the phases widget.
	 */
	private void unmask() {
		if (view.unmask()) {
			// Refreshes the toolbar.
			refreshActionsToolbar();
		}
	}

	public boolean hasValueChanged() {
		return !valueChanges.isEmpty();
	}

	public void clearChangedValues() {
		valueChanges.clear();
        view.getButtonSavePhase().setEnabled(false);
	}

	/**
	 * Loads the given {@code project} phases.
	 * 
	 * @param projectDTO
	 *          The project.
	 */
	private void loadProjectDashboard(final ProjectDTO projectDTO) {

		// Clears the required elements maps.
		activePhaseRequiredElements.clear();
		currentPhaseRequiredElements.clear();

		// Sorts phases to be displayed in the correct order.
		Collections.sort(projectDTO.getPhases());

		// --
		// -- TABS CREATION.
		// --

		// Removes old tabs configuration (from the previous displayed project).
		view.getTabPanelPhases().removeAll();
		view.getTabPanelPhases().removeAllListeners();
		tabItemsMap.clear();
		
		TabItem currentPhase = null;

		// Creates tabs for each phase.
		for (final PhaseDTO phaseDTO : projectDTO.getPhases()) {

			// Creates the default tab.
			final TabItem tabItem = new TabItem(phaseDTO.getPhaseModel().getName());
			tabItem.setLayout(new FitLayout());
			tabItem.setEnabled(false);
			tabItem.setAutoHeight(true);

			// Map the tab item with the phase id.
			tabItemsMap.put(phaseDTO.getPhaseModel().getId(), tabItem);

			// Adds the tab to the view.
			view.getTabPanelPhases().add(tabItem);

			view.getTabPanelPhases().addListener(Events.Resize, new Listener<BoxComponentEvent>() {

				@Override
				public void handleEvent(BoxComponentEvent event) {
					// 25 is the default height of the tab bar.
					tabItem.setSize(event.getWidth(), event.getHeight() - 25);
				}
			});

			// If the phase is the active one.
			if (isActivePhase(phaseDTO)) {
				// Enables it, apply the correct style and selects it.
				tabItem.setEnabled(true);
				tabItem.getHeader().addStyleName(PhasesView.PROJECT_PHASE_ACTIVE);
				
				currentPhase = tabItem;
			}

			// If the phase is ended.
			if (isEndedPhase(phaseDTO)) {
				// Enables it and apply the correct style.
				tabItem.setEnabled(true);
				tabItem.getHeader().addStyleName(PhasesView.PROJECT_PHASE_CLOSED);
			}
		}

		// Enables successors tabs of the current phase.
		enableSuccessorsTabs();

		// --
		// -- TABS LISTENERS.
		// --

		// Adds tabs listeners for selection changes (must be added after tabs creation or event fired for each tab).
		for (final PhaseDTO phaseDTO : projectDTO.getPhases()) {

			final TabItem tabItem = tabItemsMap.get(phaseDTO.getPhaseModel().getId());
			tabItem.addListener(Events.Select, new Listener<ComponentEvent>() {

				/**
				 * Id of the phase to display.<br>
				 * Important: it's better to manipulate the id instead of the phases instances to keep coherence after a project
				 * update.
				 */
				private final Integer phaseDTOId = phaseDTO.getId();

				private PhaseDTO retrievePhaseDTO() {
					// Loads the phase of the selected tab (loaded from the current project instance).
					for (final PhaseDTO p : getCurrentProject().getPhases()) {
						if (p.getId().equals(phaseDTOId)) {
							return p;
						}
					}

					return null;
				}

				@Override
				public void handleEvent(ComponentEvent tpe) {

					final PhaseDTO toDisplayPhase = retrievePhaseDTO();
					
					if (!view.getButtonSavePhase().isEnabled() || isEndedPhase(getCurrentDisplayedPhase())) {
						// Load the selected phase without asking a question
						// if the current phase has not been modified or if it is ended.
						loadPhaseOnTab(toDisplayPhase);
						return;
					}

					// Asks the client to save the unsaved elements before switching phases.
					N10N.confirmation(I18N.CONSTANTS.projectPhaseChangeAlert(), I18N.CONSTANTS.projectPhaseChangeAlertDetails(), new ConfirmCallback() {

						@Override
						public void onAction() {

							// --
							// YES CALLBACK.
							// --

							view.getButtonSavePhase().fireEvent(Events.OnClick);
							if (isActivePhase(getCurrentDisplayedPhase())) {
								activePhaseRequiredElements.saveState();
							}
							loadPhaseOnTab(toDisplayPhase);
						}
					}, new ConfirmCallback() {

						@Override
						public void onAction() {

							// --
							// NO CALLBACK.
							// --

							// If the last displayed phase was the active one, modifications are discarded then the required
							// elements map is cleared (to prevent inconsistent successor activation).
							if (isActivePhase(getCurrentDisplayedPhase())) {
								activePhaseRequiredElements.clearState();
							}
							loadPhaseOnTab(toDisplayPhase);
						}
					});
				}
			});
		}
		
		view.getTabPanelPhases().setSelection(currentPhase);
	}

	/**
	 * Returns if a phase is the current displayed phase.
	 * 
	 * @param phaseDTO
	 *          The phase to test.
	 * @return If the phase is currently displayed.
	 */
	private boolean isCurrentPhase(PhaseDTO phaseDTO) {

		final PhaseDTO currentPhaseDTO = getCurrentDisplayedPhase();
		return currentPhaseDTO != null && phaseDTO != null && currentPhaseDTO.getId().equals(phaseDTO.getId());

	}

	/**
	 * Returns if a phase is the active phase of the current project.
	 * 
	 * @param phaseDTO
	 *          The phase to test.
	 * @return If the phase is active.
	 */
	private boolean isActivePhase(PhaseDTO phaseDTO) {

		final ProjectDTO currentProjectDTO = getCurrentProject();

		return currentProjectDTO != null
			&& currentProjectDTO.getCurrentPhase() != null
			&& phaseDTO != null
			&& currentProjectDTO.getCurrentPhase().getId().equals(phaseDTO.getId());
	}

	/**
	 * Returns if a phase is ended.
	 * 
	 * @param phaseDTO
	 *          The phase to test.
	 * @return If the phase is ended.
	 */
	private boolean isEndedPhase(PhaseDTO phaseDTO) {
		return phaseDTO != null && phaseDTO.isEnded();
	}

	/**
	 * Returns if the active phase of the current project is filled in.
	 * 
	 * @return If the active phase of the current project is filled in.
	 */
	private boolean isActivePhaseFilledIn() {
		// Checks id the map contains only true booleans.
		return activePhaseRequiredElements.isTrue();
	}

	/**
	 * Enables the successors tabs of the current displayed phase.
	 */
	private void enableSuccessorsTabs() {

		for (final PhaseModelDTO successor : getCurrentProject().getCurrentPhase().getPhaseModel().getSuccessors()) {
			final TabItem successorTabItem = tabItemsMap.get(successor.getId());
			if (successorTabItem != null) {
				successorTabItem.setEnabled(true);
			}
		}
	}

	/**
	 * Loads a project phase into the selected tab panel.
	 * 
	 * @param phaseDTO
	 *          The phase to display.
	 */
	private void loadPhaseOnTab(final PhaseDTO phaseDTO) {

		// If the element are read only.
		final boolean phaseIsEnded = isEndedPhase(phaseDTO);

		// Masks the main panel.
		int count = 0;
		for (final LayoutGroupDTO groupDTO : phaseDTO.getPhaseModel().getLayout().getGroups()) {
			count += groupDTO.getConstraints().size();
		}

		if (count > 0) {
			mask(count);
		}

		// Sets current project status.
		setCurrentDisplayedPhase(phaseDTO);

		// Clears the required elements map for the current displayed phase.
		currentPhaseRequiredElements.clear();
		valueChanges.clear();
		
		// --
		// -- CLEARS PANELS
		// --

		// Clears all tabs.
		for (final TabItem tab : view.getTabPanelPhases().getItems()) {
			tab.removeAll();
		}

		// Clears panels.
		view.getPanelSelectedPhase().removeAll();
		view.getGridRequiredElements().getStore().removeAll();
		view.getTabPanelPhases().getSelectedItem().add(view.getPanelProjectModel());

		// Store required elements
		final List<FlexibleElementDTO> requiredElemetsList = new ArrayList<FlexibleElementDTO>();

		// --
		// -- PHASE LAYOUT
		// --

		final Grid layoutGrid = (Grid) phaseDTO.getPhaseModel().getWidget();
		layoutGrid.setStyleName("flexibility-layout");
		view.getPanelSelectedPhase().add(layoutGrid);

		// Dispatch queue ensuring results handling order.
		final DispatchQueue queue = new DispatchQueue(dispatch, true) {

			@Override
			protected void onComplete() {
				// View layouts update.
				// FIXME (v1.3) This should be done by Ext, not be the developer!
				injector.getProjectDashboardPresenter().getView().layoutView();
				view.layout();				
				Profiler.INSTANCE.endScenario(Scenario.OPEN_PROJECT);
			}
			
		};
		
		// Current project
		final ProjectDTO project = getCurrentProject();
		
		// Prepare the manager of computation elements
		computationTriggerManager.prepareForProject(project);

		// For each layout group.
		for (final LayoutGroupDTO groupDTO : phaseDTO.getPhaseModel().getLayout().getGroups()) {

			// Creates the fieldset and positions it.
			final FieldSet formPanel = (FieldSet) groupDTO.getWidget();
			layoutGrid.setWidget(groupDTO.getRow(), groupDTO.getColumn(), formPanel);

			// For each constraint in the current layout group.
			for (final LayoutConstraintDTO constraintDTO : groupDTO.getConstraints()) {

				// Gets the element managed by this constraint.
				final FlexibleElementDTO elementDTO = constraintDTO.getFlexibleElementDTO();
				
				// --
				// -- DISABLED ELEMENTS
				// --
				if(elementDTO.isDisabled() && 
					(phaseDTO.getEndDate() == null || phaseDTO.getEndDate().after(elementDTO.getDisabledDate()))) {
					// Hiding the field if the current phase is opened or
					// if it has been closed AFTER the field was disabled.
					unmask();
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
				queue.add(new GetValue(getCurrentProject().getId(), elementDTO.getId(), elementDTO.getEntityName(), amendmentId),
					new CommandResultHandler<ValueResult>() {

						@Override
						public void onCommandFailure(final Throwable throwable) {
							if (Log.isErrorEnabled()) {
								Log.error("Error, element value not loaded.", throwable);
							}
							unmask();
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
							elementDTO.setEventBus(eventBus);
							elementDTO.setAuthenticationProvider(injector.getAuthenticationProvider());
							elementDTO.setCache(injector.getClientCache());
							elementDTO.setCurrentContainerDTO(getCurrentProject());
							elementDTO.setTransfertManager(injector.getTransfertManager());
							elementDTO.assignValue(valueResult);

							final ProjectPresenter projectPresenter = injector.getProjectPresenter();
							
							// Generates element component (with the value).
							elementDTO.init();
							final Component elementComponent = elementDTO.getElementComponent(valueResult, phaseIsEnded);
							
							if(elementDTO.getAmendable() && projectPresenter.projectIsLocked() && projectPresenter.canUnlockProject() && !ProfileUtils.isGranted(auth(), GlobalPermissionEnum.MODIFY_LOCKED_CONTENT)) {
								projectPresenter.addUnlockProjectPopup(elementDTO, elementComponent, view);
							}
							
							// Component width.
							final FormData formData;
							if (elementDTO.getPreferredWidth() == 0) {
								formData = new FormData("100%");
							} else {
								formData = new FormData(elementDTO.getPreferredWidth(), -1);
							}

							if (elementComponent != null) {
								formPanel.add(elementComponent, formData);
							}

							formPanel.layout();

							// --
							// -- ELEMENT HANDLERS
							// --

							// Adds a value change handler to this element.
							elementDTO.addValueHandler(new ValueHandlerImpl());
							
							// Adds a value change handler if this element is a dependency of a ComputationElementDTO.
							computationTriggerManager.listenToValueChangesOfElement(elementDTO, elementComponent, valueChanges);

							// If this element id a required one.
							if (elementDTO.getValidates()) {

								// Adds a specific handler.
								elementDTO.addRequiredValueHandler(new RequiredValueHandlerImpl(elementDTO));

								// Set the groupDTO into the element
								elementDTO.setGroup(groupDTO);

								elementDTO.setConstraint(constraintDTO);

								// Adds the element to the tmp list for sorting
								requiredElemetsList.add(elementDTO);

								// Clear the store
								view.getGridRequiredElements().getStore().removeAll();

								// Sorting and add the list to the view
								view.getGridRequiredElements().getStore().add(sortRequiredElements(requiredElemetsList));

								// Refresh header
								refreshRequiredElementContentPanelHeader();

								// Map the required element for the current
								// displayed phase.
								currentPhaseRequiredElements.putSaved(elementDTO.getId(), elementDTO.isFilledIn());

								// If the current displayed phase is the active one,
								// map the required element for the active phase.
								if (isCurrentPhase(getCurrentProject().getCurrentPhase())) {
									activePhaseRequiredElements.putSaved(elementDTO.getId(), elementDTO.isFilledIn());
								}
							}

							unmask();
						}
					});
			}
		}

		queue.start();

	}

	/**
	 * Refreshes the actions toolbar for the current displayed phase.
	 */
	private void refreshActionsToolbar() {

		// If the current displayed phase is ended, the toolbar is hidden.
		if (isEndedPhase(getCurrentDisplayedPhase()) &&  
			// An exception is made if the user is authorized to edit ended phases.
			!ProfileUtils.isGranted(auth(), GlobalPermissionEnum.MODIFY_LOCKED_CONTENT)) {
			// Hide the toolbar.
			view.flushToolbar();
			return;
		}

		view.fillToolbar(ProfileUtils.isGranted(auth(), GlobalPermissionEnum.CHANGE_PHASE) &&
			// Do not add the change phase button if the phase is already closed.
			!isEndedPhase(getCurrentDisplayedPhase()));

		// --
		// -- ACTION: ACTIVATE OR CLOSE PHASE
		// --

		final boolean enabled = activePhaseRequiredElements.isTrue();
		view.getButtonActivatePhase().setEnabled(enabled);
		view.getButtonActivatePhase().removeAllListeners();

		// If the current displayed phase is the active one or it is ended, the close action is displayed.
		if (isCurrentPhase(getCurrentProject().getCurrentPhase())) {

			view.getButtonActivatePhase().setText(I18N.CONSTANTS.projectClosePhaseButton());
			view.getButtonActivatePhase().setIcon(IconImageBundle.ICONS.close());
			view.getButtonActivatePhase().setTitle(enabled ? "" : I18N.CONSTANTS.projectCannotClose());

			view.getButtonActivatePhase().addSelectionListener(new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(final ButtonEvent be) {
					view.getButtonActivatePhase().showMenu();
				}
			});

			// Builds the button menu to select the next phase after closing the current displayed one.
			final Menu successorsMenu = new Menu();

			final List<PhaseDTO> successors = getCurrentProject().getSuccessors(getCurrentDisplayedPhase());

			// If the current displayed phase hasn't successor, the close action ends the project.
			if (successors == null || successors.isEmpty()) {

				final MenuItem endItem = new MenuItem(I18N.CONSTANTS.projectEnd(), IconImageBundle.ICONS.activate());
				endItem.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent me) {
						activatePhase(null, true);
					}
				});

				successorsMenu.add(endItem);
			}
			// Each successor is added to the list of choices.
			else {

				for (final PhaseDTO successor : successors) {

					final MenuItem successorItem = new MenuItem(I18N.MESSAGES.projectActivate(successor.getPhaseModel().getName()), IconImageBundle.ICONS.activate());
					successorItem.addSelectionListener(new SelectionListener<MenuEvent>() {

						@Override
						public void componentSelected(MenuEvent me) {

							activatePhase(successor, true);
						}
					});
					successorsMenu.add(successorItem);
				}
			}

			view.getButtonActivatePhase().setMenu(successorsMenu);
		}
		// Else the active action is displayed.
		else {

			view.getButtonActivatePhase().setTitle(enabled ? "" : I18N.CONSTANTS.projectCannotActivate());
			view.getButtonActivatePhase().setMenu(null);

			view.getButtonActivatePhase().setText(I18N.CONSTANTS.projectActivatePhaseButton());
			view.getButtonActivatePhase().setIcon(IconImageBundle.ICONS.activate());

			view.getButtonActivatePhase().addSelectionListener(new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(final ButtonEvent be) {
					activatePhase(getCurrentDisplayedPhase(), false);
				}
			});
		}

		// --
		// -- ACTION: SAVE MODIFICATIONS
		// --

		// Disabled until a field is modified.
		if (hasValueChanged()) {
			view.getButtonSavePhase().setEnabled(true);
		} else {
			view.getButtonSavePhase().setEnabled(false);
			view.getButtonSavePhase().removeAllListeners();
		}

		// Adds the save action.
		view.getButtonSavePhase().addListener(Events.OnClick, new SaveListener());

		// --
		// -- ACTION: PHASE GUIDE
		// --

		// Check guide availability.
		view.getButtonPhaseGuide().removeAllListeners();

		if (getCurrentDisplayedPhase().getPhaseModel().isGuideAvailable()) {

			final String guide = getCurrentDisplayedPhase().getPhaseModel().getGuide();

			view.getButtonPhaseGuide().setEnabled(true);
			view.getButtonPhaseGuide().setTitle(guide);
			view.getButtonPhaseGuide().addListener(Events.OnClick, new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {
					Window.open(guide, "_blank", null);
				}
			});
		} else {
			view.getButtonPhaseGuide().setEnabled(false);
			view.getButtonPhaseGuide().setTitle(I18N.CONSTANTS.projectPhaseGuideUnavailable());
		}
	}

	/**
	 * This method is to update the herder of requiredElementContentPanel's header text. It computes the numbers of all
	 * filled elements and then updates.
	 * 
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	private void refreshRequiredElementContentPanelHeader() {

		// The local sotre of all elements
		final ListStore<FlexibleElementDTO> listStore = view.getGridRequiredElements().getStore();

		// The number of all element in the store
		final int requiredElementsCount = listStore.getCount();

		// The number of all element that are filled in the store
		int filledRequiredElements = 0;

		for (final FlexibleElementDTO elementDTO : listStore.getModels()) {
			if (elementDTO.isFilledIn()) {
				filledRequiredElements++;
			}
		}

		view.getRequiredElementContentPanel().setHeadingText(
			I18N.CONSTANTS.projectRequiredElements() + " (" + filledRequiredElements + "/" + requiredElementsCount + ")");
	}

	/**
	 * Method to sort the list of all required elements list
	 * 
	 * @param list
	 *          List to be sorted
	 * @return List List sorted
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	private List<FlexibleElementDTO> sortRequiredElements(List<FlexibleElementDTO> list) {
		if (list.size() < 2) {
			return list;
		}

		Collections.sort(list, new Comparator<FlexibleElementDTO>() {

			@Override
			public int compare(FlexibleElementDTO arg0, FlexibleElementDTO arg1) {

				return comparePosition(arg0, arg1);
			}

		});

		return list;
	}

	/**
	 * Method to compare the exact position of the two flexible elements.
	 * 
	 * @param o1
	 *          The first flexible element.
	 * @param o2
	 *          The second flexible element
	 * @return Respectively {@code 1}, {@code -1} or {@code 0} if {@code o1}'s position is greater than, lower than or
	 *         equal to {@code o2}'s position.
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	private int comparePosition(FlexibleElementDTO o1, FlexibleElementDTO o2) {
		int groupRow1 = o1.getGroup().getRow();
		int groupColumn1 = o1.getGroup().getColumn();
		int groupRow2 = o2.getGroup().getRow();
		int groupColumn2 = o2.getGroup().getColumn();

		// First,compare the row of group of the element
		if (groupRow1 > groupRow2) {
			return 1;
		} else if (groupRow1 < groupRow2) {
			return -1;
		} else {// The row of group is the same,compare the column of group

			if (groupColumn1 > groupColumn2) {
				return 1;
			} else if (groupColumn1 < groupColumn2) {
				return -1;
			}
		}

		// If goes this far,m2 and m2 in the same group, compare the their
		// positions in the group
		int elementPosition1 = o1.getConstraint().getSortOrder();
		int elementPosition2 = o2.getConstraint().getSortOrder();

		if (elementPosition1 > elementPosition2) {
			return 1;
		} else if (elementPosition1 < elementPosition2) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * Activates a phase.
	 * 
	 * @param phase
	 *          The phase to activate.
	 * @param reload
	 *          If the current displayed phase must be reloaded.
	 */
	private void activatePhase(final PhaseDTO phase, final boolean reload) {

		// If the active phase required elements aren't filled, shows an alert and returns.
		if (!isActivePhaseFilledIn()) {
			N10N.warn(I18N.CONSTANTS.projectPhaseActivationError(), I18N.CONSTANTS.projectPhaseActivationErrorDetails());
			return;
		}

		// If the phase to activate is null, the active phase will only be closed.
		if (phase == null) {

			// Confirms that the user wants to end the project.
			N10N.confirmation(I18N.CONSTANTS.projectEnd(), I18N.MESSAGES.projectEnd(getCurrentProject().getCurrentPhase().getPhaseModel().getName()),
				new ConfirmCallback() {

					@Override
					public void onAction() {

						// Activates the current displayed phase.
						dispatch.execute(new ChangePhase(getCurrentProject().getId(), null), new CommandResultHandler<ProjectDTO>() {

							@Override
							public void onCommandFailure(final Throwable e) {

								if (Log.isErrorEnabled()) {
									Log.error("The project hasn't be ended.", e);
								}
								N10N.warn(I18N.CONSTANTS.projectEndError(), I18N.CONSTANTS.projectEndErrorDetails());
							}

							@Override
							public void onCommandSuccess(final ProjectDTO result) {

								if (Log.isDebugEnabled()) {
									Log.debug("Project successfully ended.");
								}

								// Sets the new current project (after update).
								setCurrentProject(result);

								// Sets the new current displayed phase (not necessary the active one).
								for (final PhaseDTO phase : getCurrentProject().getPhases()) {
									if (phase.getId().equals(getCurrentDisplayedPhase().getId())) {
										setCurrentDisplayedPhase(phase);
									}
								}

								refreshDashboardAfterUpdate(reload);
							}
						});
					}
				});
		}
		// Else the active will be closed and the new phase will be activated.
		else {

			// Confirms that the user wants to close the active phase and activate the given one.
			N10N.confirmation(I18N.CONSTANTS.projectCloseAndActivate(),
				I18N.MESSAGES.projectCloseAndActivate(getCurrentProject().getCurrentPhase().getPhaseModel().getName(), phase.getPhaseModel().getName()),
				new ConfirmCallback() {

					@Override
					public void onAction() {

						// Activates the current displayed phase.
						dispatch.execute(new ChangePhase(getCurrentProject().getId(), phase.getId()), new CommandResultHandler<ProjectDTO>() {

							@Override
							public void onCommandFailure(final Throwable e) {

								if (Log.isErrorEnabled()) {
									Log.error("The phase #" + phase.getId() + " hasn't be activated.", e);
								}

								N10N.warn(I18N.CONSTANTS.projectActivatePhaseError(), I18N.CONSTANTS.projectActivatePhaseErrorDetails());
							}

							@Override
							public void onCommandSuccess(final ProjectDTO result) {

								if (Log.isDebugEnabled()) {
									Log.debug("Phase #" + phase.getId() + " successfully activated.");
								}

								// Sets the new current project (after update).
								setCurrentProject(result);

								// Sets the new current displayed phase (not necessary the active one).
								for (final PhaseDTO phase : getCurrentProject().getPhases()) {
									if (phase.getId().equals(getCurrentDisplayedPhase().getId())) {
										setCurrentDisplayedPhase(phase);
									}
								}

								refreshDashboardAfterUpdate(reload);
							}
						});
					}
				});
		}
	}

	/**
	 * Refreshes the dashboard after an update of the project instance.
	 * 
	 * @param reload
	 *          If the current displayed phase must be reloaded.
	 */
	private void refreshDashboardAfterUpdate(boolean reload) {

		if (Log.isDebugEnabled()) {
			Log.debug("Refreshes the project dashboard.");
		}

		// Map the required element for the active phase from the current displayed phase map.
		activePhaseRequiredElements.clear();
		activePhaseRequiredElements.putAll(currentPhaseRequiredElements);

		// --
		// -- BANNER
		// --

		refreshProjectBanner();

		// --
		// -- TOOLBAR
		// --

		refreshActionsToolbar();

		// --
		// -- UPDATES TABS
		// --

		// Updates closed phases styles.
		for (final PhaseDTO phase : getCurrentProject().getPhases()) {
			final TabItem successorTabItem = tabItemsMap.get(phase.getPhaseModel().getId());
			if (phase.isEnded()) {
				successorTabItem.getHeader().addStyleName(PhasesView.PROJECT_PHASE_CLOSED);
			}
		}

		// Updates active phase styles.
		for (final TabItem item : view.getTabPanelPhases().getItems()) {
			item.getHeader().removeStyleName(PhasesView.PROJECT_PHASE_ACTIVE);
		}

		final PhaseDTO phase;
		if ((phase = getCurrentProject().getCurrentPhase()) != null) {

			// Updates active phase styles.
			tabItemsMap.get(phase.getPhaseModel().getId()).getHeader().addStyleName(PhasesView.PROJECT_PHASE_ACTIVE);

			// Enables successors tabs of the current phase.
			enableSuccessorsTabs();
		}

		if (reload) {
			loadPhaseOnTab(getCurrentDisplayedPhase());
		}
	}

	/**
	 * Internal class handling the modifications saving.
	 */
	private class SaveListener implements Listener<ButtonEvent> {

		@Override
		public void handleEvent(final ButtonEvent be) {
            view.getButtonSavePhase().disable();
			final UpdateProject updateProject = new UpdateProject(getCurrentProject().getId(), valueChanges);
            
			dispatch.execute(updateProject, new CommandResultHandler<VoidResult>() {

				@Override
				public void onCommandFailure(final Throwable caught) {

					N10N.warn(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError());

					currentPhaseRequiredElements.clearState();

					if (isActivePhase(getCurrentDisplayedPhase())) {
						activePhaseRequiredElements.clearState();
					}
				}

                @Override
                protected void onFunctionalException(FunctionalException exception) {
                    super.onFunctionalException(exception);
                    
                    view.getButtonSavePhase().setEnabled(true);
                }

				@Override
				public void onCommandSuccess(final VoidResult result) {

					N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.saveConfirm());
                    
					// Checks if there is any update needed to the local project instance.
					boolean refreshBanner = false;
					boolean coreVersionUpdated = false;
					
					for (final ValueEvent event : valueChanges) {
						if (event.getSource() instanceof DefaultFlexibleElementDTO) {
							updateCurrentProject(((DefaultFlexibleElementDTO) event.getSource()), event.getSingleValue());
							refreshBanner = true;
						}
						coreVersionUpdated |= event.getSourceElement().getAmendable();
					}

					clearChangedValues();

					currentPhaseRequiredElements.saveState();

					if (isActivePhase(getCurrentDisplayedPhase())) {
						activePhaseRequiredElements.saveState();
					}

					refreshActionsToolbar();

					if (refreshBanner) {
						refreshProjectBanner();
					}
					
					if(coreVersionUpdated) {
						eventBus.fireEvent(new UpdateEvent(UpdateEvent.CORE_VERSION_UPDATED));
					}
				}
			}, new LoadingMask(view.getTabPanelPhases()));
		}
	}

	/**
	 * Updates locally the DTO to avoid a remote server call.
	 * 
	 * @param element
	 *          The default flexible element.
	 * @param value
	 *          The new value.
	 */
	private void updateCurrentProject(final DefaultFlexibleElementDTO element, final String value) {

		final ProjectDTO currentProjectDTO = getCurrentProject();

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

					for (BudgetSubFieldDTO bf : budgetElement.getBudgetSubFields()) {
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
				break;

			default:
				// Nothing, unknown type.
				break;
		}
	}

}
