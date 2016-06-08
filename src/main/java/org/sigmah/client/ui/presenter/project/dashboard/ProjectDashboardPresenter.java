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


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.CreateProjectPresenter;
import org.sigmah.client.ui.presenter.project.AbstractProjectPresenter;
import org.sigmah.client.ui.presenter.reminder.ReminderType;
import org.sigmah.client.ui.view.project.dashboard.ProjectDashboardView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.GetLinkedProjects;
import org.sigmah.shared.command.GetMonitoredPoints;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.UpdateMonitoredPoints;
import org.sigmah.shared.command.UpdateReminders;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.dto.referential.ReminderChangeType;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointHistoryDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.reminder.ReminderHistoryDTO;
import org.sigmah.shared.util.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Record.RecordUpdate;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.ui.view.project.dashboard.LinkedProjectsColumnsProvider;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;

/**
 * Project's Dashboard presenter which manages the {@link ProjectDashboardView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class ProjectDashboardPresenter extends AbstractProjectPresenter<ProjectDashboardPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectDashboardView.class)
	public static interface View extends AbstractProjectPresenter.View {

		/**
		 * Provides specific handlers implementation to the view.
		 * 
		 * @param handler
		 *          The specific handlers implementation.
		 */
		void setPresenterHandler(PresenterHandler handler);

		// --
		// Reminders/Monitored points.
		// --

		Grid<ReminderDTO> getRemindersGrid();

		Grid<MonitoredPointDTO> getMonitoredPointsGrid();

		Button getReminderAddButton();

		Button getMonitoredPointsAddButton();

		/**
		 * Updates the reminders and monitored points toolbars.
		 * 
		 * @param canEditReminders
		 *          {@code true} if the authenticated user is authorized to edit reminders.
		 * @param canEditMonitoredPoints
		 *          {@code true} if the authenticated user is authorized to edit monitored points.
		 */
		void updateRemindersToolbars(final boolean canEditReminders, final boolean canEditMonitoredPoints);

		// --
		// Linked projects (funding / funded).
		// --

		/**
		 * Updates the linked projects toolbars.
		 * 
		 * @param canRelateProject
		 *          {@code true} if the authenticated user is authorized to create relations between projects.
		 * @param canCreateProject
		 *          {@code true} if the authenticated user is authorized to create projects.
		 */
		void updateLinkedProjectsToolbars(boolean canRelateProject, boolean canCreateProject);

		// --
		// Funding projects.
		// --

		Grid<ProjectFundingDTO> getFundingProjectsGrid();

		Button getFundingProjectSelectButton();

		Button getFundingProjectCreateButton();

		// --
		// Funded projects.
		// --

		Grid<ProjectFundingDTO> getFundedProjectsGrid();

		Button getFundedProjectSelectButton();

		Button getFundedProjectCreateButton();
		
		LinkedProjectsColumnsProvider getFundingProjectsColumnsProvider();

		// --
		// Phases widget.
		// --

		PhasesPresenter getPhasesWidget();

		void layoutView();

	}

	/**
	 * Presenter's specific handlers.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static interface PresenterHandler {

		boolean isAuthor(EntityDTO<?> reminderOrMonitoredPoint);

		boolean isAuthorizedToEditReminder();

		void onLabelClickEvent(final EntityDTO<?> reminderOrMonitoredPoint);

		void onShowHistoryEvent(final EntityDTO<?> selectedReminderOrMonitoredPoint);

		ProjectModelType getProjectModelType(ProjectDTO project);

		void onLinkedProjectClickEvent(ProjectDTO project);

		void onLinkedProjectEditClickEvent(ProjectFundingDTO projectFunding, LinkedProjectType type);

	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ProjectDashboardPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.PROJECT_DASHBOARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Updates reminders and monitored points grids plugin.
		// TODO Grid plugins cannot be dynamically added/removed (component must NOT be rendered yet). Try to fix this.
		// --

		final CheckColumnConfig remindersCheckPlugin = (CheckColumnConfig) view.getRemindersGrid().getColumnModel().getColumn(0);

		// Removed the need to have "EDIT_PROJECT" privilege to see reminders.
		if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_ALL_REMINDERS) || ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_OWN_REMINDERS)) {
			view.getRemindersGrid().addPlugin(remindersCheckPlugin);
		}

		final CheckColumnConfig monitoredPointsCheckPlugin = (CheckColumnConfig) view.getMonitoredPointsGrid().getColumnModel().getColumn(0);

		if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_ALL_REMINDERS) || ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_OWN_REMINDERS)) {
			view.getMonitoredPointsGrid().addPlugin(monitoredPointsCheckPlugin);
		}

		// --
		// Reminders / Monitored Points add buttons handlers.
		// --

		view.getReminderAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				eventBus.navigateRequest(Page.REMINDER_EDIT.requestWith(RequestParameter.TYPE, ReminderType.REMINDER).addParameter(RequestParameter.ID,
					getProject().getId()));
			}
		});

		view.getMonitoredPointsAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				eventBus.navigateRequest(Page.REMINDER_EDIT.requestWith(RequestParameter.TYPE, ReminderType.MONITORED_POINT).addParameter(RequestParameter.ID,
					getProject().getId()));
			}
		});

		// --
		// Reminders / Monitored Points edit update event handlers.
		// --

		view.getRemindersGrid().getStore().addListener(Store.Update, new Listener<StoreEvent<ReminderDTO>>() {

			@Override
			public void handleEvent(final StoreEvent<ReminderDTO> event) {

				// Manages only edit event.
				if (event.getOperation() == RecordUpdate.EDIT) {
					onReminderUpdate(event.getModel());
				}
			}
		});

		view.getMonitoredPointsGrid().getStore().addListener(Store.Update, new Listener<StoreEvent<MonitoredPointDTO>>() {

			@Override
			public void handleEvent(final StoreEvent<MonitoredPointDTO> event) {

				// Manages only edit event.
				if (event.getOperation() == RecordUpdate.EDIT) {
					onMonitoredPointUpdate(event.getModel());
				}
			}
		});

		// --
		// Presenter's specific handler implementation provided to the view.
		// --

		view.setPresenterHandler(new PresenterHandler() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isAuthorizedToEditReminder() {
				// BUGFIX #741: Removed the need to have "EDIT_PROJECT" privilege to edit reminders.
				return ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_ALL_REMINDERS) || 
					ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_OWN_REMINDERS);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isAuthor(final EntityDTO<?> reminderOrMonitoredPoint) {

				boolean creator = false;

				if (reminderOrMonitoredPoint instanceof ReminderDTO) {

					final ReminderDTO reminder = (ReminderDTO) reminderOrMonitoredPoint;

					for (final ReminderHistoryDTO hist : reminder.getHistory()) {
						if (hist.getType() == ReminderChangeType.CREATED) {
							creator = auth().getUserId().equals(hist.getUserId());
						}
					}

				} else if (reminderOrMonitoredPoint instanceof MonitoredPointDTO) {

					final MonitoredPointDTO monitoredPoint = (MonitoredPointDTO) reminderOrMonitoredPoint;

					for (final MonitoredPointHistoryDTO hist : monitoredPoint.getHistory()) {
						if (hist.getType() == ReminderChangeType.CREATED) {
							creator = auth().getUserId().equals(hist.getUserId());
						}
					}
				}

				return creator && ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_OWN_REMINDERS)
					|| ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_ALL_REMINDERS);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void onLabelClickEvent(final EntityDTO<?> reminderOrMonitoredPoint) {
				// Navigating to reminder edit presenter.
				eventBus.navigateRequest(Page.REMINDER_EDIT.requestWith(RequestParameter.TYPE, ReminderType.fromDTO(reminderOrMonitoredPoint))
					.addParameter(RequestParameter.ID, getProject().getId()).addData(RequestParameter.DTO, reminderOrMonitoredPoint));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void onShowHistoryEvent(final EntityDTO<?> reminderOrMonitoredPoint) {

				if (reminderOrMonitoredPoint == null) {
					return;
				}

				// Navigating to reminder history presenter.
				eventBus.navigateRequest(Page.REMINDER_HISTORY.request().addData(RequestParameter.DTO, reminderOrMonitoredPoint));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public ProjectModelType getProjectModelType(final ProjectDTO project) {
				return project.getProjectModelType(auth().getOrganizationId());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void onLinkedProjectClickEvent(final ProjectDTO project) {
				eventBus.navigateRequest(Page.PROJECT_DASHBOARD.requestWith(RequestParameter.ID, project.getId()));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void onLinkedProjectEditClickEvent(final ProjectFundingDTO projectFunding, final LinkedProjectType type) {
				eventBus.navigateRequest(Page.LINKED_PROJECT.requestWith(RequestParameter.TYPE, type).addData(RequestParameter.HEADER, getProject())
					.addData(RequestParameter.DTO, projectFunding));
			}

		});

		// --
		// Registers presenter to update event for reminders/monitored points.
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.REMINDER_UPDATED)) {

					// --
					// On reminder/monitored point update/delete event.
					// --

					final ReminderType reminderType = event.getParam(0);

					if (reminderType == ReminderType.REMINDER) {
						loadReminders();

					} else if (reminderType == ReminderType.MONITORED_POINT) {
						loadMonitoredPoints();
					}

				} else if (event.concern(UpdateEvent.LINKED_PROJECT_UPDATE)) {

					// --
					// On linked project creation/update event.
					// --

					final LinkedProjectType linkedProjectType = event.getParam(0);
					loadLinkedProjects(linkedProjectType);

				} else if (event.concern(UpdateEvent.LINKED_PROJECT_DELETE)) {

					// --
					// On linked project delete event.
					// --

					final LinkedProjectType linkedProjectType = event.getParam(0);
					final ProjectFundingDTO linkedProject = event.getParam(1);

					onLinkedProjectDeleteAction(linkedProjectType, linkedProject);
					
				} else if (event.concern(UpdateEvent.PROJECT_CREATE)) {
					
					// --
					// On project creation event.
					// --
					
					final CreateProjectPresenter.Mode mode = event.getParam(0);
					
					switch (mode) {
						case FUNDING_ANOTHER_PROJECT:
							loadLinkedProjects(LinkedProjectType.FUNDING_PROJECT);
							break;
							
						case FUNDED_BY_ANOTHER_PROJECT:
							loadLinkedProjects(LinkedProjectType.FUNDED_PROJECT);
							break;
							
						default:
							// Nothing to do.
							break;
					}
				}
			}
		}));

		// --
		// Linked projects listeners (funding / funded).
		// --

		view.getFundingProjectSelectButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				eventBus.navigateRequest(Page.LINKED_PROJECT.requestWith(RequestParameter.TYPE, LinkedProjectType.FUNDING_PROJECT).addData(RequestParameter.HEADER,
					getProject()));
			}
		});

		view.getFundingProjectCreateButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				eventBus.navigateRequest(Page.CREATE_PROJECT.requestWith(RequestParameter.TYPE, CreateProjectPresenter.Mode.FUNDING_ANOTHER_PROJECT).addData(
					RequestParameter.DTO, getProject()));
			}
		});

		view.getFundedProjectSelectButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				eventBus.navigateRequest(Page.LINKED_PROJECT.requestWith(RequestParameter.TYPE, LinkedProjectType.FUNDED_PROJECT).addData(RequestParameter.HEADER,
					getProject()));
			}
		});

		view.getFundedProjectCreateButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				eventBus.navigateRequest(Page.CREATE_PROJECT.requestWith(RequestParameter.TYPE, CreateProjectPresenter.Mode.FUNDED_BY_ANOTHER_PROJECT).addData(
					RequestParameter.DTO, getProject()));
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// --
		// Updates reminders and monitored points toolbars.
		// --
		final boolean canEditReminders =
			ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_ALL_REMINDERS) || 
			ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_OWN_REMINDERS);

		final boolean canEditMonitoredPoints =
			ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_ALL_REMINDERS) || 
			ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_OWN_REMINDERS);

		view.updateRemindersToolbars(canEditReminders, canEditMonitoredPoints);

		// --
		// Updates linked projects toolbars.
		// --

		final boolean canRelateProject = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.RELATE_PROJECT);
		final boolean canCreateProject = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.CREATE_PROJECT, GlobalPermissionEnum.EDIT_PROJECT);
		view.updateLinkedProjectsToolbars(canRelateProject, canCreateProject);

		// --
		// Updates reminders / monitored points.
		// --
		loadReminders();
		loadMonitoredPoints();

		// --
		// Updates linked projects (funding / funded).
		// --

		view.getFundingProjectsColumnsProvider().setProject(getProject());
		loadLinkedProjects(null);

		view.getPhasesWidget().clear();
	}

	@Override
	protected void onViewRevealed() {
		
		// --
		// Updates project tabs.
		// --
		// BUGFIX #702: Loading phases after reveal to avoid layout errors.
		view.getPhasesWidget().refresh(getProject());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean hasValueChanged() {
		return view.getPhasesWidget().hasValueChanged();
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Loads monitored points.
	 */
	private void loadMonitoredPoints() {

		view.getMonitoredPointsGrid().getStore().removeAll();

		dispatch.execute(new GetMonitoredPoints(getProject().getId(), MonitoredPointDTO.Mode.WITH_HISTORY),
			new CommandResultHandler<ListResult<MonitoredPointDTO>>() {

				@Override
				protected void onCommandSuccess(final ListResult<MonitoredPointDTO> result) {

					if (result == null || result.isEmpty()) {
						return;
					}

					for (final MonitoredPointDTO point : result.getList()) {

						// Only show the undeleted monitored points.
						if (!ClientUtils.isTrue(point.getDeleted())) {
							point.setIsCompleted();
							view.getMonitoredPointsGrid().getStore().add(point);
						}
					}

					view.getMonitoredPointsGrid().getStore().sort(ReminderDTO.EXPECTED_DATE, SortDir.ASC);
				}

			}, new LoadingMask(view.getMonitoredPointsGrid()));
	}

	/**
	 * Loads reminders.
	 */
	private void loadReminders() {

		view.getRemindersGrid().getStore().removeAll();

		dispatch.execute(new GetReminders(getProject().getId(), ReminderDTO.Mode.WITH_HISTORY), new CommandResultHandler<ListResult<ReminderDTO>>() {

			@Override
			protected void onCommandSuccess(final ListResult<ReminderDTO> result) {

				if (result == null || result.isEmpty()) {
					return;
				}

				for (final ReminderDTO reminder : result.getList()) {

					// Only show the undeleted reminders.
					if (!ClientUtils.isTrue(reminder.getDeleted())) {
						reminder.setIsCompleted();
						view.getRemindersGrid().getStore().add(reminder);
					}
				}

				view.getRemindersGrid().getStore().sort(ReminderDTO.EXPECTED_DATE, SortDir.ASC);
			}

		}, new LoadingMask(view.getRemindersGrid()));
	}

	/**
	 * Loads the current project linked projects (i.e. funding / funded projects).
	 * 
	 * @param linkedProjectType
	 *          The linked projects type to load. Set to {@code null} to load all types.
	 */
	private void loadLinkedProjects(final LinkedProjectType linkedProjectType) {

		if (linkedProjectType == null || linkedProjectType == LinkedProjectType.FUNDING_PROJECT) {

			// --
			// Linked funding projects.
			// --

			view.getFundingProjectsGrid().getStore().removeAll();

			dispatch.execute(new GetLinkedProjects(getProject().getId(), LinkedProjectType.FUNDING_PROJECT, ProjectDTO.Mode._USE_PROJECT_MAPPER),
				new CommandResultHandler<ListResult<ProjectFundingDTO>>() {

					@Override
					protected void onCommandSuccess(final ListResult<ProjectFundingDTO> result) {
						view.getFundingProjectsGrid().getStore().add(result.getList());
					}

				}, new LoadingMask(view.getFundingProjectsGrid()));
		}

		if (linkedProjectType == null || linkedProjectType == LinkedProjectType.FUNDED_PROJECT) {

			// --
			// Linked funded projects.
			// --

			view.getFundedProjectsGrid().getStore().removeAll();

			dispatch.execute(new GetLinkedProjects(getProject().getId(), LinkedProjectType.FUNDED_PROJECT, ProjectDTO.Mode._USE_PROJECT_MAPPER),
				new CommandResultHandler<ListResult<ProjectFundingDTO>>() {

					@Override
					protected void onCommandSuccess(final ListResult<ProjectFundingDTO> result) {
						view.getFundedProjectsGrid().getStore().add(result.getList());
					}

				}, new LoadingMask(view.getFundedProjectsGrid()));
		}
	}

	/**
	 * Method executed on reminder update.
	 * 
	 * @param edited
	 *          The udpated reminder.
	 */
	private void onReminderUpdate(final ReminderDTO edited) {

		final Date editedDate = new Date();

		// The 'completed' field has been edited by the grid editor, but the actual property which is saved in
		// data-layer is 'completionDate'. We have to do the changes manually.
		if (edited.getIsCompleted()) {
			edited.setCompletionDate(editedDate);
		} else {
			edited.setCompletionDate(null);
		}

		// Updates points.
		dispatch.execute(new UpdateReminders(edited), new CommandResultHandler<ListResult<ReminderDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {

				if (Log.isErrorEnabled()) {
					Log.error("Error while merging the monitored points.", e);
				}

				view.getRemindersGrid().getStore().rejectChanges();

				N10N.warn(I18N.CONSTANTS.reminderUpdateError(), I18N.CONSTANTS.reminderUpdateErrorDetails());
			}

			@Override
			public void onCommandSuccess(final ListResult<ReminderDTO> result) {

				view.getRemindersGrid().getStore().commitChanges();

				for (final ReminderDTO point : result.getList()) {
					point.setIsCompleted();
					view.getRemindersGrid().getStore().update(point);
				}

				N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.reminderUpdateConfirm(), MessageType.INFO);
			}
		}, new LoadingMask(view.getRemindersGrid()));

	}

	/**
	 * Method executed on monitored point update.
	 * 
	 * @param edited
	 *          The udpated monitored point.
	 */
	private void onMonitoredPointUpdate(final MonitoredPointDTO edited) {

		final Date editedDate = new Date();

		// The 'completed' field has been edited by the grid editor, but the actual property which is saved in
		// data-layer is 'completionDate'. We have to do the changes manually.
		if (edited.getIsCompleted()) {
			edited.setCompletionDate(editedDate);
		} else {
			edited.setCompletionDate(null);
		}

		// Updates points.
		dispatch.execute(new UpdateMonitoredPoints(edited), new CommandResultHandler<ListResult<MonitoredPointDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {

				if (Log.isErrorEnabled()) {
					Log.error("Error while merging the monitored points.", e);
				}

				view.getMonitoredPointsGrid().getStore().rejectChanges();

				N10N.warn(I18N.CONSTANTS.monitoredPointUpdateError(), I18N.CONSTANTS.monitoredPointUpdateErrorDetails());
			}

			@Override
			public void onCommandSuccess(final ListResult<MonitoredPointDTO> result) {

				view.getMonitoredPointsGrid().getStore().commitChanges();

				for (final MonitoredPointDTO point : result.getList()) {
					point.setIsCompleted();
					view.getMonitoredPointsGrid().getStore().update(point);
				}

				N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.monitoredPointUpdateConfirm(), MessageType.INFO);
			}
		}, new LoadingMask(view.getMonitoredPointsGrid()));
	}

	/**
	 * Method executed on linked project action.
	 * 
	 * @param linkedProjectType
	 *          The deleted linked project type.
	 * @param linkedProject
	 *          The deleted linked project instance.
	 */
	private void onLinkedProjectDeleteAction(final LinkedProjectType linkedProjectType, final ProjectFundingDTO linkedProject) {

		final Grid<ProjectFundingDTO> linkedProjectsGrid;

		switch (linkedProjectType) {
			case FUNDING_PROJECT:
				linkedProjectsGrid = view.getFundingProjectsGrid();
				break;

			case FUNDED_PROJECT:
				linkedProjectsGrid = view.getFundedProjectsGrid();
				break;

			default:
				throw new IllegalArgumentException("Invalid linked project type.");
		}

		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(linkedProjectType.getPropertyIdKey(), linkedProject.getId());

		// RPC
		dispatch.execute(new UpdateEntity(ProjectDTO.ENTITY_NAME, getProject().getId(), properties), new CommandResultHandler<VoidResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while updating the linked projects.", caught);
				}
				N10N.warn(I18N.CONSTANTS.linkedProjectUpdateError(), I18N.CONSTANTS.linkedProjectUpdateErrorDetails());
			}

			@Override
			public void onCommandSuccess(final VoidResult result) {

				// After RPC, refresh the view.
				loadLinkedProjects(linkedProjectType);
				N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.linkedProjectUpdateConfirm(), MessageType.INFO);
			}

		}, new LoadingMask(linkedProjectsGrid));
	}
	
}
