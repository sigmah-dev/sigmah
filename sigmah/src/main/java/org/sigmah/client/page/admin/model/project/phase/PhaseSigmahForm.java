/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.admin.model.project.phase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.project.phase.AdminPhasesPresenter.View;
import org.sigmah.client.util.Notification;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.UpdateModelResult;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

/**
 * Create profile form.
 * 
 * @author nrebiai
 * 
 */
public class PhaseSigmahForm extends FormPanel {
	
	

	private final TextField<String> nameField;
	private final PhaseModelDTO phaseToUpdate;
	private final ProjectModelDTO projectModelToUpdate;
	private final View view;
	private final List<CheckBoxGroup> candidatesSuccessorsPhases;
	private final List<PhaseModelDTO> candidatesPhases = new ArrayList<PhaseModelDTO>();
	private final CheckBox isRoot;
	private final TextField<String> guideField;
	
	//FIXME private final NumberField rowNumberField;
	private final NumberField orderField;

	private final Dispatcher dispatcher;
	
	private final static int LABEL_WIDTH = 90;
	
	public PhaseSigmahForm(View view, Dispatcher dispatcher, 
			final AsyncCallback<UpdateModelResult> callback, PhaseModelDTO phaseToUpdate, ProjectModelDTO projectModelToUpdate) {
		this.view = view;
		this.dispatcher = dispatcher;
		this.phaseToUpdate = phaseToUpdate;
		this.projectModelToUpdate = projectModelToUpdate;
		
		UIConstants constants = GWT.create(UIConstants.class);
		
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		setLayout(layout);
		
		nameField = new TextField<String>();
		nameField.setFieldLabel(constants.adminPhaseName());
		nameField.setAllowBlank(false);
		if(phaseToUpdate != null && !phaseToUpdate.getName().isEmpty())
			nameField.setValue(phaseToUpdate.getName());
		add(nameField);
		
		/* ************************************Successors phases ********************************************/
		
		
		
		candidatesSuccessorsPhases = new ArrayList<CheckBoxGroup>();
		CheckBoxGroup checkphasesGroup = new CheckBoxGroup();
		checkphasesGroup.setOrientation(Orientation.VERTICAL);
		checkphasesGroup.setFieldLabel(I18N.CONSTANTS.adminPhaseSuccessors());
		String label = "";
		for(Map.Entry<String, PhaseModelDTO> phaseModel : view.getPhases().entrySet()){	
			Boolean isSuccessor = false;
			label = phaseModel.getKey();	
			CheckBox box = createCheckBox(label,label);
			if(phaseToUpdate != null){
				if(phaseToUpdate.getSuccessorsDTO()!=null
						&& phaseToUpdate.getSuccessorsDTO().contains(phaseModel.getValue())){
					box.setValue(true);
					checkphasesGroup.add(box);
					isSuccessor = true;
					candidatesPhases.add(phaseModel.getValue());
				}
				if(!isSuccessor 
						&& !view.getSuccessorsPhases().contains(phaseModel.getKey())
						&& !phaseToUpdate.getName().equals(phaseModel.getKey()) 
						&& !(projectModelToUpdate.getRootPhaseModelDTO()!= null 
								&& projectModelToUpdate.getRootPhaseModelDTO().getId() == phaseModel.getValue().getId())
								){
					checkphasesGroup.add(box);
					candidatesPhases.add(phaseModel.getValue());
				}
					
			}else{
				if(!view.getSuccessorsPhases().contains(phaseModel.getKey())
						&& !(this.projectModelToUpdate.getRootPhaseModelDTO()!= null 
								&& this.projectModelToUpdate.getRootPhaseModelDTO().getId() == phaseModel.getValue().getId())){
					checkphasesGroup.add(box);
					candidatesPhases.add(phaseModel.getValue());
				}
					
			}
		}
		candidatesSuccessorsPhases.add(checkphasesGroup);
		add(checkphasesGroup);

		isRoot = new CheckBox();
		isRoot.setValue(false);
		isRoot.setFieldLabel(constants.adminPhaseModelRoot());
		isRoot.setName("isRoot");
		isRoot.setBoxLabel(constants.adminPhaseModelRoot());
		isRoot.setValue(false);
		if(phaseToUpdate != null){
			if(this.projectModelToUpdate.getRootPhaseModelDTO()!= null && phaseToUpdate.getId() == this.projectModelToUpdate.getRootPhaseModelDTO().getId()){
				isRoot.setValue(true);
			}			
		}
		add(isRoot);
		
		orderField = new NumberField();
		orderField.setAllowBlank(false);
		orderField.setFieldLabel(I18N.CONSTANTS.adminPhaseOrder());
		orderField.clear();
		if(phaseToUpdate != null){
			orderField.setValue(phaseToUpdate.getDisplayOrder());
		}
		add(orderField);
		
		//Guide field
		guideField = new TextField<String>();
		guideField.setFieldLabel(I18N.CONSTANTS.projectPhaseGuideHeader());
		guideField.setAllowBlank(true);
		if(phaseToUpdate!=null)
		{
			guideField.setValue(phaseToUpdate.getGuide());
		}
		add(guideField);
		
		/*rowNumberField = new NumberField();
		rowNumberField.setAllowBlank(false);
		rowNumberField.setFieldLabel(constants.adminPhaseModelSize());
		rowNumberField.clear();
		if(phaseToUpdate != null){
			if(phaseToUpdate.getLayoutDTO()!=null){
				rowNumberField.setValue(phaseToUpdate.getLayoutDTO().getRowsCount());
			}			
		}
		add(rowNumberField);*/
		
		// Create button.
        final Button createButton = new Button(I18N.CONSTANTS.save());
        createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                createPhase(callback);
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

	private void createPhase(final AsyncCallback<UpdateModelResult> callback) {
		
		 if (!this.isValid()) {
	            MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
	                    I18N.MESSAGES.createFormIncompleteDetails(I18N.MESSAGES.adminStandardPhase()), null);
	            return;
		 }
		 
		 PhaseModelDTO phaseToSave = new PhaseModelDTO();
		 HashMap<String, Object> newPhaseProperties = new HashMap<String, Object>();
		 
		 if(phaseToUpdate != null){
			 phaseToSave.setId(phaseToUpdate.getId());
		 }
		 //name
		 final String name = nameField.getValue();
		 phaseToSave.setName(name);
		 
		 //successors phases
		 final List<PhaseModelDTO> successors = new ArrayList<PhaseModelDTO>();
		 for(CheckBoxGroup checkGPGroup :candidatesSuccessorsPhases){
			 final List<CheckBox> checkedPhases = checkGPGroup.getValues();
			 for(CheckBox checkedPhase : checkedPhases){	
				 PhaseModelDTO phase = view.getPhases().get(checkedPhase.getName());
				 successors.add(phase);				 
			 }
		 }
		 
		 
		 
		 
		 //rows
		 //final Integer numRows = new Integer(this.rowNumberField.getValue().intValue());
		 
		 //root
		 final Boolean root = isRoot.getValue();
		 
		 //Display order
		 Integer order = null;
		 if(orderField.getValue() != null)
			 order = new Integer(orderField.getValue().intValue());
		 
		 //Guide
		 String guide =null;
		 if( guideField.getValue()!=null)
			 guide = guideField.getValue();
		 
		 phaseToSave.setSuccessorsDTO(successors);
		 newPhaseProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, projectModelToUpdate);
		 newPhaseProperties.put(AdminUtil.PROP_PHASE_MODEL, phaseToSave);
		 newPhaseProperties.put(AdminUtil.PROP_PHASE_ORDER, order);
		 newPhaseProperties.put(AdminUtil.PROP_PHASE_ROOT, root);
		 newPhaseProperties.put(AdminUtil.PROP_PHASE_GUIDE,guide);
		 //newPhaseProperties.put(AdminUtil.PROP_PHASE_ROWS, numRows);
		 newPhaseProperties.put("modelId", new Integer(projectModelToUpdate.getId()));
         dispatcher.execute(new CreateEntity("ProjectModel", newPhaseProperties), null, new AsyncCallback<CreateResult>(){
             public void onFailure(Throwable caught) {
             	MessageBox.alert(I18N.CONSTANTS.adminPhaseCreationBox(), 
             			I18N.MESSAGES.adminStandardCreationFailureF(I18N.MESSAGES.adminStandardPhase()
								+ " '" + name + "'"), null);
             	callback.onFailure(caught);
             }

			@Override
			public void onSuccess(CreateResult result) {
				if(result != null){	
					ProjectModelDTO pModelUpdated = (ProjectModelDTO) result.getEntity();
					UpdateModelResult completeResult = new UpdateModelResult(pModelUpdated.getId());
					completeResult.setEntity(pModelUpdated);	
					view.refreshProjectModel(pModelUpdated);			
					if(phaseToUpdate != null){
						Notification.show(I18N.CONSTANTS.adminPhaseCreationBox(), 
								I18N.MESSAGES.adminStandardUpdateSuccessF(I18N.MESSAGES.adminStandardPhase()
										+ " '" + result.getEntity().get("name")+"'"));
						for(PhaseModelDTO p :pModelUpdated.getPhaseModelsDTO()){
							if(p.getId() == phaseToUpdate.getId()){
								completeResult.setAnnexEntity(p);							
								for(Map.Entry<String, PhaseModelDTO> oldPhase : view.getPhases().entrySet()){
									if(oldPhase.getValue().getId() == p.getId())
										view.getPhases().remove(oldPhase.getValue());
								}
								view.getPhases().put(p.getName(), p);
								for(PhaseModelDTO candidatePhase : candidatesPhases){
									 if(successors.contains(candidatePhase)){
										 if(!view.getSuccessorsPhases().contains(candidatePhase.getName()))
											 view.getSuccessorsPhases().add(candidatePhase.getName());
									 }else{
										 view.getSuccessorsPhases().remove(candidatePhase.getName());
									 }
								 }								
							}
						}
						
					}else{
						Notification.show(I18N.CONSTANTS.adminPhaseCreationBox(), 
								I18N.MESSAGES.adminStandardCreationSuccessF(I18N.MESSAGES.adminStandardPhase()
										+ " '" +result.getEntity().get("name")+"'"));
						for(PhaseModelDTO p :pModelUpdated.getPhaseModelsDTO()){
							if(!projectModelToUpdate.getPhaseModelsDTO().contains(p)){
									completeResult.setAnnexEntity(p);
									view.getPhases().put(p.getName(), p);
							}	
							for(PhaseModelDTO candidatePhase : candidatesPhases){
								 if(successors.contains(candidatePhase)){
									 if(!view.getSuccessorsPhases().contains(candidatePhase.getName()))
										 view.getSuccessorsPhases().add(candidatePhase.getName());
								 }else{
									 view.getSuccessorsPhases().remove(candidatePhase.getName());
								 }
							 }
						}
						
					}					
					callback.onSuccess(completeResult);	
				}					
				else{
					Throwable t = new Throwable("PhaseSigmahForm : creation result is null");
					callback.onFailure(t);
					MessageBox.alert(I18N.CONSTANTS.adminPhaseCreationBox(), 
							I18N.MESSAGES.adminStandardCreationNullF(I18N.MESSAGES.adminStandardPhase()
									+ " '" + name+"'"), null);
				}		
			}
         });
		 
	}
	
}
