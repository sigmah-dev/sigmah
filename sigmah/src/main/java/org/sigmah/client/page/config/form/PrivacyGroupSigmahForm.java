/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.form;

import java.util.HashMap;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.util.Notification;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

/**
 * Create user form.
 * 
 * @author nrebiai
 * 
 */
public class PrivacyGroupSigmahForm extends FormPanel {

	private final Integer pgToUpdateId;
	private final TextField<String> nameField;
	private final NumberField codeField;
	
	private final Dispatcher dispatcher;
	private HashMap<String, Object> newPrivacyGroupProperties;
	
	private final static int LABEL_WIDTH = 90;
	
	public PrivacyGroupSigmahForm(Dispatcher dispatcher, UserLocalCache userCache, 
			final AsyncCallback<CreateResult> callback, PrivacyGroupDTO privacyGroupToUpdate) {
		
		this.dispatcher = dispatcher;
		UIConstants constants = GWT.create(UIConstants.class);
		
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		setLayout(layout);
		if(privacyGroupToUpdate != null)
			pgToUpdateId = privacyGroupToUpdate.getId();
		else
			pgToUpdateId = -1;
		
		codeField = new NumberField();
		codeField.setFieldLabel(constants.adminPrivacyGroupsCode());
		codeField.setAllowBlank(false);
		if(privacyGroupToUpdate != null)
			codeField.setValue(privacyGroupToUpdate.getCode());
		add(codeField);
		
		nameField = new TextField<String>();
		nameField.setFieldLabel(constants.adminPrivacyGroupsName());
		nameField.setAllowBlank(false);
		if(privacyGroupToUpdate != null && !privacyGroupToUpdate.getTitle().isEmpty())
			nameField.setValue(privacyGroupToUpdate.getTitle());
		add(nameField);
			
		// Create button.
        final Button createButton = new Button(I18N.CONSTANTS.save());
        createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                createPrivacyGroup(callback);
            }
        });
        add(createButton);
  	}

	private void createPrivacyGroup(final AsyncCallback<CreateResult> callback) {
		 if (!this.isValid()) {
			 MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
	                    I18N.MESSAGES.createFormIncompleteDetails(I18N.MESSAGES.adminStandardPrivacyGroup()), null);
	            return;
		 }
		 final String name = nameField.getValue();
		 final Number code = codeField.getValue();
		 
		 newPrivacyGroupProperties = new HashMap<String, Object>();
		 newPrivacyGroupProperties.put("id", pgToUpdateId);
		 newPrivacyGroupProperties.put("code", code);
		 newPrivacyGroupProperties.put("name", name);   
		 
         dispatcher.execute(new CreateEntity("PrivacyGroup", newPrivacyGroupProperties), null, new AsyncCallback<CreateResult>(){

        	 public void onFailure(Throwable caught) {
              	MessageBox.alert(I18N.CONSTANTS.adminPrivacyGroupCreationBox(), 
              			I18N.MESSAGES.adminStandardCreationFailure(I18N.MESSAGES.adminStandardPrivacyGroup()
 								+ " '" + name + " '"), null);
              	callback.onFailure(caught);
              }

 			@Override
 			public void onSuccess(CreateResult result) {
 				if(result != null){						
 					callback.onSuccess(result);		
 					if(pgToUpdateId != 0){
 						Notification.show(I18N.CONSTANTS.adminPrivacyGroupCreationBox(), 
 	 							I18N.MESSAGES.adminStandardUpdateSuccess(I18N.MESSAGES.adminStandardPrivacyGroup()
 	 									+ " '" + result.getEntity().get("title"))+ " '");
 					}else{
 						Notification.show(I18N.CONSTANTS.adminPrivacyGroupCreationBox(), 
 	 							I18N.MESSAGES.adminStandardCreationSuccess(I18N.MESSAGES.adminStandardPrivacyGroup()
 	 									+ " '" + result.getEntity().get("title"))+ " '");
 					}					
 				}					
 				else{
 					Throwable t = new Throwable("PrivacyGroupSigmahForm : creation result is null");
 					callback.onFailure(t);
 					MessageBox.alert(I18N.CONSTANTS.adminPrivacyGroupCreationBox(), 
 							I18N.MESSAGES.adminStandardCreationNull(I18N.MESSAGES.adminStandardPrivacyGroup()
 									+ " '" + name+ " '"), null);
 				}		
 			}
         });
		 
	}
	
	public HashMap<String, Object> getPrivacyGroupsProperties(){
		return newPrivacyGroupProperties;
	}
}
