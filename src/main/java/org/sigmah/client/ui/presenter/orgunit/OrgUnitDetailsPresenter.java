package org.sigmah.client.ui.presenter.orgunit;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.widget.Layout;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchQueue;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.view.orgunit.OrgUnitDetailsView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.IterableGroupPanel;
import org.sigmah.client.ui.widget.form.IterableGroupPanel.IterableGroupItem;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateLayoutGroupIterations;
import org.sigmah.shared.command.UpdateLayoutGroupIterations.IterationChange;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.OrgUnitDetailsDTO;
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
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.ProfileUtils;
import org.sigmah.shared.util.ValueResultUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.computation.ComputationTriggerManager;
import org.sigmah.client.event.UpdateEvent;

/**
 * OrgUnit Details Presenter.
 */
public class OrgUnitDetailsPresenter extends AbstractOrgUnitPresenter<OrgUnitDetailsPresenter.View> implements IterableGroupPanel.Delegate {

	/**
	 * Presenter's view interface.
	 */
	public static interface View extends AbstractOrgUnitPresenter.View {

		ContentPanel getContentOrgUnitDetailsPanel();

		Button getSaveButton();

		Button getExcelExportButton();

		void setMainPanelWidget(final Widget widget);

	}

	/**
	 * List of values changes.
	 */
	private List<ValueEvent> valueChanges;
    
	private final Map<Integer, IterationChange> iterationChanges = new HashMap<Integer, IterationChange>();

	private final Map<Integer, IterableGroupItem> newIterationsTabItems = new HashMap<Integer, IterableGroupItem>();


