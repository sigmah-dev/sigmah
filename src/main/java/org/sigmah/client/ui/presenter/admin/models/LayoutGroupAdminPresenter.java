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
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.admin.models.LayoutGroupAdminView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.HasGrid;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.client.ui.presenter.admin.models.base.IsModelTabPresenter;
import org.sigmah.shared.command.DeleteGroups;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

public class LayoutGroupAdminPresenter<E extends IsModel> extends 
AbstractPresenter<LayoutGroupAdminPresenter.View> 
		implements IsModelTabPresenter<E, LayoutGroupAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(LayoutGroupAdminView.class)
	public static interface View extends ViewInterface, HasGrid<LayoutGroupDTO> {
        void setModelEditable(final boolean editable);
		void setToolbarEnabled(boolean enabled);

		Button getAddButton();

		Button getDeleteButton();

	}

	public static final String ON_GROUP_CLICK_EVENT_KEY = "_GROUP_EVENT_";

	// The current model.
	
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
	public LayoutGroupAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void onBind() {

		// Grid events handler
		
		view.setGridEventHandler(new GridEventHandler<LayoutGroupDTO>() {

			@Override
			public void onRowClickEvent(final LayoutGroupDTO rowElement) {
                if (ClientUtils.isTrue(rowElement.get(ON_GROUP_CLICK_EVENT_KEY))) {
					// Group label event.
					eventBus.navigateRequest(Page.ADMIN_EDIT_LAYOUT_GROUP_MODEL.request().addData(RequestParameter.MODEL, currentModel)
						.addData(RequestParameter.DTO, rowElement));

				} else {
                    eventBus.navigateRequest(Page.ADMIN_EDIT_LAYOUT_GROUP_MODEL.request().addData(RequestParameter.MODEL, currentModel)
						.addData(RequestParameter.DTO, rowElement));
					}
			}
		});
		// --
		// Grid selection change handler.
		// --

		view.getGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<LayoutGroupDTO>() {

			@Override
			public void selectionChanged(final SelectionChangedEvent<LayoutGroupDTO> event) {
				final boolean enabled = ClientUtils.isNotEmpty(event.getSelection());
				
				view.getDeleteButton().setEnabled(enabled);
				}
		});

		// Add button handler

		view.getAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				eventBus.navigateRequest(Page.ADMIN_EDIT_LAYOUT_GROUP_MODEL.request().addData(RequestParameter.MODEL, currentModel));
			}
		});


		// Delete button handler

		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onGroupsDeleteAction(view.getGrid().getSelectionModel
().getSelection());
			}
		});

		// On groups model creation/update event.
		
		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() 
{

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.LAYOUT_GROUP_UPDATE)) {
					final boolean update = event.getParam(1);
					final LayoutGroupDTO updatedOrCreatedElement = 
event.getParam(2);
					onGroupsUpdate(update, updatedOrCreatedElement);
				}
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.adminGroups();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadTab(final E model) {

		this.currentModel = model;
        view.setModelEditable(model.isEditable());
		view.setToolbarEnabled(model.getStatus() != null && model.isEditable());
		view.getDeleteButton().setVisible(!model.isUnderMaintenance());		
		view.getStore().removeAll();
       // view.getStore().add(model.getAllEs());
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
	 * @param groups
	 *          The selected group to delete.
	 */
    private void onGroupsUpdate(final boolean udpate, final LayoutGroupDTO updatedOrCreatedElement) {

		if (udpate) {
			view.getStore().update(updatedOrCreatedElement);

		} else {
			view.getStore().add(updatedOrCreatedElement);
		}

		view.getStore().commitChanges();
	}
    
	private void onGroupsDeleteAction(final List<LayoutGroupDTO> selection) {

		if (ClientUtils.isEmpty(selection)) {
			N10N.warn(I18N.CONSTANTS.delete(), I18N.MESSAGES.adminFlexibleDeleteNone());
			return;
		}

		final List<String> elementNames = new ArrayList<String>();
		
		for (final LayoutGroupDTO element : selection) {
			elementNames.add(element.getTitle());
		}
		N10N.confirmation(I18N.CONSTANTS.delete(), I18N.CONSTANTS.adminFlexibleConfirmDelete(), elementNames, new ConfirmCallback() {

			@Override
			public void onAction() {

				dispatch.execute(new DeleteGroups(selection), new CommandResultHandler<VoidResult>() {

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
						for (final LayoutGroupDTO element : selection) {
							view.getStore().remove(element);
						}

						// Notification.
						N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminFlexibleDeleteFlexibleElementsConfirm());

					}
				});
			}
		});
	}
}
