package org.sigmah.client.page.admin.model.orgunit;

import java.util.HashMap;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.dto.OrgUnitModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OrgUnitModelForm extends FormPanel {
	
	private final Dispatcher dispatcher;
	private final TextField<String> nameField;
	private final TextField<String> titleField;
	private final CheckBox hasBudgetCheckBox;
	private final CheckBox canContainProjectsCheckBox;	
	private final NumberField minLevelField;
	private final NumberField maxLevelField;

	private HashMap<String, Object> newOrgUnitModelProperties;
	
	private final static int LABEL_WIDTH = 90;
	
	public OrgUnitModelForm(Dispatcher dispatcher, 
			final AsyncCallback<CreateResult> callback) {
		this.dispatcher = dispatcher;
		UIConstants constants = GWT.create(UIConstants.class);
		
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		setLayout(layout);
		
		nameField = new TextField<String>();
		nameField.setFieldLabel(constants.adminOrgUnitsModelName());
		nameField.setAllowBlank(false);
		add(nameField);
		
		titleField = new TextField<String>();
		titleField.setFieldLabel(constants.adminOrgUnitsModelTitle());
		titleField.setAllowBlank(false);
		add(titleField);
		
		hasBudgetCheckBox = new CheckBox();
		hasBudgetCheckBox.setBoxLabel(constants.adminOrgUnitsModelHasBudget());
		hasBudgetCheckBox.setFieldLabel(constants.adminOrgUnitsModelHasBudget());
		hasBudgetCheckBox.setValue(false);
		add(hasBudgetCheckBox);
		
		canContainProjectsCheckBox = new CheckBox();
		canContainProjectsCheckBox.setBoxLabel(constants.adminOrgUnitsModelContainProjects());
		canContainProjectsCheckBox.setFieldLabel(constants.adminOrgUnitsModelContainProjects());
		canContainProjectsCheckBox.setValue(false);
		add(canContainProjectsCheckBox);
		
		maxLevelField = new NumberField();
		maxLevelField.setFieldLabel(I18N.CONSTANTS.adminOrgUnitsModelMaxLevel());
		maxLevelField.setAllowBlank(true);
		add(maxLevelField);
		
		minLevelField = new NumberField();
		minLevelField.setFieldLabel(I18N.CONSTANTS.adminOrgUnitsModelMinLevel());
		minLevelField.setAllowBlank(true);
		add(minLevelField);
		
		// Create button.
        final Button createButton = new Button(I18N.CONSTANTS.save());
        createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                createOrgUnitModel(callback);
            }
        });
        add(createButton);
	}
	
	private void createOrgUnitModel(final AsyncCallback<CreateResult> callback) {
		 if (!this.isValid()) {
			 MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
	                    I18N.MESSAGES.createFormIncompleteDetails(I18N.MESSAGES.adminStandardPrivacyGroup()), null);
	            return;
		 }
		 
		 final String name = nameField.getValue();
		 final String title = titleField.getValue();
		 final Boolean hasBudget = hasBudgetCheckBox.getValue();	 
		 final Boolean containsProjects = canContainProjectsCheckBox.getValue();
		 Integer minLevel = 0;
		 if(minLevelField.getValue() != null){
			  minLevel = new Integer(minLevelField.getValue().intValue());
		 }
		 Integer maxLevel = 0;
		 if(maxLevelField.getValue() != null){
			 maxLevel = new Integer(maxLevelField.getValue().intValue());
		 }
		 
		 newOrgUnitModelProperties = new HashMap<String, Object>();
		 newOrgUnitModelProperties.put(AdminUtil.PROP_OM_NAME, name);
		 newOrgUnitModelProperties.put(AdminUtil.PROP_OM_TITLE, title);
		 newOrgUnitModelProperties.put(AdminUtil.PROP_OM_HAS_BUDGET, hasBudget);
		 newOrgUnitModelProperties.put(AdminUtil.PROP_OM_CONTAINS_PROJECTS, containsProjects);
		 newOrgUnitModelProperties.put(AdminUtil.PROP_OM_MIN_LEVEL, minLevel);
		 newOrgUnitModelProperties.put(AdminUtil.PROP_OM_MAX_LEVEL, maxLevel);
  
		 OrgUnitModelDTO model = new OrgUnitModelDTO();
		 model.setStatus(ProjectModelStatus.DRAFT);
		 newOrgUnitModelProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, model);
		 
        dispatcher.execute(new CreateEntity("OrgUnitModel", newOrgUnitModelProperties), null, new AsyncCallback<CreateResult>(){

       	 public void onFailure(Throwable caught) {
       		MessageBox.alert(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(), 
          			I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminOrgUnitsModelStandard()
								+ " '" + name + "'"), null);
             	callback.onFailure(caught);
             }

			@Override
			public void onSuccess(CreateResult result) {
				if(result != null && result.getEntity() != null){						
					callback.onSuccess(result);		
					
					Notification.show(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(), I18N.MESSAGES.adminStandardCreationSuccess(I18N.CONSTANTS.adminOrgUnitsModelStandard()
							+ " '" + name +"'"));					
				}					
				else{
					Throwable t = new Throwable("OrgUnitModelForm : creation result is null");
					callback.onFailure(t);
					MessageBox.alert(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(), 
		          			I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminOrgUnitsModelStandard()
										+ " '" + name + "'"), null);
				}		
			}
        });
		 
	}
	
	
	public HashMap<String, Object> getPrivacyGroupsProperties(){
		return newOrgUnitModelProperties;
	}
}
