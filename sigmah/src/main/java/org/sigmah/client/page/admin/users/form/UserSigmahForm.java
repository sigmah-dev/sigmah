/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.admin.users.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.client.cache.UserLocalCache;
import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.ui.ClickableLabel;
import org.sigmah.client.util.Notification;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetProfiles;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ProfileListResult;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.profile.ProfileDTOLight;

/**
 * Create user form.
 * 
 * @author nrebiai
 * 
 */
public class UserSigmahForm extends FormPanel {

	private final Integer userToUpdateId;
	private final TextField<String> nameField;
	private final TextField<String> firstNameField;
	private final TextField<String> pwdField;
	private final TextField<String> checkPwdField;
	private final TextField<String> emailField;
	private final SimpleComboBox<String> localeField;
	private final ComboBox<OrgUnitDTOLight> orgUnitsList;
	private final ListStore<OrgUnitDTOLight> orgUnitsStore;
	private final ComboBox<ProfileDTOLight> profilesListCombo;
	private final Map<Integer, ClickableLabel> selectedProfiles = new HashMap<Integer, ClickableLabel>();
	private final List<Integer> selectedProfilesIds = new ArrayList<Integer>();
	
	private final Dispatcher dispatcher;
	private HashMap<String, Object> newUserProperties;
	private int num = 0;
	
	private final static int LABEL_WIDTH = 90;
	private final static int MAX_PROFILES_TENTATIVES_PER_USER = 100;
	private final static String ID_PROFILE = "idProfile";
	
