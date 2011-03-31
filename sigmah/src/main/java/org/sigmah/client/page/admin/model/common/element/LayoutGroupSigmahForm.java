/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.admin.model.common.element;

import java.util.HashMap;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.OrgUnitDetailsDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectDetailsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

/**
 * Create user form.
 * 
 * @author nrebiai
 * 
 */
public class LayoutGroupSigmahForm extends FormPanel {
	
	private final TextField<String> nameField;
	private final ComboBox<BaseModelData> containerList;
	private final SimpleComboBox<Integer> rowField;
	private final SimpleComboBox<Integer> columnField;
	private LayoutGroupDTO layoutGroupToUpdate;
	private ProjectModelDTO projectModelToUpdate;
	private OrgUnitModelDTO orgUnitModelToUpdate;
	
	private final Dispatcher dispatcher;
	private HashMap<String, Object> newPrivacyGroupProperties;
	
	private final static int LABEL_WIDTH = 90;
	
	public LayoutGroupSigmahForm(Dispatcher dispatcher, 
			final AsyncCallback<CreateResult> callback, FlexibleElementDTO fxToUpdate, ProjectModelDTO projectModelToUpdate
			, OrgUnitModelDTO orgUnitModelToUpdate) {
		
		this.dispatcher = dispatcher;
		this.projectModelToUpdate = projectModelToUpdate;
		this.orgUnitModelToUpdate = orgUnitModelToUpdate;
		
		UIConstants constants = GWT.create(UIConstants.class);
		
		if(fxToUpdate != null){
			layoutGroupToUpdate = fxToUpdate.getGroup();
		}
			
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		setLayout(layout);
		
		
		nameField = new TextField<String>();
		nameField.setFieldLabel(constants.adminPrivacyGroupsName());
		nameField.setAllowBlank(false);
		if(layoutGroupToUpdate != null && layoutGroupToUpdate.getTitle()!= null && !layoutGroupToUpdate.getTitle().isEmpty())
			nameField.setValue(layoutGroupToUpdate.getTitle());
		add(nameField);
		
		final ListStore<BaseModelData> containersStore = new ListStore<BaseModelData>();
		containerList = new ComboBox<BaseModelData>();
		containerList.setEditable(false);
		containerList.setDisplayField("name");
		containerList.setValueField("id");
		containerList.setFieldLabel(I18N.CONSTANTS.adminFlexibleContainer());
		containerList.setAllowBlank(false);
		containerList.setTriggerAction(TriggerAction.ALL);		
		
		
		if(projectModelToUpdate != null){
			containersStore.add(projectModelToUpdate.getProjectDetailsDTO());
			for(PhaseModelDTO p : projectModelToUpdate.getPhaseModelsDTO()){
				containersStore.add(p);
			}
		}else if(orgUnitModelToUpdate != null){
			containersStore.add(orgUnitModelToUpdate.getDetails());
		}
		
		containerList.setStore(containersStore);
		
		rowField = new SimpleComboBox<Integer>();
		rowField.setEditable(false);
		rowField.setFieldLabel(constants.adminFlexibleGroupVPosition());
		if(fxToUpdate != null){
			rowField.setSimpleValue(layoutGroupToUpdate.getRow());
		}
		
		columnField = new SimpleComboBox<Integer>();
		columnField.setEditable(false);
		columnField.setFieldLabel(constants.adminFlexibleGroupHPosition());
		if(fxToUpdate != null){
			columnField.setSimpleValue(layoutGroupToUpdate.getColumn());
		}
		
		if(fxToUpdate != null){
			containerList.setValue(fxToUpdate.getContainerModel());
			if(fxToUpdate.getContainerModel() != null){
				LayoutDTO container = new LayoutDTO();
				if(fxToUpdate.getContainerModel() instanceof ProjectDetailsDTO){
					 container = ((ProjectDetailsDTO)containerList.getValue()).getLayoutDTO();
				}else if(fxToUpdate.getContainerModel() instanceof PhaseModelDTO){
					 container = ((PhaseModelDTO)containerList.getValue()).getLayoutDTO();
				}else if(fxToUpdate.getContainerModel() instanceof OrgUnitDetailsDTO){
					 container = ((OrgUnitDetailsDTO)containerList.getValue()).getLayout();
				}
				if(container != null){
					rowField.removeAll();
					 for(int i=0; i<container.getRowsCount();i++){						 						 
						 rowField.add(i);
					 }
					 columnField.removeAll();
					 for(int i=0; i<container.getColumnsCount();i++){						 						 
						 columnField.add(i);
					 }
				}
				 
			 }
		}
		
		containerList.addListener(Events.Select, new Listener<BaseEvent>() {
			
			@Override
			public void handleEvent(BaseEvent be) {
				LayoutDTO container = null;
				 if(containerList.getValue() != null){
					 if(containerList.getValue() instanceof ProjectDetailsDTO){
						 container = ((ProjectDetailsDTO)containerList.getValue()).getLayoutDTO();
					 }else if(containerList.getValue() instanceof PhaseModelDTO){
						 container = ((PhaseModelDTO)containerList.getValue()).getLayoutDTO();
					 }else if(containerList.getValue() instanceof OrgUnitDetailsDTO){
						 container = ((OrgUnitDetailsDTO)containerList.getValue()).getLayout();
					 }
				 }
				 if(container != null){
					 rowField.removeAll();
					 for(int i=0; i<container.getRowsCount();i++){						 						 
						 rowField.add(i);
					 }
					 columnField.removeAll();
					 for(int i=0; i<container.getColumnsCount();i++){						 						 
						 columnField.add(i);
					 }
				 }				 
			}
		});
		
		add(containerList);		
		add(rowField);		
		add(columnField);
			
		// Create button.
        final Button createButton = new Button(I18N.CONSTANTS.save());
        createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                createLayoutGroup(callback);
            }
        });
        add(createButton);
  	}

	private void createLayoutGroup(final AsyncCallback<CreateResult> callback) {
		 if (!this.isValid()) {
			 MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
	                    I18N.MESSAGES.createFormIncompleteDetails(I18N.MESSAGES.adminStandardLayoutGroup()), null);
	            return;
		 }
		 
		 
		 final String name = nameField.getValue();
		 
		 Integer row = null;
		 if(rowField.getValue() != null)
			 row = new Integer(rowField.getValue().getValue());
		 
		 Integer column = null;
		 if(columnField.getValue() != null)
			 column = new Integer(columnField.getValue().getValue());
		 
		 LayoutDTO container = null;
		 if(containerList.getValue() != null){
			 if(containerList.getValue() instanceof ProjectDetailsDTO){
				 container = ((ProjectDetailsDTO)containerList.getValue()).getLayoutDTO();
			 }else if(containerList.getValue() instanceof PhaseModelDTO){
				 container = ((PhaseModelDTO)containerList.getValue()).getLayoutDTO();
			 }else if(containerList.getValue() instanceof OrgUnitDetailsDTO){
				 container = ((OrgUnitDetailsDTO)containerList.getValue()).getLayout();
			 }
		 }
		 
		 HashMap<String, Object> newGroupProperties = new HashMap<String, Object>();
		 
		 LayoutGroupDTO layoutGroupDTO = new LayoutGroupDTO();
		 if(layoutGroupToUpdate != null)
			 layoutGroupDTO = layoutGroupToUpdate;
		 
		 layoutGroupDTO.setTitle(name);
		 layoutGroupDTO.setRow(row);
		 layoutGroupDTO.setColumn(column);
		 layoutGroupDTO.setParentLayoutDTO(container);
		 
		 newGroupProperties.put(AdminUtil.PROP_NEW_GROUP_LAYOUT, layoutGroupDTO);
		 newGroupProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, projectModelToUpdate);
		 newGroupProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, orgUnitModelToUpdate);
		 
		 dispatcher.execute(new CreateEntity("GroupLayout", newGroupProperties), null, new AsyncCallback<CreateResult>(){
             public void onFailure(Throwable caught) {
             	MessageBox.alert(I18N.CONSTANTS.adminFlexibleCreationBox(), 
             			I18N.MESSAGES.adminStandardCreationFailure(I18N.MESSAGES.adminStandardLayoutGroup()
								+ " '" + name + "'"), null);
             	callback.onFailure(caught);
             }

			@Override
			public void onSuccess(CreateResult result) {
				if(result != null){
					callback.onSuccess(result);	
					
				}					
				else{
					Throwable t = new Throwable("ElementForm : creation result is null");					
					MessageBox.alert(I18N.CONSTANTS.adminFlexibleCreationBox(), 
						I18N.MESSAGES.adminStandardCreationNull(I18N.MESSAGES.adminStandardLayoutGroup()
									+ " '" + name+"'"), null);
					callback.onFailure(t);
				}		
			}
         });
		 
	}
	
	public HashMap<String, Object> getPrivacyGroupsProperties(){
		return newPrivacyGroupProperties;
	}
}
