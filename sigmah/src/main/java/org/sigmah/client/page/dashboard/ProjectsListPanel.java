package org.sigmah.client.page.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.client.page.project.category.CategoryIconProvider;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.ui.RatioBar;
import org.sigmah.client.util.DateUtils;
import org.sigmah.client.util.Notification;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.UpdateProjectFavorite;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.handler.ValueEvent;
import org.sigmah.shared.dto.profile.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget which display a list of projects.
 * 
 * @author tmi
 * 
 */
public class ProjectsListPanel {

    /**
     * A tree store with some useful dedicated methods.
     * 
     * @author tmi
     * 
     */
    public static class ProjectStore extends TreeStore<ProjectDTOLight> {
    }

    /**
     * Defines the refreshing mode.
     * 
     * @author tmi
     */
    public static enum RefreshMode {

        /**
         * The project list is refreshed each time the
         * {@link ProjectsListPanel#refresh(boolean, Integer...)} method is
         * called.
         */
        AUTOMATIC,

        /**
         * The project list is refreshed each time user clicks on the refresh
         * button.
         */
        BUTTON,

        /**
         * Refresh the project list when {@link #refresh(boolean, Integer...)}
         * if called for the first time. Subsequent refreshs are called by the
         * refresh button.
         */
        BOTH;
    }

    /**
     * Defines the loading mode.
     * 
     * @author tmi
     */
    public static enum LoadingMode {

        /**
         * The projects list id loaded by one single server call.
         */
        ONE_TIME,

        /**
         * The projects list is loaded by chunks and a progress bar informs of
         * the loading process.
         */
        CHUNK;
    }

    private final Dispatcher dispatcher;
    private final Authentication authentication;
    private final ContentPanel projectTreePanel;
    private final TreeGrid<ProjectDTOLight> projectTreeGrid;
    private final Radio ngoRadio;
    private final Radio fundingRadio;
    private final Radio partnerRadio;
    private final Button filterButton;

    /** Current projects grid parameters. */
    private ProjectModelType currentModelType;
    private final ArrayList<Integer> orgUnitsIds;

    /** The refreshing mode (automatic by default) */
    private final RefreshMode refreshMode;
    private final com.extjs.gxt.ui.client.widget.Label refreshDateLabel;

    /** The loading mode (one time by default) */
    private final LoadingMode loadingMode;

    /**
     * true if {@link #refresh(boolean, Integer...)} has already been called at
     * least one time
     */
    private static boolean isLoaded = false;

    /** The GetProjects command which will be executed for the next refresh. */
    private GetProjects command;

    /**
     * Builds a new project list panel with the default refreshing mode to
     * {@link RefreshMode#AUTOMATIC}.
     * 
     * @param dispatcher
     *            The dispatcher.
     * @param authentication
     *            The current authentication.
     */
    public ProjectsListPanel(Dispatcher dispatcher, Authentication authentication) {
        this(dispatcher, authentication, RefreshMode.AUTOMATIC, LoadingMode.ONE_TIME);
    }

    /**
     * Builds a new project list panel.
     * 
     * @param dispatcher
     *            The dispatcher.
     * @param authentication
     *            The current authentication.
     * @param refreshMode
     *            The refreshing mode.
     */
    public ProjectsListPanel(Dispatcher dispatcher, Authentication authentication, RefreshMode refreshMode) {
        this(dispatcher, authentication, refreshMode, LoadingMode.ONE_TIME);
    }

