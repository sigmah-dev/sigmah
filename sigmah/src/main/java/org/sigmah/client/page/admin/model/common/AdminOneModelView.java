package org.sigmah.client.page.admin.model.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Displays models administration screen.
 * 
 * @author nrebiai
 * 
 */
public class AdminOneModelView extends LayoutContainer implements AdminOneModelPresenter.View{
	
	private final static int BORDER = 8;

    private TabPanel tabPanelParameters;
    private LayoutContainer panelSelectedTab;
    private ContentPanel topPanel;
    private FormPanel topLeftFormPanel;
   
    private FormPanel topRightFormPanel;
    private final TextField<String> name;
    private final SimpleComboBox<String> statusList;
    
    //Project Model
    private FormPanel topCenterFormPanel;
    private final Radio ngoRadio;
    private final Radio fundingRadio;
    private final Radio partnerRadio;
    private final RadioGroup radioGroup;
    private final Grid ngoGrid;
    private final Grid fundingGrid;
    private final Grid partnerGrid;
    private ProjectModelType currentModelType = ProjectModelType.NGO;
    
    //Org unit model
    private final TextField<String> titleField;
	private final CheckBox hasBudgetCheckBox;
	private final CheckBox hasSiteCheckBox;
	private final CheckBox canContainProjectsCheckBox;	
	private final NumberField minLevelField;
	private final NumberField maxLevelField;
	
	private final Button saveButton;
	
    private ProjectModelDTO currentProjectModel;   
    private OrgUnitModelDTO currentOrgUnitModel;
    
    private Boolean isProject = true;
    
    private final UserLocalCache cache;
    private final Dispatcher dispatcher;
    
	@Inject
    public AdminOneModelView(UserLocalCache cache, Dispatcher dispatcher) { 
		this.dispatcher = dispatcher;
		this.cache = cache;
		
		final HBoxLayout hPanelLayout = new HBoxLayout();
        hPanelLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.STRETCH);
		topPanel = new ContentPanel(hPanelLayout);
		topPanel.setHeaderVisible(false);
		topPanel.setWidth(1200);
		topPanel.setBorders(true);
		
		topLeftFormPanel = new FormPanel();
		topLeftFormPanel.setWidth(400);
		topLeftFormPanel.setHeaderVisible(false);
		
		topCenterFormPanel = new FormPanel();
		topCenterFormPanel.setWidth(400);
		topCenterFormPanel.setHeaderVisible(false);
		
		topRightFormPanel = new FormPanel();
		topRightFormPanel.setWidth(400);
		topRightFormPanel.setHeaderVisible(false);
				
		name = new TextField<String>();
		name.disable();
        name.setAllowBlank(false);
        name.setFieldLabel(I18N.CONSTANTS.adminProjectModelsName());
        
