package org.sigmah.client.page.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetProjectModels;
import org.sigmah.shared.command.GetTestProjects;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ProjectDTOLightListResult;
import org.sigmah.shared.command.result.ProjectModelListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.ProjectModelDTOLight;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;

/**
 * Manages a pop-up window to create a new project.
 * 
 * @author tmi
 * 
 */
public class CreateProjectWindow {

    /**
     * Defines the different modes for the creation.
     * 
     * @author tmi
     * 
     */
    public static enum Mode {

        /**
         * Create project mode.
         */
        SIMPLE,

        /**
         * Create a project and link it with another project as a funding
         * project.
         */
        FUNDING,

        /**
         * Create a project and link it with another project as a funded
         * project.
         */
        FUNDED,
        
        /**
         * Create a test project.
         */
        TEST;
    }

    /**
     * Listener.
     * 
     * @author tmi
     * 
     */
    public static interface CreateProjectListener {

        /**
         * Method called when a project is created in the {@link Mode#SIMPLE}
         * mode.
         * 
         * @param project
         *            The new project.
         */
        public void projectCreated(ProjectDTOLight project);

        /**
         * Method called when a project is created in the {@link Mode#FUNDING}
         * mode.
         * 
         * @param project
         *            The new project.
         * @param percentage
         *            The funding percentage.
         */
        public void projectCreatedAsFunding(ProjectDTOLight project, double percentage);

        /**
         * Method called when a project is created in the {@link Mode#FUNDED}
         * mode.
         * 
         * @param project
         *            The new project.
         * @param percentage
         *            The funding percentage.
         */
        public void projectCreatedAsFunded(ProjectDTOLight project, double percentage);
        
        /**
         * Method called when a test project is created in the {@link Mode#TEST}
         * mode.
         * 
         * @param project
         *            The new test project.
         */
        public void projectCreatedAsTest(ProjectDTOLight project);
        
		/**
		 * Method called when a test project is deleted.
		 * 
		 * @param project
		 *            The test project to delete.
		 */
        public void projectDeletedAsTest(ProjectDTOLight project);
        
    }

    private final ArrayList<CreateProjectListener> listeners;
    private final Dispatcher dispatcher;
    private final UserLocalCache cache;
    private final Window window;
    VerticalPanel mainPanel;
    private final FormPanel formPanel;
    private final TextField<String> nameField;
    private final TextField<String> fullNameField;
    private final ComboBox<ProjectModelDTOLight> modelsField;
    private final LabelField modelType;
    private final ListStore<ProjectModelDTOLight> modelsStore;
    private final NumberField budgetField;
    private final NumberField amountField;
    private final LabelField percentageField;
    private ProjectDTOLight currentFunding;
    private Mode currentMode;
    private final ListStore<OrgUnitDTOLight> orgUnitsStore;
    private final ComboBox<OrgUnitDTOLight> orgUnitsField;
    private final FormPanel projectPanel;
    private com.extjs.gxt.ui.client.widget.grid.Grid<ProjectDTOLight> testProjectGrid;
    private ListStore<ProjectDTOLight> testProjectStore;
    private final FormPanel testProjectPanel;

    /**
     * Counter to wait that required data are loaded before showing the window.
     */
    private int countBeforeShow;

    /**
     * Flag to display only one alert message.
     */
    private boolean alert = false;

