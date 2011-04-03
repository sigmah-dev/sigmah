package org.sigmah.client.page.admin.model.project.logframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.project.logframe.AdminLogFramePresenter.View;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminLogFrameView extends View {	

	private static final int LIMIT = 20;

	private final Dispatcher dispatcher;
	
	private final TextField<String> name;
	private final ContentPanel logFrameMainPanel;
	private final FormPanel mainPanel;
	private final ContentPanel objectivesPanel;
	private final FormPanel objectivesFirstPanel;
	private final SimpleComboBox<String> objectives_max;
	private final SimpleComboBox<String> objectives_max_per_group;
	private final FormPanel objectivesSecondPanel;
	private final SimpleComboBox<Boolean> objectives_enable_groups;
	private final FormPanel objectivesThirdPanel;
	private final SimpleComboBox<String> objectives_max_groups;
	private final ContentPanel activitiesPanel;
	private final FormPanel activitiesFirstPanel;
	private final SimpleComboBox<String> activities_max;
	private final SimpleComboBox<Boolean> activities_enable_groups;
	private final FormPanel activitiesSecondPanel;
	private final SimpleComboBox<String> activities_max_per_result;
	private final SimpleComboBox<String> activities_max_groups;
	private final FormPanel activitiesThirdPanel;
	private final SimpleComboBox<String> activities_max_per_group;
	private final ContentPanel resultsPanel;
	private final FormPanel resultsFirstPanel;
	private final SimpleComboBox<String> results_max;
	private final SimpleComboBox<Boolean> results_enable_groups;
	private final FormPanel resultsSecondPanel;
	private final SimpleComboBox<String> results_max_per_obj;
	private final SimpleComboBox<String> results_max_groups;
	private final FormPanel resultsThirdPanel;
	private final SimpleComboBox<String> results_max_per_group;
	private final ContentPanel prerequisitesPanel;
	private final FormPanel prerequisitesFirstPanel;
	private final SimpleComboBox<String> prerequisites_max;
	private final SimpleComboBox<Boolean> prerequisites_enable_groups;
	private final FormPanel prerequisitesSecondPanel;
	private final SimpleComboBox<String> prerequisites_max_groups;
	private final SimpleComboBox<String> prerequisites_max_per_group;
	private final FormPanel prerequisitesThirdPanel;

	public AdminLogFrameView(Dispatcher dispatcher, ProjectModelDTO model){	
		this.dispatcher = dispatcher;
		this.projectModel = model;
		
		
		
		// Main panel
        final VBoxLayout mainPanelLayout = new VBoxLayout();
        mainPanelLayout.setVBoxLayoutAlign(VBoxLayout.VBoxLayoutAlign.STRETCH);
        logFrameMainPanel = new ContentPanel(mainPanelLayout);
        logFrameMainPanel.setHeaderVisible(false);
        logFrameMainPanel.setBorders(false);
        logFrameMainPanel.setBodyBorder(false);		
        logFrameMainPanel.setWidth(1300);
        logFrameMainPanel.setHeight(900);
        
        
        List<String> values = new ArrayList<String>();
        values.add(I18N.CONSTANTS.adminLogFrameUnlimited());
        for(int i=1; i<LIMIT; i++){
        	values.add(""+i);
        }
        
        mainPanel = new FormPanel();
        mainPanel.setHeaderVisible(false);
        mainPanel.setBorders(true);
        mainPanel.setWidth(1200);
        mainPanel.setHeight(50);
        
        name = new TextField<String>();
        name.setAllowBlank(false);
        name.setFieldLabel(I18N.CONSTANTS.adminLogFrameName());
        
        mainPanel.add(name);
        
		//Objectives Panel	
        final HBoxLayout oPanelLayout = new HBoxLayout();
        oPanelLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.STRETCH);
		objectivesPanel = new ContentPanel(oPanelLayout);
		objectivesPanel.setHeading(I18N.CONSTANTS.adminLogFrameObjectives());	
		objectivesPanel.setBorders(true);
		objectivesPanel.setWidth(1200);
		objectivesPanel.setHeight(150);
		
		objectives_max = new SimpleComboBox<String>();
		objectives_max.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxOS());
		objectives_max.add(values);
		objectives_max.setTriggerAction(TriggerAction.ALL);
		
		
		objectives_max_per_group = new SimpleComboBox<String>();
		objectives_max_per_group.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxAPerGroup());
		objectives_max_per_group.add(values);
		objectives_max_per_group.setTriggerAction(TriggerAction.ALL);
		
		objectivesFirstPanel = new FormPanel();
		objectivesFirstPanel.setHeaderVisible(false);
		objectivesFirstPanel.setWidth(400);
		objectivesFirstPanel.setHeight(150);
		
		objectivesFirstPanel.add(objectives_max);
		objectivesFirstPanel.add(objectives_max_per_group);

        objectivesPanel.add(objectivesFirstPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        objectives_enable_groups = new SimpleComboBox<Boolean>();
        objectives_enable_groups.setFieldLabel(I18N.CONSTANTS.adminLogFrameEnableOSGroups());
        objectives_enable_groups.add(true);
        objectives_enable_groups.add(false);
        objectives_enable_groups.setLabelSeparator("");
        objectives_enable_groups.setTriggerAction(TriggerAction.ALL);
        
        objectivesSecondPanel = new FormPanel();
        objectivesSecondPanel.setHeaderVisible(false);
        objectivesSecondPanel.setWidth(400);
        objectivesSecondPanel.setHeight(150);
        
        objectivesSecondPanel.add(objectives_enable_groups);

        objectivesPanel.add(objectivesSecondPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        objectives_max_groups = new SimpleComboBox<String>();
        objectives_max_groups.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxOSGroups());
		objectives_max_groups.add(values);
		objectives_max_groups.setTriggerAction(TriggerAction.ALL);
		
		
        objectivesThirdPanel = new FormPanel();
        objectivesThirdPanel.setHeaderVisible(false);
        objectivesThirdPanel.setWidth(400);
        objectivesThirdPanel.setHeight(150);
        
        objectivesThirdPanel.add(objectives_max_groups);
        
        objectivesPanel.add(objectivesThirdPanel, new HBoxLayoutData(0, 4, 0, 0));	
        
        //Activities Panel		
        final HBoxLayout aPanelLayout = new HBoxLayout();
        aPanelLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.STRETCH);
		activitiesPanel = new ContentPanel(aPanelLayout);
		activitiesPanel.setHeading(I18N.CONSTANTS.adminLogFrameActivities());	
		activitiesPanel.setBorders(true);
		activitiesPanel.setWidth(1200);
		activitiesPanel.setHeight(150);
		
		activities_max = new SimpleComboBox<String>();
		activities_max.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxA());
		activities_max.add(values);
		activities_max.setTriggerAction(TriggerAction.ALL);
		
		
		activities_enable_groups = new SimpleComboBox<Boolean>();
		activities_enable_groups.setFieldLabel(I18N.CONSTANTS.adminLogFrameEnableAGroups());
		activities_enable_groups.add(true);
		activities_enable_groups.add(false);
		activities_enable_groups.setTriggerAction(TriggerAction.ALL);
		activities_enable_groups.setLabelSeparator("");
		
		activitiesFirstPanel = new FormPanel();
		activitiesFirstPanel.setHeaderVisible(false);
		activitiesFirstPanel.setWidth(400);
		activitiesFirstPanel.setHeight(150);
		
		activitiesFirstPanel.add(activities_max);
		activitiesFirstPanel.add(activities_enable_groups);
		
        activitiesPanel.add(activitiesFirstPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        activities_max_groups = new SimpleComboBox<String>();
        activities_max_groups.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxAGroups());
		activities_max_groups.add(values);
		activities_max_groups.setTriggerAction(TriggerAction.ALL);
		
		
		activities_max_per_group = new SimpleComboBox<String>();
		activities_max_per_group.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxAPerGroup());
		activities_max_per_group.add(values);
		activities_max_per_group.setTriggerAction(TriggerAction.ALL);
		
        
        activitiesSecondPanel = new FormPanel();
        activitiesSecondPanel.setHeaderVisible(false);
        activitiesSecondPanel.setWidth(400);
        activitiesSecondPanel.setHeight(150);
        
        activitiesSecondPanel.add(activities_max_groups);
        activitiesSecondPanel.add(activities_max_per_group);
		
        activitiesPanel.add(activitiesSecondPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        activities_max_per_result = new SimpleComboBox<String>();
        activities_max_per_result.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxAPerRA());
		activities_max_per_result.add(values);
		activities_max_per_result.setTriggerAction(TriggerAction.ALL);
		
        
        activitiesThirdPanel = new FormPanel();
        activitiesThirdPanel.setHeaderVisible(false);
        activitiesThirdPanel.setWidth(400);
        activitiesThirdPanel.setHeight(150);
        
        activitiesThirdPanel.add(activities_max_per_result);
        
        activitiesPanel.add(activitiesThirdPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        //Results Panel		
        final HBoxLayout rPanelLayout = new HBoxLayout();
        rPanelLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.STRETCH);
		resultsPanel = new ContentPanel(rPanelLayout);
		resultsPanel.setHeading(I18N.CONSTANTS.adminLogFrameResults());	
		resultsPanel.setBorders(true);
		resultsPanel.setWidth(1200);
		resultsPanel.setHeight(150);
		
		results_max = new SimpleComboBox<String>();
		results_max.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxRA());
		results_max.add(values);
		results_max.setTriggerAction(TriggerAction.ALL);
		
		
		results_enable_groups = new SimpleComboBox<Boolean>();
		results_enable_groups.setFieldLabel(I18N.CONSTANTS.adminLogFrameEnableRAGroups());
		results_enable_groups.add(true);
		results_enable_groups.add(false);
		results_enable_groups.setTriggerAction(TriggerAction.ALL);
		results_enable_groups.setLabelSeparator("");
		
		
		resultsFirstPanel = new FormPanel();
		resultsFirstPanel.setHeaderVisible(false);
		resultsFirstPanel.setWidth(400);
		resultsFirstPanel.setHeight(150);
		
		resultsFirstPanel.add(results_max);
		resultsFirstPanel.add(results_enable_groups);
		
        resultsPanel.add(resultsFirstPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        results_max_groups = new SimpleComboBox<String>();
        results_max_groups.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxRA());
		results_max_groups.add(values);
		results_max_groups.setTriggerAction(TriggerAction.ALL);
		
		
		results_max_per_group = new SimpleComboBox<String>();
		results_max_per_group.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxRAPerGroup());
		results_max_per_group.add(values);
		results_max_per_group.setTriggerAction(TriggerAction.ALL);
		
        
        resultsSecondPanel = new FormPanel();
        resultsSecondPanel.setHeaderVisible(false);
        resultsSecondPanel.setWidth(400);
        resultsSecondPanel.setHeight(150);
        
        resultsSecondPanel.add(results_max_groups);
        resultsSecondPanel.add(results_max_per_group);
		
        resultsPanel.add(resultsSecondPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        results_max_per_obj = new SimpleComboBox<String>();
        results_max_per_obj.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxRAPerOS());
		results_max_per_obj.add(values);
		results_max_per_obj.setTriggerAction(TriggerAction.ALL);
		
        
        resultsThirdPanel = new FormPanel();
        resultsThirdPanel.setHeaderVisible(false);
        resultsThirdPanel.setWidth(400);
        resultsThirdPanel.setHeight(150);
        
        resultsThirdPanel.add(results_max_per_obj);
        
        resultsPanel.add(resultsThirdPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        //Prerequisites Panel		
        final HBoxLayout pPanelLayout = new HBoxLayout();
        pPanelLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.STRETCH);
		prerequisitesPanel = new ContentPanel(pPanelLayout);
		prerequisitesPanel.setHeading(I18N.CONSTANTS.adminLogFramePrerequisites());	
		prerequisitesPanel.setBorders(true);
		prerequisitesPanel.setWidth(1200);
		prerequisitesPanel.setHeight(150);
		
		prerequisites_max = new SimpleComboBox<String>();
		prerequisites_max.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxP());
		prerequisites_max.add(values);
		prerequisites_max.setTriggerAction(TriggerAction.ALL);
		
		
		prerequisites_enable_groups = new SimpleComboBox<Boolean>();
		prerequisites_enable_groups.setFieldLabel(I18N.CONSTANTS.adminLogFrameEnablePGroups());
		prerequisites_enable_groups.add(true);
		prerequisites_enable_groups.add(false);
		prerequisites_enable_groups.setTriggerAction(TriggerAction.ALL);
		prerequisites_enable_groups.setLabelSeparator("");
		
		
		prerequisitesFirstPanel = new FormPanel();
		prerequisitesFirstPanel.setHeaderVisible(false);
		prerequisitesFirstPanel.setWidth(400);
		prerequisitesFirstPanel.setHeight(150);
		
		prerequisitesFirstPanel.add(prerequisites_max);
		prerequisitesFirstPanel.add(prerequisites_enable_groups);
		
        prerequisitesPanel.add(prerequisitesFirstPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        prerequisites_max_groups = new SimpleComboBox<String>();
        prerequisites_max_groups.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxPGroups());
		prerequisites_max_groups.add(values);
		prerequisites_max_groups.setTriggerAction(TriggerAction.ALL);
		
        
        prerequisitesSecondPanel = new FormPanel();
        prerequisitesSecondPanel.setHeaderVisible(false);
        prerequisitesSecondPanel.setWidth(400);
        prerequisitesSecondPanel.setHeight(150);
        
        prerequisitesSecondPanel.add(prerequisites_max_groups);
        prerequisitesPanel.add(prerequisitesSecondPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        prerequisites_max_per_group = new SimpleComboBox<String>();
        prerequisites_max_per_group.setFieldLabel(I18N.CONSTANTS.adminLogFrameMaxPPerGroup());
		prerequisites_max_per_group.add(values);
		prerequisites_max_per_group.setTriggerAction(TriggerAction.ALL);
		
        
        prerequisitesThirdPanel = new FormPanel();
        prerequisitesThirdPanel.setHeaderVisible(false);
        prerequisitesThirdPanel.setWidth(400);
        prerequisitesThirdPanel.setHeight(150);
        
        prerequisitesThirdPanel.add(prerequisites_max_per_group);

        prerequisitesPanel.add(prerequisitesThirdPanel, new HBoxLayoutData(0, 4, 0, 0));
        
        final VBoxLayoutData topVBoxLayoutData0 = new VBoxLayoutData();
        topVBoxLayoutData0.setMinWidth(1200);
        topVBoxLayoutData0.setFlex(1.0);
        logFrameMainPanel.add(mainPanel, topVBoxLayoutData0);
        
        final VBoxLayoutData topVBoxLayoutData = new VBoxLayoutData();
        topVBoxLayoutData.setMinWidth(1200);
        topVBoxLayoutData.setFlex(2.0);
        logFrameMainPanel.add(objectivesPanel, topVBoxLayoutData);
        
        final VBoxLayoutData topVBoxLayoutData1 = new VBoxLayoutData();
        topVBoxLayoutData1.setMinWidth(1200);
        topVBoxLayoutData1.setFlex(3.0);
        logFrameMainPanel.add(activitiesPanel, topVBoxLayoutData1);
        
        final VBoxLayoutData topVBoxLayoutData2 = new VBoxLayoutData();
        topVBoxLayoutData2.setMinWidth(1200);
        topVBoxLayoutData2.setFlex(4.0);
        logFrameMainPanel.add(resultsPanel, topVBoxLayoutData2);
        
        final VBoxLayoutData topVBoxLayoutData3 = new VBoxLayoutData();
        topVBoxLayoutData3.setMinWidth(1200);
        topVBoxLayoutData3.setFlex(5.0);
        logFrameMainPanel.add(prerequisitesPanel, topVBoxLayoutData3);
        
        logFrameMainPanel.layout();
	}
	
	private ToolBar initToolBar() {
		
		ToolBar toolbar = new ToolBar();
    	
		final Button saveButton = new Button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
        saveButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				updateLogFrame();
			}
        });
		
		toolbar.add(saveButton);
	    return toolbar;
    }

	public void fillLogFrame(LogFrameModelDTO logFrameModel){
		if(logFrameModel != null){
			name.setValue(logFrameModel.getName());
			if(logFrameModel.getSpecificObjectivesMax()!= null){
				objectives_max.setSimpleValue(logFrameModel.getSpecificObjectivesMax().toString());
			}else{
				objectives_max.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getSpecificObjectivesPerGroupMax()!= null){
				objectives_max_per_group.setSimpleValue(logFrameModel.getSpecificObjectivesPerGroupMax().toString());
			}else{
				objectives_max_per_group.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getEnableSpecificObjectivesGroups()!= null){
	        	objectives_enable_groups.setSimpleValue(logFrameModel.getEnableSpecificObjectivesGroups());
			}
			if(logFrameModel.getSpecificObjectivesGroupsMax()!= null){
				objectives_max_groups.setSimpleValue(logFrameModel.getSpecificObjectivesGroupsMax().toString());
			}else{
				objectives_max_groups.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getActivitiesMax()!= null){
				activities_max.setSimpleValue(logFrameModel.getActivitiesMax().toString());
			}else{
				activities_max.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getActivitiesGroupsMax()!= null){
				activities_max_groups.setSimpleValue(logFrameModel.getActivitiesGroupsMax().toString());
			}else{
				activities_max_groups.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getActivitiesPerGroupMax()!= null){
				activities_max_per_group.setSimpleValue(logFrameModel.getActivitiesPerGroupMax().toString());
			}else{
				activities_max_per_group.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getActivitiesPerExpectedResultMax()!= null){
				activities_max_per_result.setSimpleValue(logFrameModel.getActivitiesPerExpectedResultMax().toString());
			}else{
				activities_max_per_result.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getEnableActivitiesGroups()!= null){
				activities_enable_groups.setSimpleValue(logFrameModel.getEnableActivitiesGroups());
			}
			if(logFrameModel.getExpectedResultsMax()!= null){
				results_max.setSimpleValue(logFrameModel.getExpectedResultsMax().toString());
			}else{
				results_max.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getEnableExpectedResultsGroups()!= null){
				results_enable_groups.setSimpleValue(logFrameModel.getEnableExpectedResultsGroups());
			}
			if(logFrameModel.getExpectedResultsGroupsMax()!= null){
				results_max_groups.setSimpleValue(logFrameModel.getExpectedResultsGroupsMax().toString());
			}else{
				results_max_groups.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getExpectedResultsPerGroupMax()!= null){
				results_max_per_group.setSimpleValue(logFrameModel.getExpectedResultsPerGroupMax().toString());
			}else{
				results_max_per_group.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getExpectedResultsPerSpecificObjectiveMax()!= null){
				results_max_per_obj.setSimpleValue(logFrameModel.getExpectedResultsPerSpecificObjectiveMax().toString());
			}else{
				results_max_per_obj.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getPrerequisitesMax()!= null){
				prerequisites_max.setSimpleValue(logFrameModel.getPrerequisitesMax().toString());
			}else{
				prerequisites_max.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getEnablePrerequisitesGroups()!= null){
				prerequisites_enable_groups.setSimpleValue(logFrameModel.getEnablePrerequisitesGroups());
			}
			if(logFrameModel.getPrerequisitesGroupsMax()!= null){
				prerequisites_max_groups.setSimpleValue(logFrameModel.getPrerequisitesGroupsMax().toString());
			}else{
				prerequisites_max_groups.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
			if(logFrameModel.getPrerequisitesPerGroupMax()!= null){
				prerequisites_max_per_group.setSimpleValue(logFrameModel.getPrerequisitesPerGroupMax().toString());
			}else{
				prerequisites_max_per_group.setSimpleValue(I18N.CONSTANTS.adminLogFrameUnlimited());
			}
		}
	}
	
	private Integer translateMaxValue(String value){
		Integer max = null;
		if(value != null && I18N.CONSTANTS.adminLogFrameUnlimited().equals(value)){
			max = null;
		}else{
			max = new Integer(value);
		}
		
		return max;
	}
	
	private void updateLogFrame(){
		
		if(!mainPanel.isValid()){
			MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
                    I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminStandardLogFrame()), null);
            return;
		}
		
		HashMap<String, Object> logFrameProperties = new HashMap<String, Object>();
		
		logFrameProperties.put(AdminUtil.PROP_LOG_FRAME, true);
		logFrameProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, projectModel);
		logFrameProperties.put(AdminUtil.PROP_LOG_FRAME_NAME, name.getValue());
		if(objectives_max.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_OBJ_MAX, translateMaxValue(objectives_max.getValue().getValue()));
		if(objectives_max_per_group.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_OBJ_MAX_PER_GROUP, translateMaxValue(objectives_max_per_group.getValue().getValue()));
		if(objectives_enable_groups.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_OBJ_ENABLE_GROUPS, objectives_enable_groups.getValue().getValue());
		if(objectives_max_groups.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_OBJ_MAX_GROUPS, translateMaxValue(objectives_max_groups.getValue().getValue()));
		if(activities_max.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_A_MAX, translateMaxValue(activities_max.getValue().getValue()));
		if(activities_enable_groups.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_A_ENABLE_GROUPS, activities_enable_groups.getValue().getValue());
		if(activities_max_per_result.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_A_MAX_PER_RESULT, translateMaxValue(activities_max_per_result.getValue().getValue()));
		if(activities_max_groups.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_A_MAX_GROUPS, translateMaxValue(activities_max_groups.getValue().getValue()));
		if(activities_max_per_group.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_A_MAX_PER_GROUP, translateMaxValue(activities_max_per_group.getValue().getValue()));
		if( results_max.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_R_MAX, translateMaxValue(results_max.getValue().getValue()));
		if(results_enable_groups.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_R_ENABLE_GROUPS, results_enable_groups.getValue().getValue());
		if(results_max_per_obj.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_R_MAX_PER_OBJ, translateMaxValue(results_max_per_obj.getValue().getValue()));
		if(results_max_groups.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_R_MAX_GROUPS, translateMaxValue(results_max_groups.getValue().getValue()));
		if(results_max_per_group.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_R_MAX_PER_GROUP, translateMaxValue(results_max_per_group.getValue().getValue()));
		if(prerequisites_max.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_P_MAX, translateMaxValue(prerequisites_max.getValue().getValue()));
		if(prerequisites_enable_groups.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_P_ENABLE_GROUPS, prerequisites_enable_groups.getValue().getValue());
		if(prerequisites_max_groups.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_P_MAX_GROUPS, translateMaxValue(prerequisites_max_groups.getValue().getValue()));
		if(prerequisites_max_per_group.getValue()!= null)
			logFrameProperties.put(AdminUtil.PROP_P_MAX_PER_GROUP, translateMaxValue(prerequisites_max_per_group.getValue().getValue()));
		
		dispatcher.execute(new CreateEntity("ProjectModel", logFrameProperties), null, new AsyncCallback<CreateResult>(){
            public void onFailure(Throwable caught) {  
            	MessageBox.alert(I18N.CONSTANTS.adminLogFrameUpdate(), I18N.MESSAGES.adminLogFrameUpdateFailure(), null);
            }

			@Override
			public void onSuccess(CreateResult result) {
				if(result != null && result.getEntity() != null)
					Notification.show(I18N.CONSTANTS.adminLogFrameUpdate(), I18N.MESSAGES.adminLogFrameUpdateSuccess());
				else
					MessageBox.alert(I18N.CONSTANTS.adminLogFrameUpdate(), I18N.MESSAGES.adminLogFrameUpdateFailure(), null);
			}
		});
		
		
	}

	@Override
	public Component getMainPanel() {
		return logFrameMainPanel;
	}

	@Override
	public void enableToolBar() {
		logFrameMainPanel.setTopComponent(initToolBar());
	}



}
