package org.sigmah.client.page.admin.model.common;

import java.util.HashMap;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

public class ProjectModelForm extends FormPanel {
	
	private final Dispatcher dispatcher;
	private final TextField<String> nameField;
	private final Radio ngoRadio;
    private final Radio fundingRadio;
    private final Radio partnerRadio;
    private ProjectModelType currentModelType = ProjectModelType.NGO;
	private HashMap<String, Object> newProjectModelProperties;
	
	private final static int LABEL_WIDTH = 90;
	
	public ProjectModelForm(Dispatcher dispatcher, 
			final AsyncCallback<CreateResult> callback) {
		this.dispatcher = dispatcher;
		UIConstants constants = GWT.create(UIConstants.class);
		
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		setLayout(layout);
		
		nameField = new TextField<String>();
		nameField.setFieldLabel(constants.adminProjectModelsName());
		nameField.setAllowBlank(false);
		add(nameField);
		
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
        
        group.add(ngoRadio);
        group.add(fundingRadio);
        group.add(partnerRadio);
        
        
        add(ngoRadio);
        add(ngoIcon);
        add(fundingRadio);
        add(fundingIcon);
        add(partnerRadio);
        add(partnerIcon);
		
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
		// Create button.
        final Button createButton = new Button(I18N.CONSTANTS.save());
        createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                createProjectModel(callback);
            }
        });
        add(createButton);
	}
	
	private void createProjectModel(final AsyncCallback<CreateResult> callback) {
		 if (!this.isValid()) {
			 MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
	                    I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminProjectModelStandard()), null);
	            return;
		 }
		 final String name = nameField.getValue();
		 
		 newProjectModelProperties = new HashMap<String, Object>();
		 newProjectModelProperties.put(AdminUtil.PROP_PM_USE, currentModelType);
		 newProjectModelProperties.put(AdminUtil.PROP_PM_NAME, name);   
		 ProjectModelDTO model = new ProjectModelDTO();
		 model.setStatus(ProjectModelStatus.DRAFT);
		 newProjectModelProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, model);
		 
        dispatcher.execute(new CreateEntity("ProjectModel", newProjectModelProperties), null, new AsyncCallback<CreateResult>(){

       	 public void onFailure(Throwable caught) {
       		MessageBox.alert(I18N.CONSTANTS.adminProjectModelCreationBox(), 
          			I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminProjectModelStandard()
								+ " '" + name + "'"), null);
             	callback.onFailure(caught);
             }

			@Override
			public void onSuccess(CreateResult result) {
				if(result != null && result.getEntity() != null){						
					callback.onSuccess(result);		
					
					Notification.show(I18N.CONSTANTS.adminProjectModelCreationBox(), I18N.MESSAGES.adminStandardCreationSuccess(I18N.CONSTANTS.adminProjectModelStandard()
							+ " '" + name +"'"));					
				}					
				else{
					Throwable t = new Throwable("ProjectModelForm : creation result is null");
					callback.onFailure(t);
					MessageBox.alert(I18N.CONSTANTS.adminProjectModelCreationBox(), 
		          			I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminProjectModelStandard()
										+ " '" + name + "'"), null);
				}		
			}
        });
		 
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
	
	public HashMap<String, Object> getPrivacyGroupsProperties(){
		return newProjectModelProperties;
	}
}
