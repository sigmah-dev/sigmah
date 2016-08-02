package org.sigmah.client.ui.presenter.admin.models;

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
import java.util.List;

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
import org.sigmah.client.ui.view.admin.models.FlexibleElementsAdminView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.HasGrid;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.DeleteFlexibleElements;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import java.util.Collection;
import java.util.Date;
import org.sigmah.shared.command.DisableFlexibleElements;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.computation.Computations;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.SingleDependency;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.util.Collections;

/**
 * Model's flexible elements administration presenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FlexibleElementsAdminPresenter<E extends IsModel> extends AbstractPresenter<FlexibleElementsAdminPresenter.View> 
		implements IsModelTabPresenter<E, FlexibleElementsAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(FlexibleElementsAdminView.class)
	public static interface View extends ViewInterface, HasGrid<FlexibleElementDTO> {

		void setModelEditable(final boolean editable);

		void setToolbarEnabled(final boolean enabled);

		Button getAddButton();

		Button getAddGroupButton();

		Button getDeleteButton();
		
		Button getEnableButton();
		
		Button getDisableButton();

	}

	/**
	 * "On flexible element group" click event key used to differentiate flexible elements grid events.
	 */
	public static final String ON_GROUP_CLICK_EVENT_KEY = "_GROUP_EVENT_";

	/**
	 * The provided current model.
	 */
	private E currentModel;

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view managed by this presenter.
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	public FlexibleElementsAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Grid selection change handler.
		// --

		view.getGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<FlexibleElementDTO>() {

			@Override
			public void selectionChanged(final SelectionChangedEvent<FlexibleElementDTO> event) {
				final boolean enabled = ClientUtils.isNotEmpty(event.getSelection());
				
				view.getDeleteButton().setEnabled(enabled);
				view.getEnableButton().setEnabled(enabled);
				view.getDisableButton().setEnabled(enabled);
			}
		});

		// --
		// Grid events handler.
		// --

		view.setGridEventHandler(new GridEventHandler<FlexibleElementDTO>() {

			@Override
			public void onRowClickEvent(final FlexibleElementDTO rowElement) {
				if (ClientUtils.isTrue(rowElement.get(ON_GROUP_CLICK_EVENT_KEY))) {
					// Group label event.
					eventBus.navigateRequest(Page.ADMIN_EDIT_LAYOUT_GROUP_MODEL.request().addData(RequestParameter.MODEL, currentModel)
						.addData(RequestParameter.DTO, rowElement));

				} else {
					eventBus.navigateRequest(Page.ADMIN_EDIT_FLEXIBLE_ELEMENT.request().addData(RequestParameter.MODEL, currentModel)
						.addData(RequestParameter.DTO, rowElement)
						.addData(RequestParameter.ELEMENTS, view.getStore().getModels()));
				}
			}
		});

		// --
		// Add button handler.
		// --

		view.getAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				eventBus.navigateRequest(Page.ADMIN_EDIT_FLEXIBLE_ELEMENT.request().addData(RequestParameter.MODEL, currentModel)
						.addData(RequestParameter.ELEMENTS, view.getStore().getModels()));
			}
		});

		// --
		// Add Group button handler.
		// --

		view.getAddGroupButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				eventBus.navigateRequest(Page.ADMIN_EDIT_LAYOUT_GROUP_MODEL.request().addData(RequestParameter.MODEL, currentModel));
			}
		});

		// --
		// Delete button handler.
		// --

		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onFlexibleElementDeleteAction(view.getGrid().getSelectionModel().getSelection());
			}
		});

		// --
		// Disable button handler.
		// --

		view.getDisableButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onFlexibleElementDisableAction(view.getGrid().getSelectionModel().getSelection());
			}
		});

		// --
		// Enable button handler.
		// --

		view.getEnableButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onFlexibleElementEnableAction(view.getGrid().getSelectionModel().getSelection());
			}
		});

		// --
		// Flexible element creation/update event handler.
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.FLEXIBLE_ELEMENT_UPDATE)) {
					final boolean update = event.getParam(1);
					final FlexibleElementDTO updatedOrCreatedElement = event.getParam(2);
					onFlexibleElementUpdate(update, updatedOrCreatedElement);
				}
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.adminProjectModelFields();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadTab(final E model) {

		this.currentModel = model;

		view.setToolbarEnabled(model.getStatus() != null && model.isEditable());

		view.setModelEditable(model.isEditable());
		
		view.getDeleteButton().setVisible(!model.isUnderMaintenance());
		view.getEnableButton().setVisible(model.isUnderMaintenance());
		view.getDisableButton().setVisible(model.isUnderMaintenance());
		
		view.getStore().removeAll();
		view.getStore().add(model.getAllElements());
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
	 * Find the computation fields using the given flexible element.
	 *
	 * @param flexibleElement
	 *          Flexible element.
	 * @return A collection of every computation element using the given element.
	 */
	private Collection<ComputationElementDTO> getComputationElementsUsingFields(final List<FlexibleElementDTO> flexibleElements) {

		final ArrayList<ComputationElementDTO> computationElements = new ArrayList<ComputationElementDTO>();
		final List<FlexibleElementDTO> allElements = view.getStore().getModels();

		for (final FlexibleElementDTO other : allElements) {
			if (other instanceof ComputationElementDTO) {
				final ComputationElementDTO computationElement = (ComputationElementDTO) other;

				final Computation computation = Computations.parse(computationElement.getRule(), allElements);
				final List<FlexibleElementDTO> dependencies = Collections.map(computation.getDependencies(), new Collections.OptionnalMapper<Dependency, FlexibleElementDTO>() {
					
					@Override
					public boolean skipEntry(Dependency entry) {
						return entry instanceof SingleDependency;
					}

					@Override
					public FlexibleElementDTO forEntry(Dependency entry) {
						return ((SingleDependency) entry).getFlexibleElement();
					}
				});
				
				if (Collections.containsOneOf(dependencies, flexibleElements)) {
					computationElements.add(computationElement);
				}
			}
		}

		return computationElements;
	}

	/**
	 * Returns a message to add to the warning displayed when removing or disabling the given elements.
	 *
	 * @param selection
	 *          Selection of elements to delete/disable.
	 * @return A warning message.
	 */
	private String getAdditionnalWarning(final List<FlexibleElementDTO> selection) {

		final String additionnalWarning;

		final Collection<ComputationElementDTO> relatedComputationElements = getComputationElementsUsingFields(selection);
		if (!relatedComputationElements.isEmpty()) {
			additionnalWarning = "<br/><br/>"
					+ I18N.MESSAGES.confirmDeleteWhenRelatedComputationElementsExists(
							Collections.join(relatedComputationElements, new Collections.Mapper<ComputationElementDTO, String>() {

								@Override
								public String forEntry(ComputationElementDTO entry) {
									return "<span style=\"font-weight: bold\">" + entry.getLabel() + "</span>";
								}
							}, ", "))
					+ "<br/><br/>";
		} else {
			additionnalWarning = "";
		}
		return additionnalWarning;
	}

	/**
	 * Callback executed on flexible element creation/update event.
	 * 
	 * @param udpate
	 *          Flag set to {@code true} in case of update.
	 * @param updatedOrCreatedElement
	 *          The updated or created flexible element.
	 */
	private void onFlexibleElementUpdate(final boolean udpate, final FlexibleElementDTO updatedOrCreatedElement) {

		if (udpate) {
			view.getStore().update(updatedOrCreatedElement);

		} else {
			view.getStore().add(updatedOrCreatedElement);
		}

		view.getStore().commitChanges();
	}

	/**
	 * Callback executed on flexible element delete action.<br>
	 * Handles confirmation message.
	 * 
	 * @param selection
	 *          The selected flexible element(s).
	 */
	private void onFlexibleElementDeleteAction(final List<FlexibleElementDTO> selection) {

		if (ClientUtils.isEmpty(selection)) {
			N10N.warn(I18N.CONSTANTS.delete(), I18N.MESSAGES.adminFlexibleDeleteNone());
			return;
		}

		final List<String> elementNames = new ArrayList<String>();
		final List<String> defaultElementNames = new ArrayList<String>();

		for (final FlexibleElementDTO element : selection) {
			elementNames.add(element.getFormattedLabel());

			if (element instanceof DefaultFlexibleElementDTO) {
				defaultElementNames.add(DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO) element).getType()));
			}
		}

		if (ClientUtils.isNotEmpty(defaultElementNames)) {
			N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.adminErrorDeleteDefaultFlexible(), defaultElementNames);
			return;
		}

		N10N.confirmation(I18N.CONSTANTS.delete(), I18N.CONSTANTS.adminFlexibleConfirmDelete() + getAdditionnalWarning(selection), elementNames, new ConfirmCallback() {

			@Override
			public void onAction() {

				dispatch.execute(new DeleteFlexibleElements(selection), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {

						final StringBuilder builder = new StringBuilder();
						for (final String deleted : elementNames) {
							if (builder.length() > 0) {
								builder.append(", ");
							}
							builder.append(deleted);
						}

						N10N.warn(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(builder.toString()));
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {

						// Updates the store.
						for (final FlexibleElementDTO element : selection) {
							view.getStore().remove(element);
						}

						// Notification.
						N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminFlexibleDeleteFlexibleElementsConfirm());

						// FIXME (v1.3) update model
					}
				});
			}
		});
	}

	/**
	 * Callback executed on flexible element disable action.<br>
	 * Handles confirmation message.
	 * 
	 * @param selection
	 *          The selected flexible element(s).
	 */
	private void onFlexibleElementDisableAction(final List<FlexibleElementDTO> selection) {
		
		final StringBuilder fields = new StringBuilder();
		final StringBuilder unableToDisableFields = new StringBuilder();
		
		for (FlexibleElementDTO s : selection) {
			if (!s.getAmendable() && !(s instanceof DefaultFlexibleElementDTO)) {
				if(fields.length() > 0) {
					fields.append(", ");
				}
				fields.append(s.getLabel());
				
			} else {
				if(unableToDisableFields.length() > 0) {
					unableToDisableFields.append(", ");
				}
				unableToDisableFields.append(s.getFormattedLabel());
			}
		}
		
		if (unableToDisableFields.length() == 0) {
	        dispatch.execute(new DisableFlexibleElements(selection, true), new AsyncCallback<VoidResult>() {
	            @Override
	            public void onFailure(Throwable caught) {
	                N10N.error(I18N.CONSTANTS.error(), I18N.MESSAGES.flexibleElementDisableError(fields.toString()));
	            }

	            @Override
	            public void onSuccess(VoidResult result) {
	            	// update view   
	            	for (FlexibleElementDTO element : selection) {
						element.setDisabledDate(new Date());
						
	            		view.getStore().update(element);
	            	}
	            	
	            	// Feedback 
	            	N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), 
						I18N.CONSTANTS.adminFlexibleDisableFlexibleElementsConfirm());
	            }
	        });
		} else {
			N10N.error(I18N.CONSTANTS.error(), I18N.MESSAGES.adminErrorDisableDefaultOrAmendableFlexible(unableToDisableFields.toString()));
		}
	}

	/**
	 * Callback executed on flexible element enable action.<br>
	 * Handles confirmation message.
	 * 
	 * @param selection
	 *          The selected flexible element(s).
	 */
	private void onFlexibleElementEnableAction(final List<FlexibleElementDTO> selection) {
		
		final StringBuilder fields = new StringBuilder();
		
		for (FlexibleElementDTO s : selection) {
			if(fields.length() > 0) {
				fields.append(", ");
			}
			fields.append(s.getLabel());
		}
		
		dispatch.execute(new DisableFlexibleElements(selection, false), new AsyncCallback<VoidResult>() {
			@Override
			public void onFailure(Throwable caught) {
				N10N.error(I18N.CONSTANTS.error(), I18N.MESSAGES.flexibleElementDisableError(fields.toString()));
			}

			@Override
			public void onSuccess(VoidResult result) {
				// update view   
				for (FlexibleElementDTO element : selection) {
					element.setDisabledDate(null);

					view.getStore().update(element);
				}
				
				// Feedback 
				N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), 
						I18N.CONSTANTS.adminFlexibleEnableFlexibleElementsConfirm());
			}
		});
	}
}
