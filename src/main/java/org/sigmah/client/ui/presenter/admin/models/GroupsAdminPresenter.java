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
import org.sigmah.client.ui.view.admin.models.GroupsAdminView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.HasGrid;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.GroupsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

public class GroupsAdminPresenter<E extends IsModel> extends AbstractPresenter<GroupsAdminPresenter.View> 
		implements IsModelTabPresenter<E, GroupsAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(GroupsAdminView.class)
	public static interface View extends ViewInterface, HasGrid<GroupsDTO> {

		Button getAddButton();

		Button getDeleteButton();

		void setToolbarEnabled(boolean enabled);

	}

	
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
	public GroupsAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void onBind() {

		// Grid events handler
		
		view.setGridEventHandler(new GridEventHandler<GroupsDTO>() {

			@Override
			public void onRowClickEvent(final GroupsDTO rowElement) {
				eventBus.navigateRequest(Page.ADMIN_EDIT_GROUPS.request().addData(RequestParameter.DTO, rowElement)
					.addData(RequestParameter.CONTENT, view.getStore().getModels()).addData(RequestParameter.MODEL, currentModel));
			}
		});

		// Grid selection change handler

	
		view.getGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<GroupsDTO>() {

			@Override
			public void selectionChanged(final SelectionChangedEvent<GroupsDTO> event) {
				final boolean singleSelection = ClientUtils.isNotEmpty(event.getSelection()) && event.getSelection().size() == 1;
				view.getDeleteButton().setEnabled(singleSelection && currentModel.isEditable());
			}
		});

		// Add button handler

		view.getAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				eventBus.navigateRequest(Page.ADMIN_EDIT_GROUPS.request().addData(RequestParameter.MODEL, currentModel));
			}
		});

		// Delete button handler

		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onDeleteAction(view.getGrid().getSelectionModel().getSelectedItem());
			}
		});

		// On groups model creation/update event.
		
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
		return I18N.CONSTANTS.adminProjectModelGroups();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadTab(final E model) {

		this.currentModel = model;
		view.setToolbarEnabled(currentModel.isEditable());

		// Populating the store and initializing Groups models default property.
		
		view.getStore().removeAll();

		for (final GroupsDTO groups : model.getGroups()) {
			
			view.getStore().add(groups);
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
	 * @param groups
	 *          The selected group to delete.
	 */
	private void onDeleteAction(final GroupsDTO groupModel) {

		if (currentModel == null || groupModel == null) {
			return;
		}

		if (!currentModel.isEditable()) {
			return;
		}


		final String confirmMessageDetails;
		if (currentModel.getGroups() == null || currentModel.getGroups().size() == 1) {
			// Cannot delete the last Group.
			confirmMessageDetails = I18N.CONSTANTS.deleteDefaultGroupsConfirm();
		} else {
			confirmMessageDetails = I18N.CONSTANTS.deleteGroupsConfirm();
		}

		N10N.confirmation(I18N.CONSTANTS.deleteConfirm(), confirmMessageDetails, new ConfirmCallback() {

			@Override
			public void onAction() {

				dispatch.execute(new Delete(GroupsDTO.ENTITY_NAME, groups.getId()), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						N10N.error(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(groups.getName()));
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {

						// Updates the Groups store.
						updateStore(groups);

						// Updates the current project model.
						currentModel.getGroups().clear();
						currentModel.getGroups().addAll(view.getStore().getModels());

						// Shows notification.
						N10N.infoNotif(I18N.CONSTANTS.deleteConfirm(), I18N.CONSTANTS.adminGroupsDeleteDetail());
					}
				});
			}
		});
	}
}
