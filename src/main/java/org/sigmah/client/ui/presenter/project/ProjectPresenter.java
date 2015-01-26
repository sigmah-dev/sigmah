package org.sigmah.client.ui.presenter.project;

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
import org.sigmah.client.ui.res.icon.IconImageBundle;
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
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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

		Button getDeletetButton();

		void buildExportDialog(ExportActionHandler handler);

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
		// AMENDMENTS.
		// --

		Component getAmendmentBox();

		Button getLockerAmendmentButton();

		Button getValidateVersionProjectCoreButton();

		SplitButton getAmendmentsButton();

		ListStore<AmendmentDTO> getListAmendmentStore();

		ListView<AmendmentDTO> getListAmendments();

		Menu getAmendmentsMenuActions();

		/**
		 * Adds a new amendment action.
		 * 
		 * @param clearFirst
		 *          {@code true} to clear previous actions, {@code false} to keep them.
		 * @param action
		 *          The amendment action.
		 * @param handler
		 *          The action handler.
		 */
		void addAmendmentAction(boolean clearFirst, AmendmentAction action, ClickHandler handler);

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

	private AmendmentDTO currentamendmentDTO;

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
				eventBus.navigateRequest(menuItem.getRequest().addAllParameters(currentPageRequest.getParameters(true)));
			}
		});

		// --
		// Amendement
		// --

		view.getAmendmentsMenuActions().getItem(0).addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				eventBus.navigateRequest(Page.PROJECT_AMENDMENT_DIFF.request().addData(RequestParameter.DTO, project));

			};
		});

		view.getAmendmentsMenuActions().getItem(1).addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				eventBus.navigateRequest(Page.PROJECT_AMENDMENT_RENAME.request().addData(RequestParameter.DTO, project));

			};
		});

		// --
		// Amendment load button event handler.
		// --

		view.getLockerAmendmentButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {

				// Displays the available actions.
				final AmendmentAction[] actions;

				if (project.getAmendmentState() != null) {

					actions = project.getAmendmentState().getActions();

				} else {

					actions = new AmendmentAction[0];
				}

				for (int i = 0; i < actions.length; i++) {

					final AmendmentAction action = actions[i];

					if (Log.isDebugEnabled()) {
						Log.debug("Adding the '" + action + "' amendment action.");
					}

					if (action == AmendmentAction.LOCK || action == AmendmentAction.UNLOCK) {

						onAmendmentActionClickEvent(project, action);
					}

				}
			}
		});

		// --
		// Amendment Validate Version
		// --

		view.getValidateVersionProjectCoreButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				// Displays the available actions.
				final AmendmentAction[] actions;

				if (project.getAmendmentState() != null) {

					actions = project.getAmendmentState().getActions();

				} else {

					actions = new AmendmentAction[0];
				}

				for (int i = 0; i < actions.length; i++) {

					final AmendmentAction action = actions[i];

					if (Log.isDebugEnabled()) {
						Log.debug("Adding the '" + action + "' amendment action.");
					}

					if (action == AmendmentAction.VALIDATE) {

						if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VALID_AMENDEMENT)) {

							onAmendmentActionClickEvent(project, action);

						}
					}

				}
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

		view.getDeletetButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

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
				}
				if (event.concern(UpdateEvent.AMENDMENT_RENAME)) {
					loadAmendments(project);
				}
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSubPresenterRequest(final PageRequest subPageRequest) {

		// Updates sub-menu widget.
		view.getSubMenuWidget().initializeMenu(subPageRequest.getPage());

		// Updates delete button enabled state.
		final boolean canDeleteProject = canDeleteProject();
		view.getDeletetButton().setEnabled(canDeleteProject);
		view.getDeletetButton().setVisible(canDeleteProject);

		currentamendmentDTO = subPageRequest.getData(RequestParameter.DTO);

		// Updates parent view elements.
		loadAmendments(project);
		refreshBanner(project);
	}

	/**
	 * Returns the current loaded project.
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
	private final void loadAmendments(final ProjectDTO project) {

		this.project = project;

		if (Log.isDebugEnabled()) {
			Log.debug("Loading amendments for project '" + project.getName() + "'...");
		}

		// Prepares the amendment store.
		final ListStore<AmendmentDTO> listAmendmentsStore = view.getListAmendmentStore();
		listAmendmentsStore.removeAll();

		for (final AmendmentDTO amendmentDTO : project.getAmendments()) {
			amendmentDTO.prepareName(buildName(amendmentDTO));
			listAmendmentsStore.add(amendmentDTO);
		}

		// Adds the current amendment.
		final AmendmentDTO currentAmendment = new AmendmentDTO(project);
		Date date = new Date();
		currentAmendment.setDate(date);
		currentAmendment.prepareName(buildName(currentAmendment));
		listAmendmentsStore.add(currentAmendment);

		view.getListAmendments().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<AmendmentDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<AmendmentDTO> se) {

				if (se.getSelectedItem() != null) {
					view.getAmendmentsButton().setText(se.getSelectedItem().getName());
					onAmendmentLoadAction();
				} else {
					view.getAmendmentsButton().setText(I18N.CONSTANTS.projectCoreNoValidated());
				}

			}
		});

		listAmendmentsStore.commitChanges();

		if (currentamendmentDTO != null) {
			view.getAmendmentsButton().setText(currentamendmentDTO.getName());
		}

		switch (project.getAmendmentState()) {

			case LOCKED:

				view.getLockerAmendmentButton().setText(I18N.CONSTANTS.projectCoreUnlockButton());
				view.getLockerAmendmentButton().setIcon(IconImageBundle.ICONS.unlock());
				view.getValidateVersionProjectCoreButton().setEnabled(true);

				break;

			case DRAFT:

				view.getValidateVersionProjectCoreButton().setEnabled(false);
				view.getLockerAmendmentButton().setText(I18N.CONSTANTS.projectCorelockButton());
				view.getLockerAmendmentButton().setIcon(IconImageBundle.ICONS.lock());

				break;

			default:
				break;
		}

	}

	/**
	 * Handles the given amendment {@code action} click event.
	 * 
	 * @param project
	 *          The current project.
	 * @param action
	 *          The selected action.
	 */
	private void onAmendmentActionClickEvent(final ProjectDTO project, final AmendmentAction action) {

		// Executes form changes detection control.
		injector.getPageManager().getCurrentPresenter().beforeLeaving(new LeavingCallback() {

			@Override
			public void leavingOk() {

				dispatch.execute(new AmendmentActionCommand(project.getId(), action), new CommandResultHandler<ProjectDTO>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						// Failures may happen if an other user changes the amendment state.
						// TODO (v1.3) we should maybe refresh the project or tell the user to refresh the page.
						N10N.warn(AmendmentAction.getName(action), I18N.CONSTANTS.amendmentActionError());
					}

					@Override
					public void onCommandSuccess(final ProjectDTO result) {

						if (Log.isDebugEnabled()) {
							Log.debug("Amendment action has been successfully processed.");
						}

						// Reloading the page.
						loadAmendments(result);
						eventBus.navigateRequest(injector.getPageManager().getCurrentPageRequest(), view.getLockerAmendmentButton());

					}
				}, new LoadingMask(view.getAmendmentBox()));

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
	private void onAmendmentLoadAction() {

		injector.getPageManager().getCurrentPresenter().beforeLeaving(new LeavingCallback() {

			@Override
			public void leavingOk() {

				final AmendmentDTO amendmentDTO = view.getListAmendments().getSelectionModel().getSelectedItem();

				if (Log.isDebugEnabled()) {
					Log.debug("Loading amendment with id #" + amendmentDTO.getId() + "...");
				}

				if (amendmentDTO.getId() != null) {
					// Reloading the page with the amendment id parameter.
					final PageRequest currentPageRequest = injector.getPageManager().getCurrentPageRequest();
					currentPageRequest.addParameter(RequestParameter.VERSION, amendmentDTO.getId());
					currentPageRequest.addData(RequestParameter.DTO, amendmentDTO);
					eventBus.navigateRequest(currentPageRequest, view.getLockerAmendmentButton());

				}
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

		// Banner data.
		final ProjectBannerDTO banner = project.getProjectModel().getProjectBanner();
		final LayoutDTO layout = banner.getLayout();

		final Widget bannerWidget;

		if (banner != null && layout != null && layout.getGroups() != null && !layout.getGroups().isEmpty()) {

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
				}, view.getDeletetButton());

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
