package org.sigmah.client.ui.presenter.reminder;

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
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.reminder.ReminderEditView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Reminder/Monitored Point presenter which manages the {@link ReminderEditView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ReminderEditPresenter extends AbstractPagePresenter<ReminderEditPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ReminderEditView.class)
	public static interface View extends ViewInterface {

		/**
		 * Sets the initialization mode.
		 * 
		 * @param reminderType
		 *          The reminder type.
		 * @param creation
		 *          {@code true} if the view is initialized for creation, {@code false} for modification.
		 */
		void setInitializationMode(ReminderType reminderType, boolean creation);

		/**
		 * Loads the given {@code reminder} into the view's form.
		 * 
		 * @param reminder
		 *          The reminder entity. If {@code null}, the view process a reset.
		 */
		void loadReminder(ReminderDTO reminder);

		/**
		 * Loads the given {@code monitoredPoint} into the view's form.
		 * 
		 * @param monitoredPoint
		 *          The monitored point entity.
		 */
		void loadMonitoredPoint(MonitoredPointDTO monitoredPoint);

		FormPanel getForm();

		TextField<String> getLabelField();

		DateField getExpectedDateField();

		Button getSaveButton();

		Button getDeleteButton();

	}

	/**
	 * The related project id.<br>
	 * Should never be {@code null}.
	 */
	private Integer projectId;

	/**
	 * The reminder type: <em>reminder</em> or <em>monitored point</em>.<br>
	 * Should never be {@code null}.
	 */
	private ReminderType reminderType;

	/**
	 * The edited reminder/monitored point entity DTO.<br>
	 * Set to {@code null} in case of creation.
	 */
	private EntityDTO<Integer> entityDTO;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ReminderEditPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.REMINDER_EDIT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onDeleteAction(entityDTO);
			}
		});

		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {

				if (entityDTO == null) {
					// Creation.
					onCreateAction();

				} else {
					// Update.
					onUpdateAction(entityDTO);
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// --
		// View reset.
		// --

		view.getForm().clear();

		// --
		// Reads project id from request (mandatory).
		// --

		projectId = request.getParameterInteger(RequestParameter.ID);

		if (projectId == null) {
			if (Log.isErrorEnabled()) {
				Log.error("Project id is required.");
			}
			hideView();
			throw new IllegalArgumentException("Project id is required.");
		}

		// --
		// Reads reminder type from request (mandatory).
		// --

		reminderType = ReminderType.fromString(request.getParameter(RequestParameter.TYPE));

		if (reminderType == null) {
			if (Log.isErrorEnabled()) {
				Log.error("Project id is required.");
			}
			hideView();
			throw new IllegalArgumentException("Project id is required.");
		}

		// --
		// Reads entity from request (optional - only for update case).
		// --

		entityDTO = request.getData(RequestParameter.DTO);
		final boolean creation = entityDTO == null;

		// --
		// Updates view.
		// --

		view.setInitializationMode(reminderType, creation);

		// Sets the page title.
		setPageTitle(ReminderType.getTitle(reminderType, creation));

		if (creation) {
			return;
		}

		switch (reminderType) {

			case REMINDER:
				view.loadReminder((ReminderDTO) entityDTO);
				break;

			case MONITORED_POINT:
				view.loadMonitoredPoint((MonitoredPointDTO) entityDTO);
				break;

			default:
				hideView();
				throw new IllegalArgumentException("Invalid reminder type parameter.");
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Method executed on reminder/monitored point <b>create</b> action.
	 */
	private void onCreateAction() {

		if (!view.getForm().isValid()) {
			return;
		}

		// Read form values.
		final HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put(ReminderDTO.EXPECTED_DATE, view.getExpectedDateField().getValue().getTime());
		properties.put(ReminderDTO.LABEL, view.getLabelField().getValue());
		properties.put(ReminderDTO.PROJECT_ID, projectId);

		// RPC to update by using the command UpdateEntity
		dispatch.execute(new CreateEntity(ReminderType.getEntityName(reminderType), properties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {

				if (Log.isErrorEnabled()) {
					Log.error("Error while updating a reminder or monitored point.", caught);
				}

				if (reminderType == ReminderType.REMINDER) {
					N10N.warn(I18N.CONSTANTS.monitoredPointAddError(), I18N.CONSTANTS.reminderAddErrorDetails());
				} else {
					N10N.warn(I18N.CONSTANTS.monitoredPointAddError(), I18N.CONSTANTS.monitoredPointAddErrorDetails());
				}
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				// Send an event to refresh the grid.
				fireUpdateEvent();

				// Hides view and displays notification.
				hideView();

				if (reminderType == ReminderType.REMINDER) {
					N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.reminderAddConfirm(), MessageType.INFO);
				} else {
					N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.monitoredPointAddConfirm(), MessageType.INFO);
				}
			}
		}, view.getSaveButton(), view.getDeleteButton());
	}

	/**
	 * Method executed on reminder/monitored point <b>update</b> action.
	 * 
	 * @param entityDTO
	 *          The entity DTO (reminder or monitored point) to update. Should never be {@code null} in case of update.
	 * @throws UnsupportedOperationException
	 *           If the given {@code entityDTO} is {@code null}.
	 */
	private void onUpdateAction(final EntityDTO<Integer> entityDTO) {

		if (entityDTO == null || entityDTO.getId() == null) {
			throw new UnsupportedOperationException("Updated reminder entity DTO is invalid.");
		}

		if (!view.getForm().isValid()) {
			return;
		}

		final Boolean deleted;

		if (entityDTO instanceof ReminderDTO) {
			deleted = ((ReminderDTO) entityDTO).getDeleted();

		} else if (entityDTO instanceof MonitoredPointDTO) {
			deleted = ((MonitoredPointDTO) entityDTO).getDeleted();

		} else {
			deleted = null;
		}

		// Read form values.
		final HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put(ReminderDTO.EXPECTED_DATE, view.getExpectedDateField().getValue().getTime());
		properties.put(ReminderDTO.LABEL, view.getLabelField().getValue());
		properties.put(ReminderDTO.DELETED, deleted);
		properties.put(ReminderDTO.PROJECT_ID, projectId);

		// RPC to update by using the command UpdateEntity
		dispatch.execute(new UpdateEntity(entityDTO, properties), new CommandResultHandler<VoidResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {

				if (Log.isErrorEnabled()) {
					Log.error("Error while updating a reminder or monitored point.", caught);
				}

				if (reminderType == ReminderType.REMINDER) {
					N10N.warn(I18N.CONSTANTS.reminderUpdateError(), I18N.CONSTANTS.reminderUpdateErrorDetails());
				} else {
					N10N.warn(I18N.CONSTANTS.monitoredPointUpdateError(), I18N.CONSTANTS.monitoredPointUpdateErrorDetails());
				}
			}

			@Override
			public void onCommandSuccess(final VoidResult result) {

				// Send an event to refresh the grid.
				fireUpdateEvent();

				// Hides view and displays notification.
				hideView();

				if (reminderType == ReminderType.REMINDER) {
					N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.reminderUpdateConfirm(), MessageType.INFO);
				} else {
					N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.monitoredPointUpdateConfirm(), MessageType.INFO);
				}
			}
		}, view.getSaveButton(), view.getDeleteButton());
	}

	/**
	 * Method executed on reminder/monitored point delete action.
	 * 
	 * @param entityDTO
	 *          The deleted entity DTO (reminder or monitored point).
	 * @throws UnsupportedOperationException
	 *           If the given {@code entityDTO} is {@code null}.
	 */
	private void onDeleteAction(final EntityDTO<Integer> entityDTO) {

		if (entityDTO == null) {
			if (Log.isErrorEnabled()) {
				Log.error("Reminder delete action should not be avalaible in case of creation.");
			}
			throw new UnsupportedOperationException("Reminder delete action should not be avalaible in case of creation.");
		}

		if (!view.getForm().isValid()) {
			return;
		}

		N10N.confirmation(I18N.CONSTANTS.deleteConfirm(), I18N.CONSTANTS.deleteConfirmMessage(), new ConfirmCallback() {

			@Override
			public void onAction() {

				final String label;
				final Date expectedDate;

				if (entityDTO instanceof ReminderDTO) {
					label = ((ReminderDTO) entityDTO).getLabel();
					expectedDate = ((ReminderDTO) entityDTO).getExpectedDate();

				} else if (entityDTO instanceof MonitoredPointDTO) {
					label = ((MonitoredPointDTO) entityDTO).getLabel();
					expectedDate = ((MonitoredPointDTO) entityDTO).getExpectedDate();

				} else {
					throw new UnsupportedOperationException("Invalid reminder type parameter: '" + entityDTO + "'.");
				}

				final HashMap<String, Object> properties = new HashMap<String, Object>();
				properties.put(ReminderDTO.EXPECTED_DATE, expectedDate.getTime());
				properties.put(ReminderDTO.LABEL, label);
				properties.put(ReminderDTO.DELETED, Boolean.TRUE);

				// RPC to update by using the command UpdateEntity.
				dispatch.execute(new UpdateEntity(entityDTO, properties), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						if (reminderType == ReminderType.REMINDER) {
							N10N.error(I18N.CONSTANTS.deletionError(), I18N.CONSTANTS.reminderDeletionErrorDetails());
						} else {
							N10N.error(I18N.CONSTANTS.deletionError(), I18N.CONSTANTS.monitoredPointDeletionErrorDetails());
						}
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {

						// Send an event to refresh the grid.
						fireUpdateEvent();

						// Hides view and displays notification.
						hideView();

						if (reminderType == ReminderType.REMINDER) {
							N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.reminderDeletionConfirm(), MessageType.INFO);
						} else {
							N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.monitoredPointDeletionConfirm(), MessageType.INFO);
						}
					}
				}, view.getSaveButton(), view.getDeleteButton());

			}
		});
	}

	/**
	 * Fires the update event to notify registered presenter(s).
	 */
	private void fireUpdateEvent() {
		eventBus.fireEvent(new UpdateEvent(UpdateEvent.REMINDER_UPDATED, reminderType));
	}

}