	public UserSigmahForm(Dispatcher dispatcher, UserLocalCache cache, 
			final AsyncCallback<CreateResult> callback, UserDTO userToUpdate) {
		
		this.dispatcher = dispatcher;
		UIConstants constants = GWT.create(UIConstants.class);
		
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		setLayout(layout);
		if(userToUpdate != null)
			userToUpdateId = userToUpdate.getId();
		else
			userToUpdateId = -1;
		
		nameField = new TextField<String>();
		nameField.setFieldLabel(constants.adminUsersName());
		nameField.setAllowBlank(false);
		if(userToUpdate != null && !userToUpdate.getName().isEmpty())
			nameField.setValue(userToUpdate.getName());
		add(nameField);
		
		firstNameField = new TextField<String>();
		firstNameField.setFieldLabel(constants.adminUsersFirstName());
		firstNameField.setAllowBlank(false);
		if(userToUpdate != null && !userToUpdate.getFirstName().isEmpty())
			firstNameField.setValue(userToUpdate.getFirstName());
		add(firstNameField);
		
		emailField = new TextField<String>();
		emailField.setFieldLabel(constants.adminUsersEmail());
		emailField.setAllowBlank(false);
		if(userToUpdate != null && !userToUpdate.getEmail().isEmpty())
			emailField.setValue(userToUpdate.getEmail());
		add(emailField);
		
		pwdField = new TextField<String>();
		pwdField.setFieldLabel(constants.password());
		pwdField.setAllowBlank(true);
		pwdField.setPassword(true);
		pwdField.addKeyListener(new KeyListener(){
			public void componentKeyUp(ComponentEvent event) {
				if(pwdField.getValue()!= null && !pwdField.getValue().isEmpty())
					checkPwdField.setAllowBlank(false);
				else
					checkPwdField.setAllowBlank(true);
			}
		});
		if(userToUpdate != null && !userToUpdate.getLocale().isEmpty()){			
			add(pwdField);
		}
		
		
		checkPwdField = new TextField<String>();
		checkPwdField.setFieldLabel(constants.confirmPassword());
		checkPwdField.setAllowBlank(true);
		checkPwdField.setPassword(true);
		checkPwdField.addKeyListener(new KeyListener(){
			public void componentKeyUp(ComponentEvent event) {
				if(checkPwdField.getValue()!= null && !checkPwdField.getValue().isEmpty()){
					pwdField.setAllowBlank(false);
					if(!checkPwdField.getValue().equals(pwdField.getValue()))
						checkPwdField.forceInvalid(I18N.MESSAGES.pwdMatchProblem());
					else
						checkPwdField.clearInvalid();
				}					
				else
					pwdField.setAllowBlank(true);
			}
		});
		if(userToUpdate != null && !userToUpdate.getLocale().isEmpty()){			
			add(checkPwdField);
		}
			
		
		localeField = new SimpleComboBox<String>();
		localeField.add(I18N.CONSTANTS.adminUsersLocaleFr());
		localeField.add(I18N.CONSTANTS.adminUsersLocaleEn());
		localeField.setFieldLabel(constants.adminUsersLocale());
		localeField.setTriggerAction(TriggerAction.ALL);
		localeField.setAllowBlank(false);
		if(userToUpdate != null && !userToUpdate.getLocale().isEmpty())
			localeField.setSimpleValue(userToUpdate.getLocale());
		add(localeField);
		
		orgUnitsList = new ComboBox<OrgUnitDTOLight>();
		orgUnitsList.setFieldLabel(I18N.CONSTANTS.adminUsersOrgUnit());
		orgUnitsList.setDisplayField("fullName");
		orgUnitsList.setValueField("id");
		orgUnitsList.setEditable(false);
		orgUnitsList.setAllowBlank(false);
		orgUnitsList.setTriggerAction(TriggerAction.ALL);		
		if(userToUpdate != null && userToUpdate.getOrgUnitWithProfiles() != null
				&& !userToUpdate.getOrgUnitWithProfiles().getFullName().isEmpty()){
			OrgUnitDTOLight orgUnitDTOLight = new OrgUnitDTOLight();
			orgUnitDTOLight.setId(userToUpdate.getOrgUnitWithProfiles().getId());
			orgUnitDTOLight.setFullName(userToUpdate.getOrgUnitWithProfiles().getFullName());
			orgUnitsList.setValue(orgUnitDTOLight);
			//orgUnitsList.setEmptyText(userToUpdate.getOrgUnitWithProfiles().getFullName());
		}
		else
			orgUnitsList.setEmptyText(I18N.CONSTANTS.adminUserCreationOrgUnitChoice());
		orgUnitsStore = new ListStore<OrgUnitDTOLight>();        
        orgUnitsList.setStore(orgUnitsStore);
        cache.getOrganizationCache().get(new AsyncCallback<OrgUnitDTOLight>() {
			@Override
            public void onFailure(Throwable e) {
				orgUnitsList.setEmptyText(I18N.CONSTANTS.adminChoiceProblem());
            }

            @Override
            public void onSuccess(OrgUnitDTOLight result) {
            	orgUnitsStore.removeAll();
                if (result != null) {
                	fillOrgUnitsList(result);
                	orgUnitsStore.commitChanges();	
                }
            }
		});			
		add(orgUnitsList);
		
		/* *****************************************************************Profiles*********************************/
		//create 100 clickable labels as max possible profiles
		for(int i = 0; i < MAX_PROFILES_TENTATIVES_PER_USER; i++){        	
        	final ClickableLabel label = new ClickableLabel();
        	label.addClickHandler(new ClickHandler(){
    			@Override
    			public void onClick(ClickEvent arg0) {
    				label.hide();
    				selectedProfilesIds.remove((Integer)label.getData(ID_PROFILE));
    			}
    			
    		});
        	label.hide();
        	selectedProfiles.put(i, label);
        }
		
		profilesListCombo = new ComboBox<ProfileDTOLight>();
		profilesListCombo.setDisplayField("name");
		profilesListCombo.setValueField("id");
		profilesListCombo.setEditable(false);		
		profilesListCombo.setTriggerAction(TriggerAction.ALL);
		final ListStore<ProfileDTOLight> profilesStore = new ListStore<ProfileDTOLight>();
		dispatcher.execute(new GetProfiles(), 
        		null,
        		new AsyncCallback<ProfileListResult>() {

					@Override
					public void onFailure(Throwable arg0) {
						profilesListCombo.setEmptyText(I18N.CONSTANTS.adminChoiceProblem());
					}

					@Override
					public void onSuccess(ProfileListResult result) {
						profilesListCombo.setEmptyText(I18N.CONSTANTS.adminUserCreationProfileChoice());
						profilesStore.removeAll();
		                if (result != null) {
		                    profilesStore.add(result.getList());
		                    profilesStore.commitChanges();		                    
		                }						
					}			
		});
		
		profilesListCombo.setStore(profilesStore);
		
		final Grid profilesAddSelectionGrid = new Grid(1, 3);
		
		profilesAddSelectionGrid.getCellFormatter().setWidth(0, 0, (LABEL_WIDTH + 5)+"px");
		profilesAddSelectionGrid.setCellPadding(0);
		profilesAddSelectionGrid.setCellSpacing(0);
		profilesAddSelectionGrid.setWidget(0, 0, new LabelField(I18N.CONSTANTS.adminUsersProfiles()+":"));
		profilesAddSelectionGrid.setWidget(0, 1, profilesListCombo);
		profilesListCombo.setHideLabel(false);

		if(userToUpdate != null && userToUpdate.getOrgUnitWithProfiles() != null
        		&& userToUpdate.getProfilesDTO() != null){
        	List<ProfileDTO> usedProfiles = userToUpdate.getProfilesDTO();
        	for(ProfileDTO usedProfile : usedProfiles){
        		selectedProfilesIds.add(usedProfile.getId());
        	}
        }
		
		
		
		final Button addButton = new Button(I18N.CONSTANTS.addItem());
        addButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {          	
                if(profilesListCombo.getValue() != null){   
                	
                	if(!selectedProfilesIds.contains(profilesListCombo.getValue().getId())){
                		if(num < MAX_PROFILES_TENTATIVES_PER_USER){
                			selectedProfiles.get(num).setData(ID_PROFILE, new Integer(profilesListCombo.getValue().getId()));  
                    		selectedProfiles.get(num).setText(profilesListCombo.getValue().getName());
                    		selectedProfiles.get(num).show();
                    		num++;               		
                    		selectedProfilesIds.add(profilesListCombo.getValue().getId());
                		}else{
                			//FIXME
                			MessageBox.alert(I18N.CONSTANTS.adminMaxAttempts(), I18N.CONSTANTS.adminMaxAttemptsUsers(), null);
                			UserSigmahForm.this.removeFromParent();
                		}                		
                	}
                }                
            }
        });
        
        profilesAddSelectionGrid.setWidget(0, 2, addButton);
        add(profilesAddSelectionGrid);
        
        if(userToUpdate != null && userToUpdate.getOrgUnitWithProfiles() != null
        		&& userToUpdate.getProfilesDTO() != null){
        	List<ProfileDTO> usedProfiles = userToUpdate.getProfilesDTO();
        	for(final ProfileDTO usedProfile : usedProfiles){
        		if(num < MAX_PROFILES_TENTATIVES_PER_USER){
        			selectedProfiles.get(num).setData(ID_PROFILE, new Integer(usedProfile.getId()));
	        		selectedProfiles.get(num).setText(usedProfile.getName().toString());
	        		selectedProfiles.get(num).show();	        		
	        		num++;  
	        	}else{
	    			MessageBox.alert(I18N.CONSTANTS.adminMaxAttempts(), I18N.CONSTANTS.adminMaxAttemptsUsers(), null);
	    			UserSigmahForm.this.removeFromParent();
	    		} 
        	}
        }        

		for(ClickableLabel selected : selectedProfiles.values()){
        	UserSigmahForm.this.add(selected);
        }				
        	
		// Create button.
        final Button createButton = new Button(I18N.CONSTANTS.save());
        createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                createUser(callback);
            }
        });
        add(createButton);
  	}

	protected CheckBox createCheckBox(String property, String label) {
		CheckBox box = new CheckBox();
		box.setName(property);
		box.setBoxLabel(label);
		return box;
	}

	private void createUser(final AsyncCallback<CreateResult> callback) {
		 if (!this.isValid()) {
			 MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
	                    I18N.MESSAGES.createFormIncompleteDetails(I18N.MESSAGES.adminStandardUser()), null);
	            return;
		 }
		 
		 
		 final String name = nameField.getValue();
		 final String firstName = firstNameField.getValue();
		 final String email = emailField.getValue();
		 final String pwd = pwdField.getValue();
		 String locale = null;
		 if(localeField.getSimpleValue() != null){
			 if(localeField.getSimpleValue().equals(I18N.CONSTANTS.adminUsersLocaleFr())){
				 locale = "fr";
			 }
			 else{
				 locale = "en";
			 }
		 }
		 
		 final int orgUnit = orgUnitsList.getValue().getId();
		 final List<Integer> profiles = selectedProfilesIds;
		 
		 if((orgUnit!= 0 && profiles.isEmpty())||(orgUnit== 0 && !profiles.isEmpty())){
			 MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
	                    I18N.MESSAGES.createUserFormIncompleteDetails(), null);
	            return;
		 }
		 
		 newUserProperties = new HashMap<String, Object>();
		 newUserProperties.put("id", userToUpdateId);
		 newUserProperties.put("name", name);
		 newUserProperties.put("firstName", firstName);   
		 newUserProperties.put("pwd", pwd);
		 newUserProperties.put("email", email);
		 newUserProperties.put("locale", locale);
		 newUserProperties.put("orgUnit", orgUnit);
		 newUserProperties.put("profiles", profiles);
		 
         dispatcher.execute(new CreateEntity("User", newUserProperties), null, new AsyncCallback<CreateResult>(){

             public void onFailure(Throwable caught) {
             	MessageBox.alert(I18N.CONSTANTS.adminUserCreationBox(), I18N.MESSAGES.adminUserCreationFailure(firstName + " " +name), null);
             	callback.onFailure(caught);
             }

				@Override
				public void onSuccess(CreateResult result) {
					if(result != null){						
						callback.onSuccess(result);	
						if(userToUpdateId != 0)
							Notification.show(I18N.CONSTANTS.adminUserCreationBox(), I18N.MESSAGES.adminUserUpdateSuccess(name));
						else
							Notification.show(I18N.CONSTANTS.adminUserCreationBox(), I18N.MESSAGES.adminUserCreationSuccess(name));
					}					
					else{
						Throwable t = new Throwable("AdminUsersPresenter : creation result is null");
						callback.onFailure(t);
						MessageBox.alert(I18N.CONSTANTS.adminUserCreationBox(), I18N.MESSAGES.adminUserCreationNull(firstName + " " +name), null);
					}		
				}
         });
		 
	}
	
	public HashMap<String, Object> getUserProperties(){
		return newUserProperties;
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
}