    /**
	 * Listen to the values of flexible elements to update computated values.
	 */
	private ComputationTriggerManager computationTriggerManager;

	
	public OrgUnitDetailsPresenter(View view, ClientFactory factory) {
		super(view, factory);
		computationTriggerManager = factory.getComputationTriggerManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ORGUNIT_DETAILS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Initialization.
		valueChanges = new ArrayList<ValueEvent>();

		// Save action.
		view.getSaveButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {

				view.getSaveButton().disable();

				onSaveAction();

			}
		});

		// Excel action.
		view.getExcelExportButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(final ButtonEvent be) {

				final ServletUrlBuilder urlBuilder =
						new ServletUrlBuilder(factory.getAuthenticationProvider(), factory.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_ORG_UNIT);

				urlBuilder.addParameter(RequestParameter.ID, getOrgUnit().getId());

				ClientUtils.launchDownload(urlBuilder.toString());
			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// clear changes
		valueChanges.clear();
		view.getSaveButton().disable();

		// load view details
		load(getOrgUnit().getOrgUnitModel().getDetails());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean hasValueChanged() {
		return !valueChanges.isEmpty() || !iterationChanges.isEmpty();
	}

	/**
	 * Loads the presenter with the org unit details.
	 * 
	 * @param details
	 *          The details.
	 */
	private void load(OrgUnitDetailsDTO details) {
        
        // Prepare the manager of computation elements
		computationTriggerManager.prepareForOrgUnit(getOrgUnit());

		// Clear panel.
		view.getContentOrgUnitDetailsPanel().removeAll();

		// Layout.
		final LayoutDTO layout = details.getLayout();

		// Counts elements.
		int count = 0;
		for (final LayoutGroupDTO groupDTO : layout.getGroups()) {
			count += groupDTO.getConstraints().size();
		}

		if (count == 0) {
			// Default details page.
			view.setMainPanelWidget(new Label(I18N.CONSTANTS.projectDetailsNoDetails()));
			return;
		}

		final Grid gridLayout = new Grid(layout.getRowsCount(), layout.getColumnsCount());
		gridLayout.setCellPadding(0);
		gridLayout.setCellSpacing(0);
		gridLayout.setWidth("100%");

		final DispatchQueue queue = new DispatchQueue(dispatch, true);

		for (final LayoutGroupDTO groupLayout : layout.getGroups()) {

			// simple group
			if(!groupLayout.getHasIterations()) {

				FieldSet fieldSet = createGroupLayoutFieldSet(getOrgUnit(), groupLayout, queue, null, null, null);
				fieldSet.setHeadingHtml(groupLayout.getTitle());
				fieldSet.setCollapsible(true);
				fieldSet.setBorders(true);
				gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), fieldSet);
				continue;
			}

			final FieldSet fieldSet = (FieldSet) groupLayout.getWidget();
			gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), fieldSet);

			// iterative group
			final IterableGroupPanel tabPanel = Forms.iterableGroupPanel(dispatch, groupLayout, getOrgUnit(), ProfileUtils.isGranted(auth(), GlobalPermissionEnum.CREATE_ITERATIONS));
			tabPanel.setDelegate(this);
			fieldSet.add(tabPanel);

			tabPanel.setAutoHeight(true);
			tabPanel.setAutoWidth(true);
			tabPanel.setTabScroll(true);
			tabPanel.addStyleName("white-tab-body");
			tabPanel.setBorders(true);
			tabPanel.setBodyBorder(false);

			GetLayoutGroupIterations getIterations = new GetLayoutGroupIterations(groupLayout.getId(), getOrgUnit().getId(), -1);

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

						FieldSet tabSet = createGroupLayoutFieldSet(getOrgUnit(), groupLayout, iterationsQueue, iteration == null ? null : iteration.getId(), tabPanel, tab);

						tab.add(tabSet);
					}

					iterationsQueue.start();

					if(tabPanel.getItemCount() > 0) {
						tabPanel.setSelection(tabPanel.getItem(0));
					}

				}
			}, new LoadingMask(view.getContentOrgUnitDetailsPanel()));

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

		view.getSaveButton().enable();
	}

	@Override
	public void addIterationTabItem(int iterationId, IterableGroupItem tab) {
		newIterationsTabItems.put(iterationId, tab);
	}

	@Override
	public FieldSet createGroupLayoutFieldSet(FlexibleElementContainer container, LayoutGroupDTO groupLayout, DispatchQueue queue, final Integer iterationId, final IterableGroupPanel tabPanel, final IterableGroupItem tabItem) {
		final OrgUnitDTO orgUnit = (OrgUnitDTO)container;

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

				// Remote call to ask for this element value.
			GetValue getValue;

			getValue = new GetValue(orgUnit.getId(), elementDTO.getId(), elementDTO.getEntityName(), null, iterationId);

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
						elementDTO.setAuthenticationProvider(factory.getAuthenticationProvider());
						elementDTO.setEventBus(eventBus);
						elementDTO.setCache(factory.getClientCache());
					elementDTO.setCurrentContainerDTO(orgUnit);
						elementDTO.setTransfertManager(factory.getTransfertManager());
						elementDTO.assignValue(valueResult);
					elementDTO.setTabPanel(tabPanel);

						// Generates element component (with the value).
						elementDTO.init();
						final Component elementComponent = elementDTO.getElementComponent(valueResult);

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

								// Enables the save action.
								view.getSaveButton().enable();
							}
						});


					if(elementDTO.getValidates() && tabItem != null) {
						tabItem.setElementValidity(elementDTO, elementDTO.isCorrectRequiredValue(valueResult));
						tabItem.refreshTitle();
						elementDTO.addRequiredValueHandler(new RequiredValueHandlerImpl(elementDTO));
					}
					}
				}, new LoadingMask(view.getContentOrgUnitDetailsPanel()));
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
		dispatch.execute(new UpdateLayoutGroupIterations(new ArrayList<IterationChange>(iterationChanges.values()), getOrgUnit().getId()), new CommandResultHandler<ListResult<IterationChange>>() {

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

				dispatch.execute(new UpdateProject(getOrgUnit().getId(), valueChanges), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						N10N.error(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError());
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {

						N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.saveConfirm());

						// Checks if there is any update needed to the local project instance.

						for (final ValueEvent event : valueChanges) {
							if (event.getSource() instanceof DefaultFlexibleElementDTO) {
								updateCurrentProject(((DefaultFlexibleElementDTO) event.getSource()), event.getSingleValue());
							}
						}

						valueChanges.clear();

						eventBus.fireEvent(new UpdateEvent(UpdateEvent.VALUE_UPDATE, getOrgUnit()));

					}

				}, view.getSaveButton(), view.getExcelExportButton(), new LoadingMask(view.getContentOrgUnitDetailsPanel()));
			}
		}, view.getSaveButton(), view.getExcelExportButton(), new LoadingMask(view.getContentOrgUnitDetailsPanel()));
	}

	/**
	 * Updates locally the DTO to avoid a remote server call.
	 * 
	 * @param element
	 *          The default flexible element.
	 * @param value
	 *          The new value.
	 */
	private void updateCurrentProject(DefaultFlexibleElementDTO element, String value) {

		final OrgUnitDTO currentOrgUnitDTO = getOrgUnit();

		switch (element.getType()) {
			case CODE:
				currentOrgUnitDTO.setName(value);
				break;

			case TITLE:
				currentOrgUnitDTO.setFullName(value);
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

					currentOrgUnitDTO.setPlannedBudget(plannedBudget);
					currentOrgUnitDTO.setSpendBudget(spendBudget);
					currentOrgUnitDTO.setReceivedBudget(receivedBudget);

				} catch (final Exception e) {
					// nothing, invalid budget.
				}
				break;

			default:
				// Nothing, non managed type.
				break;
		}
	}

}
