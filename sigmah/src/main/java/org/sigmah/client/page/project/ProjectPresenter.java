/*
 * All Sigmah code is released under the GNU General Public License v3 See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.client.page.project;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.AppEvents;
import org.sigmah.client.EventBus;
import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.event.NavigationEvent;
import org.sigmah.client.event.NavigationEvent.NavigationError;
import org.sigmah.client.event.ProjectEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.Frame;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.TabPage;
import org.sigmah.client.page.project.calendar.ProjectCalendarPresenter;
import org.sigmah.client.page.project.dashboard.ProjectDashboardPresenter;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider;
import org.sigmah.client.page.project.design.ProjectIndicatorsContainer;
import org.sigmah.client.page.project.details.ProjectDetailsPresenter;
import org.sigmah.client.page.project.logframe.ProjectLogFramePresenter;
import org.sigmah.client.page.project.pivot.ProjectPivotContainer;
import org.sigmah.client.page.project.reports.ProjectReportsPresenter;
import org.sigmah.client.ui.ExportSpreadsheetFormButton;
import org.sigmah.client.ui.ToggleAnchor;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.AmendmentAction;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.Amendment;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.ExportUtils;
import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.ProjectBannerDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Project presenter which manages the {@link ProjectView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectPresenter implements Frame, TabPage {

	public static final PageId PAGE_ID = new PageId("project");

	public static final int REPORT_TAB_INDEX = 6;

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectView.class)
	public interface View {

		public ContentPanel getPanelProjectBanner();

		public ContentPanel getTabPanel();

		public ContentPanel getAmendmentBox();

		public void setMainPanel(Widget widget);

		public void insufficient();

		public void sufficient();
	}

	private final View view;
	private final Dispatcher dispatcher;
	private final Authentication authentication;
	private final UserLocalCache cache;
	private final EventBus eventBus;
	private Page activePage;

	private ProjectState currentState;
	private ToggleAnchor currentTab;
	/**
	 * The current displayed project.
	 */
	private ProjectDTO currentProjectDTO;
	/**
	 * The current displayed phase.
	 */
	private PhaseDTO currentDisplayedPhaseDTO;

	private final static Map<Amendment.Action, String> amendmentActionDisplayNames;

	static {
		final EnumMap<Amendment.Action, String> map = new EnumMap<Amendment.Action, String>(Amendment.Action.class);
		map.put(Amendment.Action.CREATE, I18N.CONSTANTS.amendmentCreate());
		map.put(Amendment.Action.LOCK, I18N.CONSTANTS.amendmentLock());
		map.put(Amendment.Action.UNLOCK, I18N.CONSTANTS.amendmentUnlock());
		map.put(Amendment.Action.REJECT, I18N.CONSTANTS.amendmentReject());
		map.put(Amendment.Action.VALIDATE, I18N.CONSTANTS.amendmentValidate());

		amendmentActionDisplayNames = map;
	}

	private final static String[] MAIN_TABS = { I18N.CONSTANTS.projectTabDashboard(), I18N.CONSTANTS.projectDetails(),
					I18N.CONSTANTS.projectTabLogFrame(), I18N.CONSTANTS.projectTabIndicators(),
					I18N.CONSTANTS.projectTabDataEntry(), I18N.CONSTANTS.projectTabCalendar(),
					I18N.CONSTANTS.projectTabReports()
	/* , I18N.CONSTANTS.projectTabSecurityIncident() */// TO DO
	};

	// TODO: the sub presenters all probably need to be notified of when the
	// project is to be loaded
	// into view. Maybe a SubProjectPresenter interface? Then projectIndicators
	// field can be removed below
	private final SubPresenter[] presenters;

	@Inject
	public ProjectPresenter(final Dispatcher dispatcher, View view, Authentication authentication,
					final EventBus eventBus, final UserLocalCache cache, ProjectIndicatorsContainer projectIndicators,
					ProjectPivotContainer pivot) {
		this.dispatcher = dispatcher;
		this.view = view;
		this.authentication = authentication;
		this.cache = cache;
		this.eventBus = eventBus;

		// For development.
		// final DummyPresenter dummyPresenter = new DummyPresenter();

		this.presenters = new SubPresenter[] {
						new ProjectDashboardPresenter(dispatcher, eventBus, authentication, this, cache), // Dashboard
						new ProjectDetailsPresenter(eventBus, dispatcher, authentication, this, cache), // Details,
						new ProjectLogFramePresenter(eventBus, dispatcher, authentication, this), // Logic
						projectIndicators, pivot, new ProjectCalendarPresenter(dispatcher, authentication, this), // Calendar
						new ProjectReportsPresenter(authentication, dispatcher, eventBus, this) // Reports
		/* ,dummyPresenter */// Security incidents TO DO
		};

		for (int i = 0; i < MAIN_TABS.length; i++) {
			final int index = i;

			String tabTitle = MAIN_TABS[i];

			final HBoxLayoutData layoutData = new HBoxLayoutData();
			layoutData.setMargins(new Margins(2, 10, 0, 0));

			final ToggleAnchor anchor = new ToggleAnchor(tabTitle);
			anchor.setAnchorMode(true);

			anchor.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					eventBus.fireEvent(new NavigationEvent(NavigationHandler.NavigationRequested, currentState
									.deriveTo(index), null));
				}
			});

			this.view.getTabPanel().add(anchor, layoutData);
		}

		// Export excel button
		final Button exportAnchor = new Button(I18N.CONSTANTS.export(), IconImageBundle.ICONS.excel());
		exportAnchor.setHeight("22px");

		final HBoxLayoutData layoutData = new HBoxLayoutData();
		layoutData.setMargins(new Margins(0, 3, 0, 0));

		final ExportSpreadsheetFormButton exportForm = new ExportSpreadsheetFormButton();
		exportAnchor.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent arg0) {
				final Window w = new Window();
				w.setPlain(true);
				w.setModal(true);
				w.setBlinkModal(true);
				w.setLayout(new FitLayout());
				w.setSize(350, 180);
				w.setHeading(I18N.CONSTANTS.exportData());

				final FormPanel panel = new FormPanel();

				final CheckBoxGroup options = new CheckBoxGroup();
				options.setOrientation(Orientation.VERTICAL);
				options.setFieldLabel(I18N.CONSTANTS.exportOptions());
				final CheckBox synthesisBox = createCheckBox(I18N.CONSTANTS.projectSynthesis());
				synthesisBox.setValue(true);
				synthesisBox.setEnabled(false);
				final CheckBox indicatorBox = createCheckBox(I18N.CONSTANTS.flexibleElementIndicatorsList());
				final CheckBox logFrameBox = createCheckBox(I18N.CONSTANTS.logFrame());
				options.add(synthesisBox);
				options.add(logFrameBox);
				options.add(indicatorBox);

				panel.add(options);

				final Button export = new Button(I18N.CONSTANTS.export());
				panel.getButtonBar().add(export);
				export.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						ExportUtils.ExportType type = ExportUtils.ExportType.PROJECT_SYNTHESIS;
						if (indicatorBox.getValue() && logFrameBox.getValue()) {
							type = ExportUtils.ExportType.PROJECT_SYNTHESIS_LOGFRAME_INDICATORS;
						} else if (indicatorBox.getValue() && !logFrameBox.getValue()) {
							type = ExportUtils.ExportType.PROJECT_SYNTHESIS_INDICATORS;
						} else if (!indicatorBox.getValue() && logFrameBox.getValue()) {
							type = ExportUtils.ExportType.PROJECT_SYNTHESIS_LOGFRAME;
						}
						exportForm.getFieldMap().put(ExportUtils.PARAM_EXPORT_TYPE, type.name());

						exportForm.triggerExport();
						w.hide();
					}
				});

				w.add(panel);
				w.show();

				exportForm.getFieldMap().put(ExportUtils.PARAM_EXPORT_PROJECT_ID,
								String.valueOf(currentProjectDTO.getId()));
			}
		});

		// Creates a blank space between the tabs'links and the buttons
		final HBoxLayoutData flex = new HBoxLayoutData();
		flex.setFlex(1);
		this.view.getTabPanel().add(new Anchor(), flex);

		this.view.getTabPanel().add(exportAnchor, layoutData);
		this.view.getTabPanel().add(exportForm.getExportForm());

		if (ProfileUtils.isGranted(authentication, GlobalPermissionEnum.DELETE_PROJECT)) {
			final HBoxLayoutData deleteLayoutData = new HBoxLayoutData();
			deleteLayoutData.setMargins(new Margins(0, 0, 0, 0));

			final Button deleteAnchor = new Button(I18N.CONSTANTS.deleteProjectAnchor(), IconImageBundle.ICONS.remove());
			deleteAnchor.setHeight("22px");
			deleteAnchor.addSelectionListener(new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent event) {
					MessageBox.confirm(I18N.CONSTANTS.confirmDeleteProjectMessageBoxTitle(),
									I18N.CONSTANTS.confirmDeleteProjectMessageBoxContent(),
									new Listener<MessageBoxEvent>() {

										@Override
										public void handleEvent(MessageBoxEvent be) {
											Button selectedButton = be.getButtonClicked();
											if (selectedButton.getItemId().equals(Dialog.YES)) {
												Map<String, Object> changes = new HashMap<String, Object>();
												changes.put("dateDeleted", new Date());
												UpdateEntity updateEntity = new UpdateEntity(currentProjectDTO, changes);
												dispatcher.execute(updateEntity, null, new AsyncCallback<VoidResult>() {

													@Override
													public void onFailure(Throwable arg0) {
														// TODO Auto-generated
														// method stub

													}

													@Override
													public void onSuccess(VoidResult arg0) {
														ProjectPresenter.this.eventBus
																		.fireEvent(AppEvents.DeleteProject,
																						new ProjectEvent(
																										AppEvents.DeleteProject,
																										ProjectPresenter.this.currentProjectDTO
																														.getId()));
														Notification.show(
																		I18N.CONSTANTS.deleteProjectNotificationTitle(),
																		I18N.CONSTANTS.deleteProjectNotificationContent());
													}
												});
											}

										}
									});
				}
			});

			this.view.getTabPanel().add(deleteAnchor);
		}
	}

	private CheckBox createCheckBox(String label) {
		CheckBox box = new CheckBox();
		box.setBoxLabel(label);
		return box;
	}

	private void selectTab(int index, boolean force) {
		final ToggleAnchor anchor = (ToggleAnchor) this.view.getTabPanel().getWidget(index);

		if (currentTab != anchor) {
			if (currentTab != null)
				currentTab.toggleAnchorMode();

			anchor.toggleAnchorMode();
			currentTab = anchor;
			ProjectPresenter.this.view.setMainPanel(presenters[index].getView());
			presenters[index].viewDidAppear();
		} else if (force) {
			ProjectPresenter.this.view.setMainPanel(presenters[index].getView());
			presenters[index].viewDidAppear();
		}
	}

	@Override
	public boolean navigate(final PageState place) {
		final ProjectState projectState = (ProjectState) place;
		final int projectId = projectState.getProjectId();

		if (ProfileUtils.isGranted(authentication, GlobalPermissionEnum.VIEW_PROJECT)) {

			view.sufficient();

			if (currentProjectDTO == null || projectId != currentProjectDTO.getId()) {
				if (Log.isDebugEnabled()) {
					Log.debug("Loading project #" + projectId + "...");
				}

				dispatcher.execute(new GetProject(projectId), null, new AsyncCallback<ProjectDTO>() {

					@Override
					public void onFailure(Throwable throwable) {
						Log.error("Error, project #" + projectId + " not loaded.");
					}

					@Override
					public void onSuccess(ProjectDTO projectDTO) {

						if (projectDTO == null) {
							Log.error("Project not loaded : " + projectId);
							view.insufficient();
						} else {

							if (Log.isDebugEnabled()) {
								Log.debug("Project loaded : " + projectDTO.getName());
							}

							currentState = projectState;

							boolean projectChanged = !projectDTO.equals(currentProjectDTO);

							projectState.setTabTitle(projectDTO.getName());
							loadProjectOnView(projectDTO);

							selectTab(projectState.getCurrentSection(), projectChanged);
						}
					}
				});
			} else {
				boolean change = false;

				if (!currentState.equals(projectState)) {
					change = true;
					currentState = projectState;
				}

				selectTab(projectState.getCurrentSection(), change);
			}
		} else {
			view.insufficient();
		}

		return true;
	}

	/**
	 * Loads a {@link ProjectDTO} object on the view.
	 * 
	 * @param projectDTO
	 *            the {@link ProjectDTO} object loaded on the
	 *            viewprojectIndicators
	 */
	private void loadProjectOnView(ProjectDTO projectDTO) {
		currentProjectDTO = projectDTO;
		currentDisplayedPhaseDTO = projectDTO.getCurrentPhaseDTO();

		refreshBanner();
		refreshAmendment();

		for (SubPresenter presenter : presenters) {
			if (presenter instanceof ProjectSubPresenter) {
				((ProjectSubPresenter) presenter).loadProject(projectDTO);
			}
		}

	}

	public void ReloadProjectOnView(ProjectDTO projectDTO) {
		currentProjectDTO = projectDTO;
		currentDisplayedPhaseDTO = projectDTO.getCurrentPhaseDTO();

		refreshBanner();
		refreshAmendment();

		for (SubPresenter presenter : presenters) {
			if (presenter instanceof ProjectSubPresenter) {
				((ProjectSubPresenter) presenter).loadProject(projectDTO);
			}
		}
	}

	public ProjectDTO getCurrentProjectDTO() {
		return currentProjectDTO;
	}

	public void setCurrentProjectDTO(ProjectDTO currentProjectDTO) {
		this.currentProjectDTO = currentProjectDTO;
	}

	public PhaseDTO getCurrentDisplayedPhaseDTO() {
		return currentDisplayedPhaseDTO;
	}

	public void setCurrentDisplayedPhaseDTO(PhaseDTO currentPhaseDTO) {
		this.currentDisplayedPhaseDTO = currentPhaseDTO;
	}

	/**
	 * Refreshes the project banner for the current project.
	 */
	public void refreshBanner() {

		// Panel.
		final ContentPanel panel = view.getPanelProjectBanner();

		// Set the heading of panel
		String projectTitle = currentProjectDTO.getFullName();
		String titleToDisplay = "";
		if (projectTitle != null && !projectTitle.isEmpty())
			titleToDisplay = projectTitle.length() > 110 ? projectTitle.substring(0, 110) + "..." : projectTitle;

		panel.setHeading(I18N.CONSTANTS.projectMainTabTitle() + ' ' + currentProjectDTO.getName() + " ("
						+ titleToDisplay + ")");

		// Set the tool tip
		ToolTipConfig panelToolTipconfig = new ToolTipConfig();
		panelToolTipconfig.setMaxWidth(500);
		panelToolTipconfig.setText(projectTitle);
		panel.setToolTip(panelToolTipconfig);

		panel.removeAll();

		final Grid gridPanel = new Grid(1, 2);
		gridPanel.addStyleName("banner");
		gridPanel.setCellPadding(0);
		gridPanel.setCellSpacing(0);
		gridPanel.setWidth("100%");
		gridPanel.setHeight("100%");

		// Logo.
		final Image logo = FundingIconProvider.getProjectTypeIcon(
						currentProjectDTO.getProjectModelDTO().getVisibility(authentication.getOrganizationId()),
						FundingIconProvider.IconSize.LARGE).createImage();
		gridPanel.setWidget(0, 0, logo);
		gridPanel.getCellFormatter().addStyleName(0, 0, "banner-logo");

		// Banner.
		final ProjectBannerDTO banner = currentProjectDTO.getProjectModelDTO().getProjectBannerDTO();
		final LayoutDTO layout = banner.getLayoutDTO();

		// Executes layout.
		if (banner != null && layout != null && layout.getLayoutGroupsDTO() != null
						&& !layout.getLayoutGroupsDTO().isEmpty()) {

			// For visibility constraints, the banner accept a maximum of 2 rows
			// and 4 columns.
			final int rows = layout.getRowsCount() > 2 ? 2 : layout.getRowsCount();
			final int cols = layout.getColumnsCount() > 4 ? 4 : layout.getColumnsCount();

			final Grid gridLayout = new Grid(rows, cols);
			gridLayout.addStyleName("banner-flex");
			gridLayout.setCellPadding(0);
			gridLayout.setCellSpacing(0);
			gridLayout.setWidth("100%");
			gridLayout.setHeight("100%");

			for (int i = 0; i < gridLayout.getColumnCount() - 1; i++) {
				gridLayout.getColumnFormatter().setWidth(i, "325px");
			}

			for (final LayoutGroupDTO groupLayout : layout.getLayoutGroupsDTO()) {

				// Checks group bounds.
				if (groupLayout.getRow() + 1 > rows || groupLayout.getColumn() + 1 > cols) {
					continue;
				}

				final ContentPanel groupPanel = new ContentPanel();
				groupPanel.setLayout(new FormLayout());
				groupPanel.setTopComponent(null);
				groupPanel.setHeaderVisible(false);

				gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), groupPanel);

				if (groupLayout.getLayoutConstraintsDTO() != null) {
					for (final LayoutConstraintDTO constraint : groupLayout.getLayoutConstraintsDTO()) {

						final FlexibleElementDTO element = constraint.getFlexibleElementDTO();

						// Only default elements are allowed.
						if (!(element instanceof DefaultFlexibleElementDTO)) {
							continue;
						}

						// Builds the graphic component
						final DefaultFlexibleElementDTO defaultElement = (DefaultFlexibleElementDTO) element;
						defaultElement.setService(dispatcher);
						defaultElement.setAuthentication(authentication);
						defaultElement.setCache(cache);
						defaultElement.setCurrentContainerDTO(currentProjectDTO);

						Integer amendmentId = null;
						if (currentProjectDTO.getCurrentAmendment() != null)
							amendmentId = currentProjectDTO.getCurrentAmendment().getId();
						
						// Remote call to ask for this element value.
						final GetValue command = new GetValue(currentProjectDTO.getId(), element.getId(),
										element.getEntityName(), amendmentId);
						
						dispatcher.execute(command, null, new AsyncCallback<ValueResult>() {

							@Override
							public void onFailure(Throwable throwable) {
								Log.error("Error, element value not loaded.");
							}

							@Override
							public void onSuccess(ValueResult valueResult) {

								if (Log.isDebugEnabled()) {
									Log.debug("Element value(s) object : " + valueResult);
								}

								final Component component;
								if (defaultElement instanceof BudgetElementDTO) {
									component = defaultElement.getElementComponentInBanner(valueResult);

								} else {
									component = defaultElement.getElementComponentInBanner(null);
								}

								if (component != null) {

									if (component instanceof LabelField) {
										LabelField lableFieldComponent = (LabelField) component;
										// Get the text of the field
										String textValue = lableFieldComponent.getText();

										// Set the tool tip
										ToolTipConfig config = new ToolTipConfig();
										config.setMaxWidth(500);
										config.setText(textValue);
										lableFieldComponent.setToolTip(config);

										// Clip the text if it is longer than 30
										if (textValue != null && !textValue.isEmpty()) {
											String newTextValue = textValue.length() > 30 ? textValue.substring(0, 29)
															+ "..." : textValue;
											lableFieldComponent.setText(newTextValue);
										}

										groupPanel.add(lableFieldComponent);
									} else {
										groupPanel.add(component);
									}
									groupPanel.layout();

								}
							}
						});

						// Only one element per cell.
						break;
					}
				}
			}

			gridPanel.setWidget(0, 1, gridLayout);
		}
		// Default banner.
		else {

			panel.setLayout(new FormLayout());

			final LabelField codeField = new LabelField();
			codeField.setReadOnly(true);
			codeField.setFieldLabel(I18N.CONSTANTS.projectName());
			codeField.setLabelSeparator(":");
			codeField.setValue(currentProjectDTO.getName());

			gridPanel.setWidget(0, 1, codeField);
		}

		panel.add(gridPanel);
		panel.layout();
	}

	private void refreshAmendment() {
		Log.debug("Loading amendments for project '" + currentProjectDTO.getName() + "'...");

		final ContentPanel amendmentBox = view.getAmendmentBox();
		amendmentBox.removeAll();

		// Prepare the amendment store
		final ListStore<AmendmentDTO> store = new ListStore<AmendmentDTO>();

		for (final AmendmentDTO amendmentDTO : currentProjectDTO.getAmendments()) {
			amendmentDTO.prepareName();
			store.add(amendmentDTO);
		}

		// Adding the current amendment
		final AmendmentDTO currentAmendment = new AmendmentDTO(currentProjectDTO);
		store.add(currentAmendment);

		// Creating the amendment list
		final ComboBox<AmendmentDTO> versionList = new ComboBox<AmendmentDTO>();
		versionList.setStore(store);
		versionList.setTriggerAction(ComboBox.TriggerAction.ALL);

		versionList.setValue(currentAmendment); // Selecting the
												// currentAmendment

		Log.debug(store.getCount() + " amendment(s).");

		final Button displayAmendmentButton = new Button(I18N.CONSTANTS.amendmentDisplay());
		displayAmendmentButton.setEnabled(false);

		versionList.addSelectionChangedListener(new SelectionChangedListener<AmendmentDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<AmendmentDTO> se) {
				int currentAmendmentId = 0;
				if (currentProjectDTO.getCurrentAmendment() != null)
					currentAmendmentId = currentProjectDTO.getCurrentAmendment().getId();

				Log.debug("Current " + currentAmendmentId + " / Selected " + se.getSelectedItem().getId());

				displayAmendmentButton.setEnabled(se.getSelectedItem().getId() != currentAmendmentId);
			}
		});

		displayAmendmentButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				NavigationCallback navigationCallback = new NavigationCallback() {

					@Override
					public void onDecided(NavigationError navigationError) {
						if (navigationError == NavigationError.NONE) {
							AmendmentDTO amendmentDTO = versionList.getSelection().get(0);
							if (amendmentDTO.getId() == 0)
								amendmentDTO = null;
							else
								Log.debug("Back to " + amendmentDTO.getId());

							currentProjectDTO.setCurrentAmendment(amendmentDTO);

							// Refreshing the whole view
							discardAllViews();
							selectTab(currentState.getCurrentSection(), true);
							displayAmendmentButton.setEnabled(false);
						}
					}

				};
				requestToNavigateAway(null, navigationCallback);
			}
		});

		final LayoutContainer amendmentListContainer = new LayoutContainer(new HBoxLayout());
		amendmentListContainer.add(versionList);
		amendmentListContainer.add(displayAmendmentButton, new HBoxLayoutData(0, 0, 0, 4));

		amendmentBox.add(amendmentListContainer, new VBoxLayoutData(0, 0, 3, 0));

		if (ProfileUtils.isGranted(authentication, GlobalPermissionEnum.EDIT_PROJECT)) {

			// Displaying the available actions
			final Amendment.Action[] actions;
			if (currentProjectDTO.getAmendmentState() != null)
				actions = currentProjectDTO.getAmendmentState().getActions();
			else
				actions = new Amendment.Action[0];
			final Anchor[] anchors = new Anchor[actions.length];

			for (int index = 0; index < actions.length; index++) {
				final Amendment.Action action = actions[index];
				Log.debug("Adding the " + action + " amendment action.");

				if (action == Amendment.Action.VALIDATE || action == Amendment.Action.REJECT) {
					if (!ProfileUtils.isGranted(authentication, GlobalPermissionEnum.VALID_AMENDEMENT)) {
						Log.debug("You can not validate !");
						continue;
					}

				}

				Log.debug("You can  validate !");

				final Anchor actionAnchor = new Anchor(amendmentActionDisplayNames.get(action));
				actionAnchor.addStyleName("amendment-action");

				actionAnchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// Disabling every actions before sending the request

						NavigationCallback navigationCallback = new NavigationCallback() {

							@Override
							public void onDecided(NavigationError navigationError) {
								// TODO Auto-generated method stub
								if (navigationError == NavigationError.NONE) {
									amendmentBox.mask(I18N.CONSTANTS.loading());

									for (final Anchor anchor : anchors) {
										if (anchor == null)
											Log.debug("anchor is null");
										if (anchor != null)
											anchor.setEnabled(false);
									}

									final AmendmentAction amendmentAction = new AmendmentAction(currentProjectDTO
													.getId(), action);
									dispatcher.execute(amendmentAction, null, new AsyncCallback<ProjectDTO>() {

										@Override
										public void onFailure(Throwable caught) {
											// Failures may happen if an other
											// user changes
											// the
											// amendment state.
											// TODO: we should maybe refresh the
											// project or
											// tell
											// the user to refresh the page.
											MessageBox.alert(amendmentActionDisplayNames.get(action),
															I18N.CONSTANTS.amendmentActionError(), null);
											for (final Anchor anchor : anchors)
												anchor.setEnabled(true);
											amendmentBox.unmask();
										}

										@Override
										public void onSuccess(ProjectDTO result) {
											for (final Anchor anchor : anchors) {
												if (anchor != null)
													anchor.setEnabled(true);
											}

											// Updating the current project
											currentProjectDTO = result;

											// Refreshing the whole view
											discardAllViews();
											selectTab(currentState.getCurrentSection(), true);
											refreshAmendment();

											amendmentBox.unmask();
										}
									});
								}
							}
						};
						requestToNavigateAway(null, navigationCallback);
					}

				});

				amendmentBox.add(actionAnchor, new VBoxLayoutData());
				anchors[index] = actionAnchor;

			}

		}

		amendmentBox.layout();
	}

	private void discardAllViews() {
		for (final SubPresenter presenter : presenters)
			presenter.discardView();
	}

	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.projectMainTabTitle();
	}

	@Override
	public PageId getPageId() {
		return PAGE_ID;
	}

	@Override
	public Object getWidget() {
		return view;
	}

	@Override
	public void requestToNavigateAway(PageState place, final NavigationCallback callback) {
		NavigationError navigationError = NavigationError.NONE;
		for (SubPresenter subPresenter : presenters) {
			if (subPresenter.hasValueChanged()) {
				navigationError = NavigationError.WORK_NOT_SAVED;
			}
		}

		Listener<MessageBoxEvent> listener = new Listener<MessageBoxEvent>() {

			@Override
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					for (SubPresenter subPresenter : presenters) {
						subPresenter.forgetAllChangedValues();
					}
					callback.onDecided(NavigationError.NONE);
				}
			}
		};

		if (navigationError == NavigationError.WORK_NOT_SAVED) {
			MessageBox.confirm(I18N.CONSTANTS.unsavedDataTitle(), I18N.CONSTANTS.unsavedDataMessage(), listener);
		}

		callback.onDecided(navigationError);
	}

	@Override
	public String beforeWindowCloses() {
		return null;
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void setActivePage(Page page) {
		this.activePage = page;
	}

	@Override
	public Page getActivePage() {
		return this.activePage;
	}

	public ProjectState getCurrentState() {
		return currentState;
	}

	@Override
	public AsyncMonitor showLoadingPlaceHolder(PageId pageId, PageState loadingPlace) {
		return null;
	}

}
