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


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.EventBus.LeavingCallback;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.presenter.base.HasSubPresenter;
import org.sigmah.client.ui.view.base.HasSubView;
import org.sigmah.client.ui.view.project.ProjectView;
import org.sigmah.client.ui.widget.SubMenuItem;
import org.sigmah.client.ui.widget.SubMenuWidget;
import org.sigmah.client.ui.widget.SubMenuWidget.SubMenuListener;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.AmendmentActionCommand;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.ProjectBannerDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.referential.AmendmentAction;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.ExportUtils.ExportType;
import org.sigmah.shared.util.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Header;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.util.DateUtils;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.referential.CoreVersionAction;

/**
 * <p>
 * <b>UI parent</b> presenter which manages the {@link ProjectView}.
 * </p>
 * <p>
 * Does not respond to a page token. Manages sub-presenters.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectPresenter extends AbstractPresenter<ProjectPresenter.View> implements HasSubPresenter<ProjectPresenter.View> {
	
	private static final String ALERT_STYLE = "header-alert";

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectView.class)
	public static interface View extends HasSubView {

		/**
		 * Returns the sub-menu widget.
		 * 
		 * @return The sub-menu widget.
		 */
		SubMenuWidget getSubMenuWidget();

		Button getExportButton();

		Button getDeleteButton();

		void buildExportDialog(ExportActionHandler handler);
		
		void buildCreateCoreVersionDialog(SelectionListener<ButtonEvent> callback);

		// --
		// PROJECT BANNER.
		// --

		/**
		 * Sets the project view title.
		 * 
		 * @param projectName
		 *          The project name.
		 * @param projectFullName
		 *          The project full name.
		 */
		void setProjectTitle(String projectName, String projectFullName);
		
		/**
		 * Sets the project type icon.
		 * 
		 * @param projectType
		 *          The project type.
		 */
		void setProjectLogo(final ProjectModelType projectType);

		ContentPanel getProjectBannerPanel();

		void setProjectBanner(final Widget bannerWidget);

		HTMLTable buildBannerTable(final int rows, final int cols);

		// --
		// PROJECT CORE VERSION.
		// --

		ContentPanel getProjectCoreVersionPanel();

		Button getLockProjectCoreButton();
		
		Button getUnlockProjectCoreButton();

		Button getValidateVersionButton();
		
		Button getBackToWorkingVersionButton();
		
		ComboBox<CoreVersionAction> getCoreVersionActionComboBox();

		void setProjectCoreVersionState(AmendmentState state, boolean coreVersionWasModified);
		
		void setProjectCoreVersions(List<AmendmentDTO> coreVersions, boolean coreVersionWasModified, boolean canRenameVersion);
	}

	/**
	 * Export action handler.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static interface ExportActionHandler {

		/**
		 * Method executed on export dialog validation event.
		 * 
		 * @param indicatorField
		 *          The indicator field.
		 * @param logFrameField
		 *          The log frame field.
		 */
		void onExportProject(Field<Boolean> indicatorField, Field<Boolean> logFrameField);

	}

	/**
	 * Current project.
	 */
	private ProjectDTO project;

	/**
	 * The phase tab currently displayed on project dashboard view.<br>
	 * If {@code null}, no phase is currently displayed.
	 */
	private PhaseDTO displayedPhase;

	private AmendmentDTO currentCoreVersion;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ProjectPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		
		// --
		// SubMenu listener.
		// --
		view.getSubMenuWidget().addListener(new SubMenuListener() {

			@Override
			public void onSubMenuClick(final SubMenuItem menuItem) {

				final PageRequest currentPageRequest = injector.getPageManager().getCurrentPageRequest(false);
				Profiler.INSTANCE.startScenario(Scenario.AGENDA);	
				Profiler.INSTANCE.markCheckpoint(Scenario.AGENDA, "Before navigateRequest");
				eventBus.navigateRequest(menuItem.getRequest().addAllParameters(currentPageRequest.getParameters(false)));
			}
		});

		// --
		// Project core version.
		// --
		view.getCoreVersionActionComboBox().addSelectionChangedListener(new SelectionChangedListener<CoreVersionAction>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<CoreVersionAction> se) {
				final CoreVersionAction action = se.getSelectedItem();
				
				if(action == currentCoreVersion) {
					return;
				}
				
				switch(action.getType()) {
					case FUNCTION_COMPARE:
						eventBus.navigateRequest(Page.PROJECT_AMENDMENT_DIFF.request().addData(RequestParameter.DTO, project));
						view.getCoreVersionActionComboBox().setValue(currentCoreVersion);
						break;
					case FUNCTION_RENAME:
						eventBus.navigateRequest(Page.PROJECT_AMENDMENT_RENAME.request().addData(RequestParameter.DTO, project));
						view.getCoreVersionActionComboBox().setValue(currentCoreVersion);
						break;
					case CORE_VERSION:
						onDisplayCoreVersion((AmendmentDTO) action);
						break;
					default:
						view.getCoreVersionActionComboBox().setValue(currentCoreVersion);
						break;
				}
			}
		});
		
		// --
		// Lock project core version button handler.
		// --
		view.getLockProjectCoreButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onCoreVersionAction(project, AmendmentAction.LOCK, view.getLockProjectCoreButton());
			}
		});
		
		// --
		// Unlock project core version button handler.
		// --
		view.getUnlockProjectCoreButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onCoreVersionAction(project, AmendmentAction.UNLOCK, view.getUnlockProjectCoreButton());
			}
		});

		// --
		// Validate project core version button handler.
		// --
		view.getValidateVersionButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				view.buildCreateCoreVersionDialog(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						final String name = (String) ce.getSource();
						onCoreVersionAction(project, AmendmentAction.VALIDATE, view.getValidateVersionButton(), name);
					}
				});
			}
		});

		// --
		// Back to working version button handler.
		// --
		view.getBackToWorkingVersionButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				final PageRequest currentPageRequest = injector.getPageManager().getCurrentPageRequest();
				eventBus.navigateRequest(currentPageRequest.removeParameter(RequestParameter.VERSION), view.getBackToWorkingVersionButton());
				// BUGFIX #726: Setting the current core version to null to really exit the core version view mode.
				currentCoreVersion = null;
			}
		});

		// --
		// Project export button handler.
		// --
		view.getExportButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onExportProject(project);
			}
		});

		// --
		// Project delete button handler.
		// --
		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onDeleteProject(project);
			}
		});

		// --
		// Project banner update event handler.
		// --
		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {
				if (event.concern(UpdateEvent.PROJECT_BANNER_UPDATE)) {
					refreshBanner(project);
					
				} else if (event.concern(UpdateEvent.AMENDMENT_RENAME)) {
					loadAmendments(project, currentCoreVersion != null ? currentCoreVersion.getId() : null);
					
				} else if(event.concern(UpdateEvent.CORE_VERSION_UPDATED)) {
					// This is really harsh but it was the simplest to have to latest project revision.
					// If too much, it is possible to set revision to revision + 1 and call loadAmendments instead.
					eventBus.navigateRequest(injector.getPageManager().getCurrentPageRequest(), new LoadingMask(view.getProjectCoreVersionPanel()));
				}
			}
		}));
		
		// --
		// Sub menu visibility.
		// --
		view.getSubMenuWidget().setRequiredPermissions(Page.PROJECT_LOGFRAME, GlobalPermissionEnum.VIEW_LOGFRAME);
		view.getSubMenuWidget().setRequiredPermissions(Page.PROJECT_INDICATORS_MANAGEMENT, GlobalPermissionEnum.VIEW_INDICATOR);
		view.getSubMenuWidget().setRequiredPermissions(Page.PROJECT_INDICATORS_MAP, GlobalPermissionEnum.VIEW_MAPTAB);
		view.getSubMenuWidget().setRequiredPermissions(Page.PROJECT_INDICATORS_ENTRIES, GlobalPermissionEnum.VIEW_INDICATOR);
		view.getSubMenuWidget().setRequiredPermissions(Page.PROJECT_CALENDAR, GlobalPermissionEnum.VIEW_PROJECT_AGENDA);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSubPresenterRequest(final PageRequest subPageRequest) {

		// Updates sub-menu widget.
		view.getSubMenuWidget().initializeMenu(subPageRequest.getPage(), auth());
		Profiler.INSTANCE.markCheckpoint(Scenario.OPEN_PROJECT, "End initializeMenu");
		// Updates delete button enabled state.
		final boolean canDeleteProject = canDeleteProject();
		view.getDeleteButton().setEnabled(canDeleteProject);
		view.getDeleteButton().setVisible(canDeleteProject);

		currentCoreVersion = null;
		Integer coreVersionId = subPageRequest.getParameterInteger(RequestParameter.VERSION);

		// Updates parent view elements.
		loadAmendments(project, coreVersionId);
		Profiler.INSTANCE.markCheckpoint(Scenario.OPEN_PROJECT, "End loadAmendments");
		refreshBanner(project);
		Profiler.INSTANCE.markCheckpoint(Scenario.OPEN_PROJECT, "End refreshBanner");
		//Profiler.INSTANCE.endScenario(Scenario.OPEN_PROJECT);
	}

	/**
	 * Return the current loaded project.
	 * 
	 * @return The current loaded project instance.
	 */
	public ProjectDTO getCurrentProject() {
		return project;
	}

	/**
	 * Sets the current project.
	 * 
	 * @param project
	 *          The project instance.
	 */
	public void setCurrentProject(final ProjectDTO project) {
		this.project = project;
	}

	/**
	 * Returns the current displayed phase.
	 * 
	 * @return The current displayed phase, or {@code null} if no phase is displayed.
	 */
	public PhaseDTO getCurrentDisplayedPhase() {
		return displayedPhase;
	}

	/**
	 * Sets the current displayed phase.
	 * 
	 * @param displayedPhase
	 *          The displayed phase.
	 */
	public void setCurrentDisplayedPhase(final PhaseDTO displayedPhase) {
		this.displayedPhase = displayedPhase;
	}
	
	/**
	 * Indicates if the current loaded project is locked.
	 * 
	 * @return <code>true</code> if the current project is loaded, <code>false</code> otherwise.
	 */
	public boolean projectIsLocked() {
		return project.getAmendmentState() == AmendmentState.LOCKED;
	}
	
	/**
	 * Indicates if the user has the permission to unlock projects.
	 * 
	 * @return <code>true</code> if the current user can unlock projects, <code>false</code> otherwise.
	 */
	public boolean canUnlockProject() {
		return ProfileUtils.isGranted(auth(), GlobalPermissionEnum.LOCK_PROJECT);
	}
	
	/**
	 * Asks the user if he wants to unlock the project to edit the clicked 
	 * flexible element.
	 * 
	 * @param element Element that is part of the project core version.
	 * @param component Component of the element.
	 * @param loadable Loadable to mask while unlocking the project.
	 */
	public void addUnlockProjectPopup(final FlexibleElementDTO element, Component component, final Loadable loadable) {
		component.addListener(Events.OnFocus, new Listener<DomEvent>() {

			@Override
			public void handleEvent(DomEvent be) {
				N10N.confirmation(I18N.MESSAGES.projectCoreUnlockInvite(element.getFormattedLabel()), new ConfirmCallback() {

					@Override
					public void onAction() {
						dispatch.execute(new AmendmentActionCommand(project.getId(), AmendmentAction.UNLOCK), new CommandResultHandler<ProjectDTO>() {

							@Override
							protected void onCommandSuccess(ProjectDTO result) {
								project.setAmendmentState(result.getAmendmentState());
								eventBus.navigateRequest(injector.getPageManager().getCurrentPageRequest(), loadable);
							}
						}, loadable);
					}
				});
			}
		});
	}

	// -------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------

	/**
	 * Loads amendments for the given {@code project}.
	 * 
	 * @param project
	 *          The project.
	 */
	private void loadAmendments(ProjectDTO project, Integer coreVersionId) {

		Log.debug("Loading amendments for project '" + project.getName() + "'...");
		
		if(coreVersionId != null) {
			for(AmendmentDTO coreVersion : project.getAmendments()) {
				if(coreVersionId.equals(coreVersion.getId())) {
					currentCoreVersion = coreVersion;
				}
			}
		}
		
		project.setCurrentAmendment(currentCoreVersion);
		view.getCoreVersionActionComboBox().setValue(currentCoreVersion);
		
		final boolean coreVersionWasModified = project.getAmendmentVersion() == 1 || project.getAmendmentRevision() > 1;
		
		if(currentCoreVersion != null) {
			view.setProjectCoreVersionState(currentCoreVersion.getState(), coreVersionWasModified);
		} else if(project.getCurrentPhase().isEnded()) {
			view.setProjectCoreVersionState(AmendmentState.PROJECT_ENDED, coreVersionWasModified);
		} else {
			view.setProjectCoreVersionState(project.getAmendmentState(), coreVersionWasModified);
		}
		
		final boolean canValidateCoreVersion = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VALID_AMENDEMENT);
		final boolean canLockOrUnlock = canUnlockProject();
		
		view.setProjectCoreVersions(project.getAmendments(), coreVersionWasModified, canValidateCoreVersion);
		
		if(coreVersionWasModified) {
			view.getProjectCoreVersionPanel().setHeadingHtml("<span title=\"" + I18N.CONSTANTS.projectCoreModified() + "\">" + I18N.CONSTANTS.projectCoreBoxTitle() + ' ' + IconImageBundle.ICONS.warningSmall().getHTML() + "</span>");
		} else {
			view.getProjectCoreVersionPanel().setHeadingHtml(I18N.CONSTANTS.projectCoreBoxTitle());
		}

		// BUGFIX #738: Disabling buttons if the user has not the required rights.
		view.getLockProjectCoreButton().setEnabled(view.getLockProjectCoreButton().isEnabled() && canLockOrUnlock);
		view.getUnlockProjectCoreButton().setEnabled(view.getUnlockProjectCoreButton().isEnabled() && canLockOrUnlock);
		view.getValidateVersionButton().setEnabled(view.getValidateVersionButton().isEnabled() && canValidateCoreVersion);
	}

	/**
	 * Handles the given amendment {@code action} click event.
	 * 
	 * @param project
	 *          The current project.
	 * @param action
	 *          The selected action.
	 */
	private void onCoreVersionAction(final ProjectDTO project, final AmendmentAction action, final Button source) {
		onCoreVersionAction(project, action, source, null);
	}
	
	/**
	 * Handles the given amendment {@code action} click event.
	 * 
	 * @param project
	 *          The current project.
	 * @param action
	 *          The selected action.
	 * @param name
	 *          Name of the new core version.
	 */
	private void onCoreVersionAction(final ProjectDTO project, final AmendmentAction action, final Button source, final String name) {

		// Executes form changes detection control.
		injector.getPageManager().getCurrentPresenter().beforeLeaving(new LeavingCallback() {

			@Override
			public void leavingOk() {

				dispatch.execute(new AmendmentActionCommand(project.getId(), action, name), new CommandResultHandler<ProjectDTO>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						// Failures may happen if an other user changes the amendment state.
						// TODO (v1.3) we should maybe refresh the project or tell the user to refresh the page.
						N10N.warn(AmendmentAction.getName(action), I18N.CONSTANTS.amendmentActionError());
					}

					@Override
					public void onCommandSuccess(final ProjectDTO result) {

						Log.debug("Amendment action has been successfully processed.");
						ProjectPresenter.this.project = result;

						// Reloading the page.
						eventBus.navigateRequest(injector.getPageManager().getCurrentPageRequest(), new LoadingMask(view.getProjectCoreVersionPanel()));

					}
				}, source);

			}

			@Override
			public void leavingKo() {
				if (Log.isDebugEnabled()) {
					Log.debug("User does not want to leave unsaved page. Nothing to do.");
				}
			}
		});
	}

	/**
	 * Handles the amendment load action.<br>
	 * Retrieves the selected amendment value and loads it.
	 */
	private void onDisplayCoreVersion(final AmendmentDTO coreVersion) {
		
		currentCoreVersion = coreVersion;

		injector.getPageManager().getCurrentPresenter().beforeLeaving(new LeavingCallback() {

			@Override
			public void leavingOk() {

				Log.debug("Loading amendment with id #" + currentCoreVersion.getId() + "...");

				if (currentCoreVersion.getId() != null) {
					// Reloading the page with the amendment id parameter.
					final PageRequest currentPageRequest = injector.getPageManager().getCurrentPageRequest();
					currentPageRequest.addParameter(RequestParameter.VERSION, currentCoreVersion.getId());
					eventBus.navigateRequest(currentPageRequest, new LoadingMask(view.getProjectCoreVersionPanel()));
				}
			}

			@Override
			public void leavingKo() {
				Log.debug("User does not want to leave unsaved page. Nothing to do.");
			}
		});
	}
	
	/**
	 * <p>
	 * Refreshes the project banner for the current project.
	 * </p>
	 * <p>
	 * Provided {@code project} must possess following attributes:
	 * <ul>
	 * <li>Base attributes (id, name, etc.)</li>
	 * <li>{@link ProjectDTO#PROJECT_MODEL}</li>
	 * <li>{@link ProjectDTO#CURRENT_AMENDMENT}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param project
	 *          The current loaded project.
	 */
	private void refreshBanner(final ProjectDTO project) {

		view.setProjectTitle(project.getName(), project.getFullName());
		view.setProjectLogo(project.getProjectModel().getVisibility(auth().getOrganizationId()));
		
		// Maintenance alert
		final Header header = view.getProjectBannerPanel().getHeader();
		
		if (project.getProjectModel().isUnderMaintenance()) {
			header.addStyleName(ALERT_STYLE);
			header.setHtml(header.getHtml() + " - " + I18N.MESSAGES.projectMaintenanceMessage());
			
		} else if (project.getProjectModel().getDateMaintenance() != null) {
			header.addStyleName(ALERT_STYLE);
			
			String maintenanceDate = DateUtils.DATE_TIME_SHORT.format(project.getProjectModel().getDateMaintenance());
			header.setHtml(header.getHtml() + " - " + I18N.MESSAGES.projectMaintenanceScheduledMessage(maintenanceDate));
			
		} else {
			header.removeStyleName(ALERT_STYLE);
		}

		// Banner data.
		final ProjectBannerDTO banner = project.getProjectModel().getProjectBanner();
		final LayoutDTO layout = banner != null ? banner.getLayout() : null;

		final Widget bannerWidget;

		if (layout != null && layout.getGroups() != null && !layout.getGroups().isEmpty()) {

			// --
			// Layout banner.
			// --

			// For visibility constraints, the banner accept a maximum of 2 rows and 4 columns.
			final int rows = layout.getRowsCount() > 2 ? 2 : layout.getRowsCount();
			final int cols = layout.getColumnsCount() > 4 ? 4 : layout.getColumnsCount();

			final HTMLTable gridLayout = view.buildBannerTable(rows, cols);
			bannerWidget = gridLayout;

			for (final LayoutGroupDTO groupLayout : layout.getGroups()) {

				if (groupLayout.getRow() + 1 > rows || groupLayout.getColumn() + 1 > cols) {
					// Checks group bounds.
					continue;
				}

				final ContentPanel groupPanel = new ContentPanel();
				groupPanel.setLayout(new FormLayout());
				groupPanel.setTopComponent(null);
				groupPanel.setHeaderVisible(false);

				gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), groupPanel);

				if (groupLayout.getConstraints() == null) {
					continue;
				}

				for (final LayoutConstraintDTO constraint : groupLayout.getConstraints()) {

					final FlexibleElementDTO element = constraint.getFlexibleElementDTO();

					// Only default elements are allowed.
					if (!(element instanceof DefaultFlexibleElementDTO)) {
						continue;
					}

					// Builds the graphic component
					final DefaultFlexibleElementDTO defaultElement = (DefaultFlexibleElementDTO) element;
					defaultElement.setService(dispatch);
					defaultElement.setAuthenticationProvider(injector.getAuthenticationProvider());
					defaultElement.setCache(injector.getClientCache());
					defaultElement.setCurrentContainerDTO(project);

					final Integer amendmentId;
					if (project.getCurrentAmendment() != null) {
						amendmentId = project.getCurrentAmendment().getId();
					} else {
						amendmentId = null;
					}

					// Remote call to ask for this element value.
					dispatch.execute(new GetValue(project.getId(), element.getId(), element.getEntityName(), amendmentId), new CommandResultHandler<ValueResult>() {

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

							final Component component;
							if (defaultElement instanceof BudgetElementDTO) {
								component = defaultElement.getElementComponentInBanner(valueResult);
							} else {
								component = defaultElement.getElementComponentInBanner(null);
							}

							if (component == null) {
								return;
							}

							if (component instanceof LabelField) {

								final LabelField lableFieldComponent = (LabelField) component;

								// Get the text of the field
								final String textValue = (String) lableFieldComponent.getValue();

								// Set the tool tip
								final ToolTipConfig config = new ToolTipConfig();
								config.setMaxWidth(500);
								config.setText(textValue);
								lableFieldComponent.setToolTip(config);

								// Clip the text if it is longer than 30
								if (ClientUtils.isNotBlank(textValue)) {
									lableFieldComponent.setValue(ClientUtils.abbreviate(textValue, 30));
								}

								groupPanel.add(lableFieldComponent);

							} else {
								groupPanel.add(component);
							}

							groupPanel.layout();
						}
					});

					// Only one element per cell.
					break;
				}
			}

		} else {

			// --
			// Default banner.
			// --

			view.getProjectBannerPanel().setLayout(new FormLayout());

			final LabelField codeField = new LabelField();
			codeField.setReadOnly(true);
			codeField.setFieldLabel(I18N.CONSTANTS.projectName());
			codeField.setLabelSeparator(I18N.CONSTANTS.form_label_separator());
			codeField.setValue(project.getName());

			bannerWidget = codeField;
		}

		view.setProjectBanner(bannerWidget);
		view.getProjectBannerPanel().layout();
	}

	/**
	 * Method executed on export project action.
	 * 
	 * @param project
	 *          The project to export.
	 */
	private void onExportProject(final ProjectDTO project) {

		view.buildExportDialog(new ExportActionHandler() {

			@Override
			public void onExportProject(final Field<Boolean> indicatorField, final Field<Boolean> logFrameField) {

				final ServletUrlBuilder urlBuilder =
						new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_PROJECT);

				final ExportType type;

				if (indicatorField.getValue() && logFrameField.getValue()) {
					type = ExportType.PROJECT_SYNTHESIS_LOGFRAME_INDICATORS;

				} else if (indicatorField.getValue() && !logFrameField.getValue()) {
					type = ExportType.PROJECT_SYNTHESIS_INDICATORS;

				} else if (!indicatorField.getValue() && logFrameField.getValue()) {
					type = ExportType.PROJECT_SYNTHESIS_LOGFRAME;

				} else {
					type = ExportType.PROJECT_SYNTHESIS;
				}

				urlBuilder.addParameter(RequestParameter.ID, project.getId());
				urlBuilder.addParameter(RequestParameter.TYPE, type);

				final FormElement form = FormElement.as(DOM.createForm());
				form.setAction(urlBuilder.toString());
				form.setTarget("_downloadFrame");
				form.setMethod(Method.POST.name());

				RootPanel.getBodyElement().appendChild(form);

				form.submit();
				form.removeFromParent();
			}
		});
	}

	/**
	 * Method executed on delete project action.
	 * 
	 * @param project
	 *          The project to delete.
	 */
	private void onDeleteProject(final ProjectDTO project) {

		if (project == null || !canDeleteProject()) {
			return;
		}

		N10N.confirmation(I18N.CONSTANTS.confirmDeleteProjectMessageBoxTitle(), I18N.CONSTANTS.confirmDeleteProjectMessageBoxContent(), new ConfirmCallback() {

			/**
			 * OK action.
			 */
			@Override
			public void onAction() {

				final Map<String, Object> changes = new HashMap<String, Object>();
				changes.put("dateDeleted", new Date());

				dispatch.execute(new UpdateEntity(project, changes), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandSuccess(final VoidResult result) {

						final PageRequest currentRequest = injector.getPageManager().getCurrentPageRequest(false);
						eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROJECT_DELETE, currentRequest));

						N10N.infoNotif(I18N.CONSTANTS.deleteProjectNotificationTitle(), I18N.CONSTANTS.deleteProjectNotificationContent());
					}
				}, view.getDeleteButton());

			}
		});
	}

	/**
	 * Returns if the current authenticated user is authorized to delete a project.
	 * 
	 * @return {@code true} if the current authenticated user is authorized to delete a project.
	 */
	private boolean canDeleteProject() {
		return ProfileUtils.isGranted(auth(), GlobalPermissionEnum.DELETE_PROJECT);
	}

	public String buildName(AmendmentDTO amendment) {

		String version = "";

		try {
			version = Integer.toString(amendment.getVersion());

		} catch (NullPointerException e) {
			// Digest.
		}

		return version + ". " + amendment.getName() + "    " + DateTimeFormat.getShortDateFormat().format(amendment.getDate());
	}
}