    /**
     * Builds a new project list panel.
     * 
     * @param dispatcher
     *            The dispatcher.
     * @param authentication
     *            The current authentication.
     * @param refreshMode
     *            The refreshing mode.
     * @param loadingMode
     *            The loading mode.
     */
    public ProjectsListPanel(Dispatcher dispatcher, Authentication authentication, RefreshMode refreshMode,
            LoadingMode loadingMode) {

        this.dispatcher = dispatcher;
        this.authentication = authentication;
        this.refreshMode = refreshMode;
        this.loadingMode = loadingMode;

        // Default filters parameters.
        orgUnitsIds = new ArrayList<Integer>();
        currentModelType = ProjectModelType.NGO;

        // Store.
        final ProjectStore projectStore = new ProjectStore();
        projectStore.setMonitorChanges(true);

        // Default sort order of the projects grid.
        projectStore.setSortInfo(new SortInfo("name", SortDir.ASC));

        // Grid.
        projectTreeGrid = new TreeGrid<ProjectDTOLight>(projectStore, getProjectGridColumnModel());
        projectTreeGrid.setBorders(true);
        projectTreeGrid.getStyle().setNodeOpenIcon(null);
        projectTreeGrid.getStyle().setNodeCloseIcon(null);
        projectTreeGrid.getStyle().setLeafIcon(null);
        projectTreeGrid.setAutoExpandColumn("fullName");
        projectTreeGrid.setTrackMouseOver(false);
        projectTreeGrid.setAutoExpand(true);

        // Store.
        projectStore.setStoreSorter(new StoreSorter<ProjectDTOLight>() {
            @Override
            public int compare(Store<ProjectDTOLight> store, ProjectDTOLight m1, ProjectDTOLight m2, String property) {

                if ("name".equals(property)) {
                    return m1.getName().compareToIgnoreCase(m2.getName());
                } else if ("fullName".equals(property)) {
                    return m1.getFullName().compareToIgnoreCase(m2.getFullName());
                } else if ("phase".equals(property)) {
                    return m1.getCurrentPhaseName().compareToIgnoreCase(m2.getCurrentPhaseName());
                } else if ("orgUnitName".equals(property)) {
                    return m1.getOrgUnitName().compareToIgnoreCase(m2.getOrgUnitName());
                } else if ("spentBudget".equals(property)) {
                    final Double d1 = NumberUtils.adjustRatio(NumberUtils.ratio(m1.getSpendBudget(),
                            m1.getPlannedBudget()));
                    final Double d2 = NumberUtils.adjustRatio(NumberUtils.ratio(m2.getSpendBudget(),
                            m2.getPlannedBudget()));
                    return d1.compareTo(d2);
                } else if ("time".equals(property)) {
                    final Double d1 = m1.getElapsedTime();
                    final Double d2 = m2.getElapsedTime();
                    return d1.compareTo(d2);
                } else if ("activity".equals(property)) {
                    return 0;
                } else if ("category".equals(property)) {
                    return 0;
                } else {
                    return super.compare(store, m1, m2, property);
                }
            }
        });

        // Top panel
        final RadioGroup group = new RadioGroup("projectTypeFilter");
        group.setFireChangeEventOnSetValue(true);

        ngoRadio = new Radio();
        ngoRadio.setFireChangeEventOnSetValue(true);
        ngoRadio.setValue(true);
        ngoRadio.setFieldLabel(ProjectModelType.getName(ProjectModelType.NGO));
        ngoRadio.addStyleName("toolbar-radio");

        final WidgetComponent ngoIcon = new WidgetComponent(FundingIconProvider.getProjectTypeIcon(
                ProjectModelType.NGO, IconSize.SMALL).createImage());
        ngoIcon.addStyleName("toolbar-icon");

        final Label ngoLabel = new Label(ProjectModelType.getName(ProjectModelType.NGO));
        ngoLabel.addStyleName("flexibility-element-label");
        ngoLabel.addStyleName("project-starred-icon");
        ngoLabel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ngoRadio.setValue(true);
                fundingRadio.setValue(false);
                partnerRadio.setValue(false);
            }
        });

        fundingRadio = new Radio();
        fundingRadio.setFireChangeEventOnSetValue(true);
        fundingRadio.setFieldLabel(ProjectModelType.getName(ProjectModelType.FUNDING));
        fundingRadio.addStyleName("toolbar-radio");

        final WidgetComponent fundingIcon = new WidgetComponent(FundingIconProvider.getProjectTypeIcon(
                ProjectModelType.FUNDING, IconSize.SMALL).createImage());
        fundingIcon.addStyleName("toolbar-icon");

        final Label fundingLabel = new Label(ProjectModelType.getName(ProjectModelType.FUNDING));
        fundingLabel.addStyleName("flexibility-element-label");
        fundingLabel.addStyleName("project-starred-icon");
        fundingLabel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ngoRadio.setValue(false);
                fundingRadio.setValue(true);
                partnerRadio.setValue(false);
            }
        });

        partnerRadio = new Radio();
        partnerRadio.setFireChangeEventOnSetValue(true);
        partnerRadio.setFieldLabel(ProjectModelType.getName(ProjectModelType.LOCAL_PARTNER));
        partnerRadio.addStyleName("toolbar-radio");

        final WidgetComponent partnerIcon = new WidgetComponent(FundingIconProvider.getProjectTypeIcon(
                ProjectModelType.LOCAL_PARTNER, IconSize.SMALL).createImage());
        partnerIcon.addStyleName("toolbar-icon");

        final Label partnerLabel = new Label(ProjectModelType.getName(ProjectModelType.LOCAL_PARTNER));
        partnerLabel.addStyleName("flexibility-element-label");
        partnerLabel.addStyleName("project-starred-icon");
        partnerLabel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ngoRadio.setValue(false);
                fundingRadio.setValue(false);
                partnerRadio.setValue(true);
            }
        });

        final HTML headLabel = new HTML("&nbsp;&nbsp;" + I18N.CONSTANTS.projectTypeFilter() + ": ");
        headLabel.addStyleName("flexibility-element-label");

        group.add(ngoRadio);
        group.add(fundingRadio);
        group.add(partnerRadio);
        group.setAutoWidth(true);

        // Expand all button.
        final Button expandButton = new Button("", IconImageBundle.ICONS.expand(),
                new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        projectTreeGrid.expandAll();
                    }
                });

        // Collapse all button.
        final Button collapseButton = new Button("", IconImageBundle.ICONS.collapse(),
                new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        projectTreeGrid.collapseAll();
                    }
                });

        // Filter button.
        filterButton = new Button(I18N.CONSTANTS.filter(), IconImageBundle.ICONS.filter());

        // Refresh button.
        final Button refreshButton = new Button(I18N.CONSTANTS.refreshProjectList(), IconImageBundle.ICONS.refresh(),
                new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        // Explicit refresh.
                        refreshProjectGrid(command);
                    }
                });
        refreshButton.setToolTip(I18N.CONSTANTS.refreshProjectListDetails());
        refreshButton.addStyleName("project-refresh-button");

        // Refresh date.
        refreshDateLabel = new com.extjs.gxt.ui.client.widget.Label();

        final ToolBar toolbar = new ToolBar();
        if (refreshMode == RefreshMode.BUTTON || refreshMode == RefreshMode.BOTH) {
            toolbar.add(refreshButton);
            toolbar.add(refreshDateLabel);
            toolbar.add(new SeparatorToolItem());
        }
        toolbar.add(expandButton);
        toolbar.add(collapseButton);
        toolbar.add(new SeparatorToolItem());
        toolbar.add(filterButton);
        toolbar.add(new WidgetComponent(headLabel));
        toolbar.add(ngoRadio);
        toolbar.add(ngoIcon);
        toolbar.add(new WidgetComponent(ngoLabel));
        toolbar.add(fundingRadio);
        toolbar.add(fundingIcon);
        toolbar.add(new WidgetComponent(fundingLabel));
        toolbar.add(partnerRadio);
        toolbar.add(partnerIcon);
        toolbar.add(new WidgetComponent(partnerLabel));

        // Panel
        projectTreePanel = new ContentPanel(new FitLayout());
        projectTreePanel.setHeading(I18N.CONSTANTS.projects());

        projectTreePanel.setTopComponent(toolbar);

        if (ProfileUtils.isGranted(authentication, GlobalPermissionEnum.VIEW_PROJECT)) {
            projectTreePanel.add(projectTreeGrid);
        } else {
            final HTML insufficient = new HTML(I18N.CONSTANTS.permViewProjectsInsufficient());
            insufficient.addStyleName("important-label");
            projectTreePanel.add(insufficient);
        }

        addListeners();
        addFilters();
    }

    /**
     * Builds and returns the columns model for the projects tree grid.
     * 
     * @return The project tree grid columns model.
     */
    private ColumnModel getProjectGridColumnModel() {

        final DateTimeFormat format = DateUtils.DATE_SHORT;

        // Starred icon
        final ColumnConfig starredIconColumn = new ColumnConfig("starred", "", 24);
        starredIconColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {
            private final DashboardImageBundle imageBundle = GWT.create(DashboardImageBundle.class);

            @Override
            public Object render(final ProjectDTOLight model, String property, ColumnData config, int rowIndex,
                    int colIndex, final ListStore<ProjectDTOLight> store, final Grid<ProjectDTOLight> grid) {
            	
            	//A star icon
                final Image icon;
                                             
                if (isFavoriteProject(authentication.getUserId(),model)) {
                	//star
                    icon = imageBundle.star().createImage();
                    icon.setTitle("Favorite");
                } else {
                	//empty start
                    icon = imageBundle.emptyStar().createImage();
                    icon.setTitle("Non-favorite");
                }

                //Star icon click-handler
                icon.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                    	UpdateProjectFavorite updateCmd ;
                    	
                    	//Get the project's id
                    	int projectId = model.getId();
                		if(projectId<0)
                			projectId = model.get("pid");
                    	
                    	
                    	if(isFavoriteProject(authentication.getUserId(),model))
                    	{//Remove the favorite user from project favorite user list
                    	              	   
                    		 updateCmd = new UpdateProjectFavorite(projectId,UpdateProjectFavorite.UpdateType.REMOVE);
                    		                    		
                    	}
                    	else
                    	{//Add current user into the favorite user list of the project
                    		
                    		 updateCmd = new UpdateProjectFavorite(projectId,UpdateProjectFavorite.UpdateType.ADD);                   		
                    		
                    	}
                    	
                    	//RPC to change the favorite tag
                    	dispatcher.execute(updateCmd, new MaskingAsyncMonitor(projectTreePanel, I18N.CONSTANTS.loading()), new AsyncCallback<CreateResult>(){

							@Override
							public void onFailure(Throwable caught) {
								
								  Log.error(
	                                        "[execute] Error while setting the favorite status of the project #"
	                                                + model.getId(), caught);
	                                MessageBox.alert(I18N.CONSTANTS.projectStarredError(),
	                                        I18N.CONSTANTS.projectStarredErrorDetails(), null);
	                            
								
							}

							@Override
							public void onSuccess(CreateResult result) {
								
								if(result==null || result.getEntity()==null)
								{
		                                MessageBox.alert(I18N.CONSTANTS.projectStarredError(),
		                                        I18N.CONSTANTS.projectStarredErrorDetails(), null);
								}
								else
								{
									//Update local store
									ProjectDTOLight resultProject =(ProjectDTOLight) result.getEntity();
									model.setFavoriteUsers(resultProject.getFavoriteUsers());
									store.update(model);
									
									Notification.show(I18N.CONSTANTS.infoConfirmation(),
                                            I18N.CONSTANTS.projectStarred());
								 
																													
								}
								
								
							}
                			
                		});
                    	                    	
                 
                    }
                });

                icon.addStyleName("project-starred-icon");

                return icon;
            }
        });

        // Code
        final ColumnConfig codeColumn = new ColumnConfig("name", I18N.CONSTANTS.projectName(), 110);
        codeColumn.setRenderer(new WidgetTreeGridCellRenderer<ProjectDTOLight>() {
            @Override
            public Widget getWidget(ProjectDTOLight model, String property, ColumnData config, int rowIndex,
                    int colIndex, ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {

                Integer id = model.get("id");
                if (id <= 0) {
                    id = model.get("pid");
                }

                final Hyperlink h = new Hyperlink((String) model.get(property), true, ProjectPresenter.PAGE_ID
                        .toString() + '!' + String.valueOf(id));
                if (!model.isLeaf()) {
                    h.addStyleName("project-grid-node");
                } else {
                    h.addStyleName("project-grid-leaf");
                }

                final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 2);
                panel.setCellPadding(0);
                panel.setCellSpacing(0);

                panel.setWidget(
                        0,
                        0,
                        FundingIconProvider.getProjectTypeIcon(
                                model.getProjectModelType(authentication.getOrganizationId()), IconSize.SMALL_MEIDUM)
                                .createImage());
                panel.getCellFormatter().addStyleName(0, 0, "project-grid-code-icon");
                panel.setWidget(0, 1, h);
                panel.getCellFormatter().addStyleName(0, 1, "project-grid-code");

                return panel;
            }
        });

        // Title
        final ColumnConfig titleColumn = new ColumnConfig("fullName", I18N.CONSTANTS.projectFullName(), 230);
        titleColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {

            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {
                String title = (String) model.get(property);
                if (model.getParent() != null) {
                    title = "&nbsp;&nbsp;&nbsp;&nbsp;" + title;
                }
                return createProjectGridText(model, title);
            }
        });

        // Current phase
        final ColumnConfig currentPhaseName = new ColumnConfig("phase", I18N.CONSTANTS.projectActivePhase(), 150);
        currentPhaseName.setRenderer(new GridCellRenderer<ProjectDTOLight>() {
            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {
                return createProjectGridText(model, model.getCurrentPhaseName());
            }
        });

        // Org Unit
        final ColumnConfig orgUnitColumn = new ColumnConfig("orgUnitName", I18N.CONSTANTS.orgunit(), 150);
        orgUnitColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {

            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {
                return createProjectGridText(model, (String) model.get(property));
            }
        });

        // Ratio budget
        final ColumnConfig spentBudgetColumn = new ColumnConfig("spentBudget", I18N.CONSTANTS.projectSpendBudget(), 100);
        spentBudgetColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {

            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {
                return new RatioBar(NumberUtils.ratio(model.getSpendBudget(), model.getPlannedBudget()));
            }
        });

        // Planned budget
        final ColumnConfig plannedBudgetColumn = new ColumnConfig("plannedBudget",
                I18N.CONSTANTS.projectPlannedBudget(), 75);
        plannedBudgetColumn.setHidden(true);

        // Spend budget
        final ColumnConfig spendBudgetColumn = new ColumnConfig("spendBudget", I18N.CONSTANTS.projectSpendBudget(), 75);
        spendBudgetColumn.setHidden(true);

        // Received budget
        final ColumnConfig receivedBudgetColumn = new ColumnConfig("receivedBudget",
                I18N.CONSTANTS.projectReceivedBudget(), 75);
        receivedBudgetColumn.setHidden(true);

        // Time
        final ColumnConfig timeColumn = new ColumnConfig("time", I18N.CONSTANTS.projectTime(), 100);
        timeColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {

            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {
                return new RatioBar(model.getElapsedTime());
            }
        });

        // Start date
        final ColumnConfig startDateColumn = new ColumnConfig("startDate", I18N.CONSTANTS.projectStartDate(), 75);
        startDateColumn.setHidden(true);
        startDateColumn.setDateTimeFormat(format);
        startDateColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {

            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {
                final Date d = (Date) model.get(property);
                return createProjectGridText(model, d != null ? format.format(d) : "");
            }
        });

        // End date
        final ColumnConfig endDateColumn = new ColumnConfig("endDate", I18N.CONSTANTS.projectEndDate(), 75);
        endDateColumn.setDateTimeFormat(format);
        endDateColumn.setHidden(true);
        endDateColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {

            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {
                final Date d = (Date) model.get(property);
                return createProjectGridText(model, d != null ? format.format(d) : "");
            }
        });

        // Close date
        final ColumnConfig closeDateColumn = new ColumnConfig("closeDate", I18N.CONSTANTS.projectClosedDate(), 75);
        closeDateColumn.setDateTimeFormat(format);
        closeDateColumn.setHidden(true);
        closeDateColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {

            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {
                final Date d = (Date) model.get(property);
                return createProjectGridText(model, d != null ? format.format(d) : "");
            }
        });

        // Activity
        final ColumnConfig activityColumn = new ColumnConfig("activity", I18N.CONSTANTS.logFrameActivity(), 100);
        activityColumn.setSortable(false);
        activityColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {

            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {
                return new RatioBar(model.getActivityAdvancement() != null ? model.getActivityAdvancement() : 0);
            }
        });

        // Category
        final ColumnConfig categoryColumn = new ColumnConfig("category", I18N.CONSTANTS.category(), 150);
        categoryColumn.setSortable(false);
        categoryColumn.setRenderer(new GridCellRenderer<ProjectDTOLight>() {

            @Override
            public Object render(ProjectDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ProjectDTOLight> store, Grid<ProjectDTOLight> grid) {

                final Set<CategoryElementDTO> elements = model.getCategoryElements();
                final LayoutContainer panel = new LayoutContainer();
                panel.setLayout(new FlowLayout());
                final FlowData data = new FlowData(new Margins(0, 5, 0, 0));

                if (elements != null) {
                    for (final CategoryElementDTO element : elements) {
                        panel.add(CategoryIconProvider.getIcon(element), data);
                    }
                }

                return panel;
            }
        });

        return new ColumnModel(Arrays.asList(starredIconColumn, codeColumn, titleColumn, currentPhaseName,
                orgUnitColumn, spentBudgetColumn, plannedBudgetColumn, spendBudgetColumn, receivedBudgetColumn,
                startDateColumn, endDateColumn, closeDateColumn, timeColumn, activityColumn, categoryColumn));
    }

    private Object createProjectGridText(ProjectDTOLight model, String content) {
        final Text label = new Text(content);
        if (!model.isLeaf()) {
            label.addStyleName("project-grid-node");
        } else {
            label.addStyleName("project-grid-leaf");
        }
        return label;
    }

    private void addListeners() {

        // Updates the projects grid heading when the store is filtered.
        projectTreeGrid.getTreeStore().addListener(Store.Filter, new Listener<StoreEvent<ProjectDTOLight>>() {

            @Override
            public void handleEvent(StoreEvent<ProjectDTOLight> be) {
                projectTreePanel.setHeading(I18N.CONSTANTS.projects() + " ("
                        + projectTreeGrid.getTreeStore().getChildCount() + ')');
            }
        });

        // Adds actions on filter by model type.
        for (final ProjectModelType type : ProjectModelType.values()) {
            getRadioFilter(type).addListener(Events.Change, new Listener<FieldEvent>() {

                @Override
                public void handleEvent(FieldEvent be) {
                    if (Boolean.TRUE.equals(be.getValue())) {
                        currentModelType = type;
                        applyProjectFilters();
                    }
                }
            });
        }
    }

    private void addFilters() {

        // The filter by model type.
        final StoreFilter<ProjectDTOLight> typeFilter = new StoreFilter<ProjectDTOLight>() {

            @Override
            public boolean select(Store<ProjectDTOLight> store, ProjectDTOLight parent, ProjectDTOLight item,
                    String property) {

                boolean selected = false;

                // Root item.
                if (item.getParent() == null) {
                    // A root item is filtered if its type doesn't match the
                    // current type.
                    selected = item.getVisibility(authentication.getOrganizationId()) == currentModelType;
                }
                // Child item
                else {
                    // A child item is filtered if its parent is filtered.
                    selected = ((ProjectDTOLight) item.getParent()).getVisibility(authentication.getOrganizationId()) == currentModelType;
                }

                return selected;
            }
        };

        getProjectsStore().addFilter(typeFilter);

        // Filters aren't used for the moment.
        filterButton.setVisible(false);
    }

    private void applyProjectFilters() {
        getProjectsStore().applyFilters(null);
    }

    private Radio getRadioFilter(ProjectModelType type) {

        if (type != null) {
            switch (type) {
            case NGO:
                return ngoRadio;
            case FUNDING:
                return fundingRadio;
            case LOCAL_PARTNER:
                return partnerRadio;
            }
        }

        return null;
    }

    /**
     * Display the given date as the last refreshed date.
     * 
     * @param date
     *            The last refreshed date.
     */
    @SuppressWarnings("deprecation")
    private void updateRefreshingDate(Date date) {
        if (date != null) {
            refreshDateLabel.setText("(" + (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + "h"
                    + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) + ")");
        }
    }

    /**
     * Refreshes the projects grid with the current parameters.
     * 
     * @param cmd
     *            The {@link GetProjects} command to execute.
     */
    private void refreshProjectGrid(GetProjects cmd) {

        // Checks that the user can view projects.
        if (!ProfileUtils.isGranted(authentication, GlobalPermissionEnum.VIEW_PROJECT)) {
            return;
        }

        if (cmd == null) {
            return;
        }

        if (loadingMode == LoadingMode.ONE_TIME) {

            dispatcher.execute(cmd, new MaskingAsyncMonitor(projectTreePanel, I18N.CONSTANTS.loading()),
                    new AsyncCallback<ProjectListResult>() {

                        @Override
                        public void onFailure(Throwable e) {
                            Log.error("[GetProjects command] Error while getting projects.", e);
                            // nothing
                        }

                        @Override
                        public void onSuccess(ProjectListResult result) {

                            getProjectsStore().removeAll();
                            getProjectsStore().clearFilters();

                            if (result != null) {
                                final List<ProjectDTOLight> resultList = result.getListProjectsLightDTO();
                                int i = -1;
                                for (final ProjectDTOLight p : resultList) {
                                    try {
                                        // Project id.
                                        p.setProjectId(p.getId());
                                        // Tree id.
                                        p.setId(i--);

                                        for (final ProjectDTOLight c : p.getChildrenProjects()) {
                                            // Project id.
                                            if (c != null) {

                                                c.setProjectId(c.getId());
                                                // Tree id.
                                                c.setId(i--);

                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                getProjectsStore().add(resultList, true);
                            }

                            applyProjectFilters();
                            updateRefreshingDate(new Date());
                        }
                    });
        } else if (loadingMode == LoadingMode.CHUNK) {

            // Builds a new chunks worker.

            int chunSize = 2;
            try {
                chunSize = Integer.parseInt(Window.Location.getParameter("chunk"));
                if (chunSize <= 0) {
                    chunSize = 2;
                }
            } catch (Throwable e) {
                // swallow exception.
            }

            final GetProjectsWorker worker = new GetProjectsWorker(dispatcher, cmd, projectTreePanel, chunSize);
            worker.addWorkerListener(new GetProjectsWorker.WorkerListener() {

                private int index = -1;

                @Override
                public void serverError(Throwable error) {
                    Log.error("[GetProjectsWorker] Error while getting projects by chunks.", error);
                    applyProjectFilters();
                    updateRefreshingDate(new Date());
                    MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.refreshProjectListError(), null);
                }

                @Override
                public void chunkRetrieved(List<ProjectDTOLight> projects) {

                    if (projects != null) {
                        for (final ProjectDTOLight p : projects) {
                            try {
                                // Project id.
                                p.setProjectId(p.getId());
                                // Tree id.
                                p.setId(index--);

                                for (final ProjectDTOLight c : p.getChildrenProjects()) {
                                    // Project id.
                                    if (c != null) {

                                        c.setProjectId(c.getId());
                                        // Tree id.
                                        c.setId(index--);

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        getProjectsStore().add(projects, true);
                    }
                }

                @Override
                public void ended() {
                    applyProjectFilters();
                    updateRefreshingDate(new Date());
                }
            });

            // Runs the worker.
            getProjectsStore().removeAll();
            getProjectsStore().clearFilters();
            worker.run();
        }
    }

    public ContentPanel getProjectsPanel() {
        return projectTreePanel;
    }

    public TreeGrid<ProjectDTOLight> getProjectsTreeGrid() {
        return projectTreeGrid;
    }

    public ProjectStore getProjectsStore() {
        return (ProjectStore) projectTreeGrid.getTreeStore();
    }

    public RefreshMode getRefreshMode() {
        return refreshMode;
    }

    /**
     * Asks for a refresh of the projects list. If the refreshing mode is set to
     * {@link RefreshMode#AUTOMATIC}, the list will be refreshed immediately.
     * Otherwise, the list will be refreshed depending on the selected
     * refreshing mode.
     * 
     * @param viewOwnOrManage
     *            If the projects that the user own or manage must be included
     *            in the list (no matter of their organizational units).
     * @param orgUnitsIds
     *            The list of ids of the organizational units for which the
     *            projects will be retrieved. The projects of each the
     *            sub-organizational units are retrieved automatically.
     */
    public void refresh(boolean viewOwnOrManage, Integer... orgUnitsIds) {

        final List<Integer> orgUnitsIdsAsList = Arrays.asList(orgUnitsIds);

        this.orgUnitsIds.clear();
        this.orgUnitsIds.addAll(orgUnitsIdsAsList);

        // Builds the next refresh command.
        command = new GetProjects();
        command.setOrgUnitsIds(orgUnitsIdsAsList);
        command.setViewOwnOrManage(viewOwnOrManage);

        // If the mode is automatic, the list is refreshed immediately.
        if (refreshMode == RefreshMode.AUTOMATIC || (refreshMode == RefreshMode.BOTH && !isLoaded)) {
            refreshProjectGrid(command);
            isLoaded = true;
        }
    }
    
    /**
     * 
     * Check if the project is a favorite project of the current user.
     * 
     * @param userId
     * 
     * @param project
     * 
     * @return 
     * 
     * @author  HUZHE(zhe.hu32@gmail.com)	  
     */
    public boolean isFavoriteProject(int userId,ProjectDTOLight project)
    {
       if(project.getFavoriteUsers()==null)
    	   return false;
    	
    	for(UserDTO u : project.getFavoriteUsers())
    	{
    		if(u.getId()==userId)   		
    			return true;
    		
    	}
    	
    	return false;
    }
    
    
}