        statusList = new SimpleComboBox<String>();
        statusList.disable();
        statusList.setFieldLabel(I18N.CONSTANTS.adminProjectModelsStatus());
		statusList.setAllowBlank(false);
		statusList.setTriggerAction(TriggerAction.ALL);	
		List<String> values = new ArrayList<String>();  
		for(ProjectModelStatus e : ProjectModelStatus.values()){
			if(!ProjectModelStatus.USED.equals(e))//Technical status
				values.add(ProjectModelStatus.getName(e));
		}
		statusList.add(values);
		statusList.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if(statusList.getValue()!=null && 
						!ProjectModelStatus.getName(ProjectModelStatus.DRAFT).equals(statusList.getValue().getValue())){
					if(isProject){
						MessageBox.confirm(I18N.MESSAGES.adminModelStatusChangeBox(), 
								I18N.MESSAGES.adminModelStatusChange(ProjectModelStatus.getName(currentProjectModel.getStatus()), 
										statusList.getValue().getValue()), new Listener<MessageBoxEvent>(){

							@Override
							public void handleEvent(MessageBoxEvent be) {
								if (Dialog.NO.equals(be.getButtonClicked().getItemId())) {
									statusList.setSimpleValue(ProjectModelStatus.getName(currentProjectModel.getStatus()));
	                            }
							}

						});
					}else{
						MessageBox.confirm(I18N.MESSAGES.adminModelStatusChangeBox(), 
								I18N.MESSAGES.adminModelStatusChange(ProjectModelStatus.getName(currentOrgUnitModel.getStatus()), 
										statusList.getValue().getValue()), new Listener<MessageBoxEvent>(){

							@Override
							public void handleEvent(MessageBoxEvent be) {
								if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
									statusList.setSimpleValue(ProjectModelStatus.getName(currentOrgUnitModel.getStatus()));
	                            }
							}

						});
					}
					
				}
			}
			
		});
        
       
        
        /* *******************************************ProjectModel ***********************/
        
        radioGroup = new RadioGroup("projectTypeFilter");
        

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
            	currentModelType = ProjectModelType.NGO;
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
                currentModelType = ProjectModelType.FUNDING;
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
                currentModelType = ProjectModelType.LOCAL_PARTNER;
            }
        });
        
        radioGroup.add(ngoRadio);
        radioGroup.add(fundingRadio);
        radioGroup.add(partnerRadio);
        
        ngoGrid = new Grid(1,3);
        ngoGrid.setVisible(false);
        ngoGrid.setBorderWidth(0);
        ngoGrid.setWidget(0, 0, ngoIcon);
        ngoGrid.setWidget(0, 1, ngoRadio);
        ngoGrid.setWidget(0, 2, AdminUtil.createGridText(ProjectModelType.getName(ProjectModelType.NGO)));
        topCenterFormPanel.add(ngoGrid);
        
        fundingGrid = new Grid(1,3);
        fundingGrid.setVisible(false);
        fundingGrid.setBorderWidth(0);
        fundingGrid.setWidget(0, 0, fundingIcon);
        fundingGrid.setWidget(0, 1, fundingRadio);		
        fundingGrid.setWidget(0, 2,AdminUtil.createGridText(ProjectModelType.getName(ProjectModelType.FUNDING)));
        topCenterFormPanel.add(fundingGrid);
        
        partnerGrid = new Grid(1,3);
        partnerGrid.setVisible(false);
        partnerGrid.setBorderWidth(0);
        partnerGrid.setWidget(0, 0, partnerIcon);
        partnerGrid.setWidget(0, 1, partnerRadio);
        partnerGrid.setWidget(0, 2, AdminUtil.createGridText(ProjectModelType.getName(ProjectModelType.LOCAL_PARTNER)));
        topCenterFormPanel.add(partnerGrid);
        
        /* *******************************************OrgUnitModel ***********************/
        titleField = new TextField<String>();
		titleField.setFieldLabel(I18N.CONSTANTS.adminOrgUnitsModelTitle());
		titleField.setAllowBlank(false);
		titleField.hide();		
		
		topLeftFormPanel.add(name);
		topLeftFormPanel.add(titleField);
	    topLeftFormPanel.add(statusList);
		
		hasBudgetCheckBox = new CheckBox();
		hasBudgetCheckBox.hide();
		hasBudgetCheckBox.setBoxLabel(I18N.CONSTANTS.adminOrgUnitsModelHasBudget());
		hasBudgetCheckBox.setFieldLabel("      ");
		hasBudgetCheckBox.setLabelSeparator(" ");
		hasBudgetCheckBox.setValue(false);
		topCenterFormPanel.add(hasBudgetCheckBox);
		
		hasSiteCheckBox = new CheckBox();
		hasSiteCheckBox.hide();
		hasSiteCheckBox.setBoxLabel(I18N.CONSTANTS.adminOrgUnitsModelHasSite());
		hasSiteCheckBox.setFieldLabel("      ");
		hasSiteCheckBox.setLabelSeparator(" ");
		hasSiteCheckBox.setValue(false);
		topCenterFormPanel.add(hasSiteCheckBox);
		
		canContainProjectsCheckBox = new CheckBox();
		canContainProjectsCheckBox.hide();
		canContainProjectsCheckBox.setBoxLabel(I18N.CONSTANTS.adminOrgUnitsModelContainProjects());
		canContainProjectsCheckBox.setFieldLabel("      ");
		canContainProjectsCheckBox.setLabelSeparator(" ");
		canContainProjectsCheckBox.setValue(false);
		topCenterFormPanel.add(canContainProjectsCheckBox);
		
		maxLevelField = new NumberField();
		maxLevelField.hide();
		maxLevelField.setFieldLabel(I18N.CONSTANTS.adminOrgUnitsModelMaxLevel());
		maxLevelField.setAllowBlank(true);
		topRightFormPanel.add(maxLevelField);
		
		minLevelField = new NumberField();
		minLevelField.hide();
		minLevelField.setFieldLabel(I18N.CONSTANTS.adminOrgUnitsModelMinLevel());
		minLevelField.setAllowBlank(true);
		topRightFormPanel.add(minLevelField);
        
		
        // Adds actions on filter by model type.
        for (final ProjectModelType type : ProjectModelType.values()) {
            getRadioFilter(type).addListener(Events.Change, new Listener<FieldEvent>() {

                @Override
                public void handleEvent(FieldEvent be) {
                    if (Boolean.TRUE.equals(be.getValue())) {
                        currentModelType = type;
                    }
                }
            });
        }
		
		final BorderLayoutData topLayoutData = new BorderLayoutData(LayoutRegion.NORTH, 150);
		topLayoutData.setCollapsible(true);
		topLayoutData.setMargins(new Margins(BORDER / 2, 0, BORDER / 2, 0));
        
		final BorderLayout borderLayout = new BorderLayout();
        //borderLayout.setContainerStyle("x-border-layout-ct " + STYLE_MAIN_BACKGROUND);
        setLayout(borderLayout);
        
        tabPanelParameters = new TabPanel();
        tabPanelParameters.setPlain(true);
        
        panelSelectedTab = new LayoutContainer(new BorderLayout());
        panelSelectedTab.setBorders(false);
        panelSelectedTab.addStyleName("project-current-phase-panel");
        
        final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0, 0, 4, 4));
        
        saveButton = new Button(I18N.CONSTANTS.save());
        saveButton.disable();
        saveButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				updateModel();
			}
        });
        topRightFormPanel.add(saveButton);

        topPanel.add(topLeftFormPanel, new HBoxLayoutData(0, 4, 0, 4));
		topPanel.add(topCenterFormPanel, new HBoxLayoutData(0, 4, 0, 4));
		topPanel.add(topRightFormPanel, new HBoxLayoutData(0, 4, 0, 4));
		
        add(tabPanelParameters, centerData);
        add(topPanel, topLayoutData);
	}

	
	private void updateModel(){
		
		if(isProject){
			if(name.getValue() == null || statusList.getValue() == null || currentModelType == null ){
				MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
	                    I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminStandardModel()), null);
	            return;
			}
			
			HashMap<String, Object> modelProperties = new HashMap<String, Object>();
			modelProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, currentProjectModel);
			modelProperties.put(AdminUtil.PROP_PM_NAME, name.getValue());
			
			if(statusList.getValue() != null){
				String status = statusList.getValue().getValue();
				ProjectModelStatus statusEnum = ProjectModelStatus.getStatus(status);
				modelProperties.put(AdminUtil.PROP_PM_STATUS, statusEnum);
			}			
			
			modelProperties.put(AdminUtil.PROP_PM_USE, currentModelType);
			
			dispatcher.execute(new CreateEntity("ProjectModel", modelProperties), null, new AsyncCallback<CreateResult>(){
	            public void onFailure(Throwable caught) {  
	            	MessageBox.alert(I18N.CONSTANTS.adminProjectModelUpdateBox(), 
	              			I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminProjectModelStandard()
	 								+ " '" + name.getValue() + "'"), null);
	            }

				@Override
				public void onSuccess(CreateResult result) {
					if(result != null && result.getEntity() != null){
						MessageBox.alert(I18N.CONSTANTS.adminProjectModelUpdateBox(), 
		              			I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminProjectModelStandard()
		 								+ " '" + name.getValue() + "'"), null);
					}else{
						Notification.show(I18N.CONSTANTS.adminProjectModelUpdateBox(), I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminProjectModelStandard()
								+ " '" + name.getValue()+"'"));
					}				
				}
			});
		}else{
			if(!topLeftFormPanel.isValid()){
				MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
	                    I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminStandardModel()), null);
	            return;
			}
			
			final String nameValue = name.getValue();
			final String title = titleField.getValue();
			final Boolean hasBudget = hasBudgetCheckBox.getValue();
			final Boolean hasSite = hasSiteCheckBox.getValue();		 
			final Boolean containsProjects = canContainProjectsCheckBox.getValue();
			Integer minLevel = 0;
			if(minLevelField.getValue() != null){
			  minLevel = new Integer(minLevelField.getValue().intValue());
			}
			Integer maxLevel = 0;
			if(maxLevelField.getValue() != null){
			 maxLevel = new Integer(maxLevelField.getValue().intValue());
			}
					
			HashMap<String, Object> modelProperties = new HashMap<String, Object>();
			modelProperties.put(AdminUtil.PROP_OM_NAME, name.getValue());
			if(statusList.getValue() != null){
				String status = statusList.getValue().getValue();
				ProjectModelStatus statusEnum = ProjectModelStatus.getStatus(status);
				modelProperties.put(AdminUtil.PROP_OM_STATUS, statusEnum);
			}
			modelProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, currentOrgUnitModel);
			modelProperties.put(AdminUtil.PROP_OM_NAME, nameValue);
			modelProperties.put(AdminUtil.PROP_OM_TITLE, title);
			modelProperties.put(AdminUtil.PROP_OM_HAS_BUDGET, hasBudget);
			modelProperties.put(AdminUtil.PROP_OM_HAS_SITE, hasSite);
			modelProperties.put(AdminUtil.PROP_OM_CONTAINS_PROJECTS, containsProjects);
			modelProperties.put(AdminUtil.PROP_OM_MIN_LEVEL, minLevel);
			modelProperties.put(AdminUtil.PROP_OM_MAX_LEVEL, maxLevel);
			 
	        dispatcher.execute(new CreateEntity("OrgUnitModel", modelProperties), null, new AsyncCallback<CreateResult>(){

	       	 public void onFailure(Throwable caught) {
	       		MessageBox.alert(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(), 
	          			I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminOrgUnitsModelStandard()
									+ " '" + name.getValue() + "'"), null);
	             }

				@Override
				public void onSuccess(CreateResult result) {
					if(result != null && result.getEntity() != null){								
						
						Notification.show(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminOrgUnitsModelStandard()
								+ " '" + name.getValue() +"'"));					
					}					
					else{
						MessageBox.alert(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(), 
			          			I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminOrgUnitsModelStandard()
											+ " '" + name.getValue() + "'"), null);
					}		
				}
	        });
		}
		
		
	}
	
	@Override
	public Widget getMainPanel() {
		return this;
	}

	@Override
	public TabPanel getTabPanelParameters() {
		return tabPanelParameters;
	}
	
	@Override
    public LayoutContainer getPanelSelectedTab() {
        return panelSelectedTab;
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

	@Override
	public void initModelView(Object model) {
		
		if(model instanceof ProjectModelDTO){
			ngoGrid.setVisible(true);
			fundingGrid.setVisible(true);
			partnerGrid.setVisible(true);

			fundingGrid.setVisible(true);
			partnerGrid.setVisible(true);
			if(ProjectModelStatus.DRAFT.equals(((ProjectModelDTO) model).getStatus())){
				name.enable();
				statusList.enable();
				saveButton.enable();
				ngoGrid.setVisible(true);
				fundingGrid.setVisible(true);
				partnerGrid.setVisible(true);
				radioGroup.setFireChangeEventOnSetValue(true);
			}
				
			
			isProject = true;
			
			currentProjectModel = (ProjectModelDTO) model;
			if(currentProjectModel != null){
				
				name.setValue(currentProjectModel.getName());
				
				statusList.setSimpleValue(ProjectModelStatus.getName(currentProjectModel.getStatus()));
				Log.debug("Original project model status : " + ProjectModelStatus.getName(currentProjectModel.getStatus()));
				
				currentProjectModel.getStatus();
				ProjectModelType type = currentProjectModel.getVisibility(cache.getOrganizationCache().getOrganization().getId());
				switch (type) {
		            case NGO:
		            	ngoRadio.setValue(true);
		                fundingRadio.setValue(false);
		                partnerRadio.setValue(false);
		                currentModelType = ProjectModelType.NGO;
		                break;
		            case FUNDING:
		            	ngoRadio.setValue(false);
		                fundingRadio.setValue(true);
		                partnerRadio.setValue(false);
		                currentModelType = ProjectModelType.FUNDING;
		                break;
		            case LOCAL_PARTNER:
		            	ngoRadio.setValue(false);
		                fundingRadio.setValue(false);
		                partnerRadio.setValue(true);
		                currentModelType = ProjectModelType.LOCAL_PARTNER;
		                break;
		        }
				
				
			}
		}else if(model instanceof OrgUnitModelDTO){
			isProject = false;
			
			titleField.show();
			hasBudgetCheckBox.show();
			hasSiteCheckBox.show();
			canContainProjectsCheckBox.show();	
			minLevelField.show();
			maxLevelField.show();
			
			titleField.disable();
			hasBudgetCheckBox.disable();
			hasSiteCheckBox.disable();
			canContainProjectsCheckBox.disable();	
			minLevelField.disable();
			maxLevelField.disable();
			if(ProjectModelStatus.DRAFT.equals(((OrgUnitModelDTO) model).getStatus())){
				name.enable();
				statusList.enable();
				radioGroup.setFireChangeEventOnSetValue(true);
				saveButton.enable();
				titleField.enable();
				hasBudgetCheckBox.enable();
				hasSiteCheckBox.enable();
				canContainProjectsCheckBox.enable();
				minLevelField.enable();
				maxLevelField.enable();
			}
				
			
			
			currentOrgUnitModel = (OrgUnitModelDTO) model;
			if(currentOrgUnitModel != null){
				
				name.setValue(currentOrgUnitModel.getName());				
				statusList.setSimpleValue(ProjectModelStatus.getName(currentOrgUnitModel.getStatus()));
				Log.debug("Original org unit model status : " + currentOrgUnitModel.getName() + " " + ProjectModelStatus.getName(currentOrgUnitModel.getStatus()));
			}
				
		}
		
		
	}
}