    public CreateProjectWindow(final Dispatcher dispatcher, final Authentication authentication,
            final UserLocalCache cache) {

        listeners = new ArrayList<CreateProjectListener>();

        this.dispatcher = dispatcher;
        this.cache = cache;

        // Name field.
        nameField = new TextField<String>();
        nameField.setMaxLength(16);
        nameField.setFieldLabel(I18N.CONSTANTS.projectName());
        nameField.setAllowBlank(false);

        // Full name field.
        fullNameField = new TextField<String>();
        fullNameField.setMaxLength(50);
        fullNameField.setFieldLabel(I18N.CONSTANTS.projectFullName());
        fullNameField.setAllowBlank(false);

        // Budget field.
        budgetField = new NumberField();
        budgetField.setFieldLabel(I18N.CONSTANTS.projectPlannedBudget() + " (" + I18N.CONSTANTS.currencyEuro() + ')');
        budgetField.setValue(0);
        budgetField.setAllowBlank(false);

        // Models list.
        modelsField = new ComboBox<ProjectModelDTOLight>();
        modelsField.setFieldLabel(I18N.CONSTANTS.projectModel());
        modelsField.setAllowBlank(false);
        modelsField.setValueField("id");
        modelsField.setDisplayField("name");
        modelsField.setEditable(true);
        modelsField.setEmptyText(I18N.CONSTANTS.projectModelEmptyChoice());
        modelsField.setTriggerAction(TriggerAction.ALL);

        // Model type
        modelType = new LabelField();
        modelType.setFieldLabel(I18N.CONSTANTS.createProjectType());

        modelsField.addListener(Events.Select, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {

                final ProjectModelType type = modelsField.getSelection().get(0)
                        .getVisibility(authentication.getOrganizationId());

                final Grid iconGrid = new Grid(1, 2);
                iconGrid.setCellPadding(0);
                iconGrid.setCellSpacing(0);

                iconGrid.setWidget(0, 0, FundingIconProvider.getProjectTypeIcon(type, IconSize.MEDIUM).createImage());
                DOM.setStyleAttribute(iconGrid.getCellFormatter().getElement(0, 0), "paddingTop", "2px");
                iconGrid.setText(0, 1, ProjectModelType.getName(type));
                DOM.setStyleAttribute(iconGrid.getCellFormatter().getElement(0, 1), "paddingLeft", "5px");

                modelType.setText(iconGrid.getElement().getString());
            }
        });

        // Models list store.
        modelsStore = new ListStore<ProjectModelDTOLight>();
        modelsStore.addListener(Events.Add, new Listener<StoreEvent<ProjectModelDTO>>() {

            @Override
            public void handleEvent(StoreEvent<ProjectModelDTO> be) {
                modelsField.setEnabled(true);
            }
        });

        modelsStore.addListener(Events.Clear, new Listener<StoreEvent<ProjectModelDTO>>() {

            @Override
            public void handleEvent(StoreEvent<ProjectModelDTO> be) {
                modelsField.setEnabled(false);
            }
        });
        modelsField.setStore(modelsStore);

        // Amount for funding.
        amountField = new NumberField();
        amountField.addListener(Events.Change, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {

                // Checks that values are filled.
                if (amountField.getValue() == null || amountField.getValue().doubleValue() < 0) {
                    amountField.setValue(0);
                }

                if (budgetField.getValue() == null || budgetField.getValue().doubleValue() < 0) {
                    budgetField.setValue(0);
                    amountField.setValue(0);
                }

                // Computes the ratio between the allocated amount and the
                // funding project.
                if (currentFunding == null) {
                    percentageField.setText(I18N.CONSTANTS.createProjectPercentageNotAvailable());
                } else {

                    switch (currentMode) {
                    case FUNDED: {

                        final double fundingBudget = currentFunding.getPlannedBudget();
                        final double budget = budgetField.getValue().doubleValue();

                        // Checks the budget bounds and adjusts the allocated
                        // amount.
                        double min;
                        if (budget <= 0) {
                            amountField.setValue(0);
                            percentageField.setText("0.0 %");
                            break;
                        } else if ((min = Math.min(fundingBudget, budget)) < amountField.getValue().doubleValue()) {
                            amountField.setValue(min);
                        }

                        percentageField.setText(NumberUtils.ratioAsString(amountField.getValue().doubleValue(), budget));
                    }
                        break;
                    case FUNDING: {

                        final double fundingBudget = currentFunding.getPlannedBudget();
                        final double budget = budgetField.getValue().doubleValue();

                        if (fundingBudget <= 0) {
                            percentageField.setText("0.0 %");
                            return;
                        }
                        
                        double min;
                        if (budget <= 0) {
                            amountField.setValue(0);
                            percentageField.setText("0.0 %");
                            break;
                        } else if ((min = Math.min(fundingBudget, budget)) < amountField.getValue().doubleValue()) {
                            amountField.setValue(min);
                        }

                        percentageField.setText(NumberUtils.ratioAsString(amountField.getValue().doubleValue(),
                                fundingBudget));
                    }
                        break;
                        
                    case TEST :
                    	budgetField.setValue(0);
                    	break;
                    default:
                        percentageField.setText(I18N.CONSTANTS.createProjectPercentageNotAvailable());
                        break;
                    }
                }
            }
        });

        // Percentage for funding.
        percentageField = new LabelField();
        percentageField.setFieldLabel(I18N.CONSTANTS.createProjectPercentage());

        // Org units list.
        orgUnitsField = new ComboBox<OrgUnitDTOLight>();
        orgUnitsField.setFieldLabel(I18N.CONSTANTS.orgunit());
        orgUnitsField.setAllowBlank(false);
        orgUnitsField.setValueField("id");
        orgUnitsField.setDisplayField("completeName");
        orgUnitsField.setEditable(true);
        orgUnitsField.setEmptyText(I18N.CONSTANTS.orgunitEmptyChoice());
        orgUnitsField.setTriggerAction(TriggerAction.ALL);

        // Org units list store.
        orgUnitsStore = new ListStore<OrgUnitDTOLight>();
        orgUnitsStore.addListener(Events.Add, new Listener<StoreEvent<OrgUnitDTOLight>>() {

            @Override
            public void handleEvent(StoreEvent<OrgUnitDTOLight> be) {
                orgUnitsField.setEnabled(true);
            }
        });

        orgUnitsStore.addListener(Events.Clear, new Listener<StoreEvent<OrgUnitDTOLight>>() {

            @Override
            public void handleEvent(StoreEvent<OrgUnitDTOLight> be) {
                orgUnitsField.setEnabled(false);
            }
        });

        orgUnitsField.setStore(orgUnitsStore);

        // Create button.
        final Button createButton = new Button(I18N.CONSTANTS.createProjectCreateButton());
        createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                createProject();
            }
        });
        
        // Form panel.
        formPanel = new FormPanel();
        formPanel.setBodyBorder(false);
        formPanel.setHeaderVisible(false);
        formPanel.setPadding(5);
        formPanel.setLabelWidth(170);
        formPanel.setFieldWidth(350);

        formPanel.add(nameField);
        formPanel.add(fullNameField);
        formPanel.add(budgetField);
        formPanel.add(orgUnitsField);
        formPanel.add(modelsField);
        formPanel.add(modelType);
        formPanel.add(amountField);
        formPanel.add(percentageField);
        formPanel.addButton(createButton);
        
        testProjectPanel = new FormPanel();
        testProjectPanel.setPadding(0);
        testProjectPanel.setHeading(I18N.CONSTANTS.createTestProjectListe());
        testProjectPanel.setHeaderVisible(true);
        testProjectStore = new ListStore<ProjectDTOLight>();   
        testProjectGrid = buildTestProjectPanel();
        testProjectPanel.add(testProjectGrid);
        testProjectPanel.setVisible(false);
        
        projectPanel = new FormPanel();
        projectPanel.setBodyBorder(false);
        projectPanel.setHeaderVisible(false);
        projectPanel.setPadding(5);
        projectPanel.add(formPanel);
        projectPanel.add(testProjectPanel);
        
        // Main window panel.
        mainPanel = new VerticalPanel();
        mainPanel.setLayout(new FitLayout()); 
        mainPanel.add(projectPanel);
        mainPanel.setAutoHeight(true);

        // Window.
        window = new Window();
        window.setWidth(560);
        window.setAutoHeight(true);
       
        window.setPlain(true);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setLayout(new FitLayout());
        window.add(mainPanel);
    }

    
    /**
     * Builds the test projects panel.
     * 
     * @return The panel.
     */
    private com.extjs.gxt.ui.client.widget.grid.Grid<ProjectDTOLight>  buildTestProjectPanel() {
    	ColumnModel cm = buildColumnModelTestProject(); 
    	testProjectStore.setMonitorChanges(true);
    	com.extjs.gxt.ui.client.widget.grid.Grid<ProjectDTOLight> grid = new com.extjs.gxt.ui.client.widget.grid.Grid<ProjectDTOLight>(testProjectStore, cm);  
    	grid.setAutoExpandColumn("fullName");
    	grid.setHeight(200);
        grid.getView().setForceFit(true);
    	return grid;
    }
    
    /**
     * Build the columns for the test project grid.
     * @return the columns for the test project grid.
     */
    private ColumnModel buildColumnModelTestProject(){
    	List<ColumnConfig> columns = new ArrayList<ColumnConfig>();   	
    	
    	//set name column
    	ColumnConfig column = new ColumnConfig("name",I18N.CONSTANTS.projectName(),50);  
    	column.setDataIndex("name");
    	column.setWidth(135);
    	column.setAlignment(HorizontalAlignment.RIGHT);
    	columns.add(column);  
   
    	//set full name column
	    column = new ColumnConfig("fullName", I18N.CONSTANTS.projectFullName(), 100);  
	    column.setDataIndex("fullName");
	    column.setWidth(320);
	    columns.add(column); 
	    
		// set delete button for each row
		column = new ColumnConfig();
		column.setWidth(30);
		column.setId("id");
		column.setDataIndex("id");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<ProjectDTOLight>() {
			public Object render(
					ProjectDTOLight model,
					String property,
					ColumnData config,
					int rowIndex,
					int colIndex,
					ListStore<ProjectDTOLight> store,
					com.extjs.gxt.ui.client.widget.grid.Grid<ProjectDTOLight> grid) {
				Button deleteBouton = new Button("", IconImageBundle.ICONS
						.delete());
				final ProjectDTOLight selectedTestptoject = model;
				deleteBouton.setData("testProjectId", model.getId());
				deleteBouton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {
							@Override
							public void handleEvent(ButtonEvent be) {
								// Request confirm test project delete
								MessageBox.confirm(I18N.CONSTANTS
										.deleteTestProjectHeader(), I18N.CONSTANTS
										.deleteTestProjectConfirm(),
										new Listener<MessageBoxEvent>() {
											@Override
											public void handleEvent(
													MessageBoxEvent be) {
												if (Dialog.YES.equals(be
														.getButtonClicked()
														.getItemId())) {
													deleteTestProject(selectedTestptoject);
												}
											}
										});
							}
						});
				return deleteBouton;
			}
		});
		columns.add(column);
		return new ColumnModel(columns);
    }
    

	/**
	 * Delete a test project from the user's test projects list.    		     
	 * @param testProject the test project to delete.
	 */
    private void deleteTestProject(final ProjectDTOLight testProject){
    
    	Delete cmd = new Delete("Project", testProject.getId());
    	cmd.setMode(Mode.TEST);
    	dispatcher.execute(cmd, null, new AsyncCallback<VoidResult>() {

            @Override
            public void onFailure(Throwable arg0) {
                MessageBox.alert(I18N.CONSTANTS.createProjectFailed(), I18N.CONSTANTS.deleteTestProject(),
                        null);
            }

            @Override
            public void onSuccess(VoidResult result) {
                if (Log.isDebugEnabled()) {
                    Log.debug("Test project deleted.");
                }
                testProjectStore.remove(testProject);
                testProjectStore.commitChanges();
                fireProjectDeletedAsTest(testProject);           
            }
        });  
    }
  
    /**
     * Creates a project for the given fields.
     */
    private void createProject() {

        // Checks the form completion.
        if (!formPanel.isValid()) {
            MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
                    I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.project()), null);
            return;
        }

        // Gets values.
        final String name = nameField.getValue();
        final String fullName = fullNameField.getValue();
        Double budget = 0.0;
        if( !Mode.TEST.equals(currentMode)){
        	budget = budgetField.getValue().doubleValue();
        }
        
        final long modelId = modelsField.getValue().getId();
        // final int countryId = countriesField.getValue().getId();
       
        String orgUnitId = null;
        //No organizational unit for a test project
        if(orgUnitsField.getValue()!=null){
        	orgUnitId =String.valueOf(orgUnitsField.getValue().getId());
        }
         

        if (Log.isDebugEnabled()) {

            final StringBuilder sb = new StringBuilder();
            sb.append("Create a new project with parameters: ");
            sb.append("name=");
            sb.append(name);
            sb.append(" ; full name=");
            sb.append(fullName);
            sb.append(" ; budget=");
            sb.append(budget);
            sb.append(" ; model id=");
            sb.append(modelId);
            sb.append(" ; org unit id=");
            sb.append(orgUnitId);
            
            Log.debug(sb.toString());
        }

        // Stores the project properties in a map to be send to the server.
        final HashMap<String, Object> projectProperties = new HashMap<String, Object>();
        projectProperties.put("name", name);
        projectProperties.put("fullName", fullName);
        projectProperties.put("budget", budget);
        projectProperties.put("modelId", modelId);
        projectProperties.put("orgUnitId", orgUnitId);
        projectProperties.put("calendarName", I18N.CONSTANTS.calendarDefaultName());

        // Creates the project.
        dispatcher.execute(new CreateEntity("Project", projectProperties), null, new AsyncCallback<CreateResult>() {

            @Override
            public void onFailure(Throwable arg0) {
                MessageBox.alert(I18N.CONSTANTS.createProjectFailed(), I18N.CONSTANTS.createProjectFailedDetails(),
                        null);
            }

            @Override
            public void onSuccess(CreateResult result) {

                if (Log.isDebugEnabled()) {
                    Log.debug("Project created with id #" + result.getNewId() + ".");
                }

                // Manages the display mode.
                switch (currentMode) {
                case FUNDING:
                    fireProjectCreatedAsFunding((ProjectDTOLight) result.getEntity(), amountField.getValue()
                            .doubleValue());
                    break;
                case FUNDED:
                    fireProjectCreatedAsFunded((ProjectDTOLight) result.getEntity(), amountField.getValue()
                            .doubleValue());
                    break;
                case TEST:{
                	fireProjectCreatedAsTest((ProjectDTOLight) result.getEntity());
                	testProjectStore.add(((ProjectDTOLight)result.getEntity()));
                	formPanel.clear();
                	break;
                }
                default:
                    fireProjectCreated((ProjectDTOLight) result.getEntity());
                    break;
                }
            }
        });
        if(!Mode.TEST.equals(currentMode)){
        	   window.hide();
        }     
    }

    /**
     * Informs the user that some required data cannot be recovered. The project
     * cannot be created.
     * 
     * @param msg
     *            The alert message.
     */
    private void missingRequiredData(String msg) {

        if (alert) {
            return;
        }

        alert = true;

        MessageBox.alert(I18N.CONSTANTS.createProjectDisable(), msg, null);

        window.hide();
    }

    /**
     * Initializes and show the window.
     */
    public void show() {
        show(Mode.SIMPLE, null);
    }

    /**
     * Initializes and show the window.
     */
    public void showProjectTest() {
        show(Mode.TEST, null);
    }
    
    /**
     * Initializes and show the window.
     * 
     * @param mode
     *            The display mode.
     * @param funding
     *            The current project which is linked to the created project.
     */
    public void show(Mode mode, ProjectDTOLight funding) {

        currentMode = mode;
        currentFunding = funding;

        // Resets window state.
        nameField.reset();
        fullNameField.reset();
        modelsField.reset();
        // countriesField.reset();
        orgUnitsField.reset();
        modelType.setValue("");
        amountField.setValue(0);
        budgetField.setValue(0);
        percentageField.setText("0 %");
        alert = false;

        // Manages the display mode.
        switch (currentMode) {
        case FUNDING:
            amountField.setVisible(true);
            amountField.setValue(0);
            amountField.setAllowBlank(false);
            amountField.setFieldLabel(I18N.CONSTANTS.projectFundedByDetails() + " (" + I18N.CONSTANTS.currencyEuro()
                    + ')');
            percentageField.setVisible(true);
            orgUnitsField.setAllowBlank(false);
            orgUnitsField.setVisible(true);
            testProjectPanel.setVisible(false);
            testProjectGrid.setVisible(false);
            break;
        case FUNDED:
            amountField.setVisible(true);
            amountField.setValue(0);
            amountField.setAllowBlank(false);
            amountField.setFieldLabel(I18N.CONSTANTS.projectFinancesDetails() + " (" + I18N.CONSTANTS.currencyEuro()
                    + ')');
            percentageField.setVisible(true);
            orgUnitsField.setAllowBlank(false);
            orgUnitsField.setVisible(true);
            testProjectPanel.setVisible(false);
            testProjectGrid.setVisible(false);
            break;
        case TEST :
        	 amountField.setVisible(false);
             amountField.setValue(0);
             amountField.setAllowBlank(true);
             percentageField.setVisible(false);
             orgUnitsField.setAllowBlank(true);
             orgUnitsField.setVisible(false);
             budgetField.setVisible(false);
             budgetField.setAllowBlank(true);
             testProjectPanel.setVisible(true);
             testProjectGrid.setVisible(true);
             window.setHeading(I18N.CONSTANTS.createProjectTest());
//             window.setSize(550, 450);
        	break;
        default:
            amountField.setVisible(false);
            amountField.setAllowBlank(true);
            percentageField.setVisible(false);
            orgUnitsField.setAllowBlank(false);
            orgUnitsField.setVisible(true);
            testProjectPanel.setVisible(false);
            testProjectGrid.setVisible(false);
            break;
        }
        
        // There are three remote calls
        countBeforeShow = 3;
        
//        // There are three remote calls for the TEST MODE
//        if(Mode.TEST.equals(currentMode)){
//        	  countBeforeShow = 3;
//        }

        if (orgUnitsStore.getCount() == 0) {

            cache.getOrganizationCache().get(new AsyncCallback<OrgUnitDTOLight>() {

                @Override
                public void onSuccess(OrgUnitDTOLight result) {
                    fillOrgUnitsList(result);

                    if (orgUnitsStore.getCount() == 0) {
                        Log.error("[show] No available org unit.");
                        missingRequiredData(I18N.CONSTANTS.createProjectDisableOrgUnit());
                        return;
                    }

                    countBeforeShow();
                }

                @Override
                public void onFailure(Throwable caught) {
                    Log.error("[show] Error while getting the org units.", caught);
                    missingRequiredData(I18N.CONSTANTS.createProjectDisableOrgUnitError());
                }
            });
        } else {
            countBeforeShow();
        }

        if (modelsStore.getCount() == 0) {

            // Retrieves project models (with an optional filter on the type).
        	GetProjectModels cmdGetProjectModels = new GetProjectModels();
        	
        	if(Mode.TEST.equals(currentMode)){
        		//Retrieves the test projectModel
        		cmdGetProjectModels.setProjectModelStatus(ProjectModelStatus.DRAFT);
        	}
        	
            dispatcher.execute(cmdGetProjectModels, null, new AsyncCallback<ProjectModelListResult>() {

                @Override
                public void onFailure(Throwable caught) {
                    missingRequiredData(I18N.CONSTANTS.createProjectDisableModelError());
                }

                @Override
                public void onSuccess(ProjectModelListResult result) {

                    if (result.getList() == null || result.getList().isEmpty()) {
                        Log.error("[missingRequiredData] No available project model.");
                        missingRequiredData(I18N.CONSTANTS.createProjectDisableModel());
                        return;
                    }
                    for(ProjectModelDTOLight projectModelLight : result.getList()){
                    	if(!Mode.TEST.equals(currentMode)){
                    		/*if(!ProjectModelStatus.DRAFT.equals(projectModelLight.getStatus())
                        			&& !ProjectModelStatus.UNAVAILABLE.equals(projectModelLight.getStatus())){*/
                        		 modelsStore.add(projectModelLight);
                        	//}
                    	}else{
                    		/*TODO enable if(!ProjectModelStatus.DRAFT.equals(projectModelLight.getStatus())
                			&& !ProjectModelStatus.UNAVAILABLE.equals(projectModelLight.getStatus())){*/
                    		modelsStore.add(projectModelLight);
                    		//}
                    	}
                    	
                    }                   
                    modelsStore.commitChanges();

                    countBeforeShow();
                }
            });         
            
        } else {
            countBeforeShow();
        }
        
        if(testProjectStore.getCount()==0){
        	GetTestProjects cmdgetGetTestProjects = new GetTestProjects(ProjectModelStatus.DRAFT);
        	dispatcher.execute(cmdgetGetTestProjects, null, new AsyncCallback<ProjectDTOLightListResult>() {

                @Override
                public void onFailure(Throwable caught) {
                    missingRequiredData(I18N.CONSTANTS.createProjectDisableModelError());
                }

                @Override
                public void onSuccess(ProjectDTOLightListResult result) {

//                    if (result.getList() == null || result.getList().isEmpty()) {
//                        Log.error("[missingRequiredData] No available project model.");
//                        missingRequiredData(I18N.CONSTANTS.createProjectDisableModel());
//                        return;
//                    }
   
                    testProjectStore.add(result.getList());                        
                    countBeforeShow();
                }
            });
        	
        }else{
        	 countBeforeShow();
        }
    }

    /**
     * Decrements the local counter before showing the window.
     */
    private void countBeforeShow() {

        countBeforeShow--;

        if (countBeforeShow == 0) {
            window.show();
        }
    }

    /**
     * Fills combobox with the children of the given root org units.
     * 
     * @param root
     *            The root org unit.
     */
    private void fillOrgUnitsList(OrgUnitDTOLight root) {

        for (final OrgUnitDTOLight child : root.getChildrenDTO()) {
            recursiveFillOrgUnitsList(child);
        }
    }

    /**
     * Fills recursively the combobox from the given root org unit.
     * 
     * @param root
     *            The root org unit.
     */
    private void recursiveFillOrgUnitsList(OrgUnitDTOLight root) {

        if (root.getCanContainProjects()) {
            orgUnitsStore.add(root);
        }

        for (final OrgUnitDTOLight child : root.getChildrenDTO()) {
            recursiveFillOrgUnitsList(child);
        }
    }

    public void addListener(CreateProjectListener l) {
        listeners.add(l);
    }

    public void removeListener(CreateProjectListener l) {
        listeners.remove(l);
    }

    /**
     * Method called when a project is created.
     * 
     * @param project
     *            The new project.
     */
    public void fireProjectCreated(ProjectDTOLight project) {
        for (final CreateProjectListener l : listeners) {
            l.projectCreated(project);
        }
    }

    /**
     * Method called when a project is created.
     * 
     * @param project
     *            The new project.
     * @param percentage
     *            The funding percentage.
     */
    public void fireProjectCreatedAsFunding(ProjectDTOLight project, double percentage) {
        for (final CreateProjectListener l : listeners) {
            l.projectCreatedAsFunding(project, percentage);
        }
    }

    /**
     * Method called when a project is created.
     * 
     * @param project
     *            The new project.
     * @param percentage
     *            The funding percentage.
     */
    public void fireProjectCreatedAsFunded(ProjectDTOLight project, double percentage) {
        for (final CreateProjectListener l : listeners) {
            l.projectCreatedAsFunded(project, percentage);
        }
    }
    
	/**
	 * Method called when a test project is created.
	 * 
	 * @param project
	 *            The new test project.
	 * 
	 */
    public void fireProjectCreatedAsTest(ProjectDTOLight project) {
        for (final CreateProjectListener l : listeners) {
            l.projectCreatedAsTest(project);
        }
    }
    
    
    /**
	 * Method called when a test project is deleted.
	 * 
	 * @param project
	 *            The deleted project.
	 * 
	 */
    public void fireProjectDeletedAsTest(ProjectDTOLight project) {
        for (final CreateProjectListener l : listeners) {
            l.projectDeletedAsTest(project);
        }
    }
}
