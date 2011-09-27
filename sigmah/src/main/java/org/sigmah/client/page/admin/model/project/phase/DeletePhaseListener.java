/**
 * 
 */
package org.sigmah.client.page.admin.model.project.phase;


import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Listener to response the click event for deleting a phase model
 * 
 * @author HUZHE(zhe.hu32@gmail.com)
 *
 */
public class DeletePhaseListener implements Listener<ButtonEvent>{

	private final PhaseModelDTO model;
	private final Dispatcher dispatcher;
	//private ListStore<PhaseModelDTO> phaseStore;
	protected ProjectModelDTO projectModel;
	private AdminPhasesPresenter.View view;
	
	/**
	 * @param model
	 */
	public DeletePhaseListener(PhaseModelDTO model, Dispatcher dispatcher, AdminPhasesPresenter.View view) {
		this.model = model;
		this.dispatcher = dispatcher;
		//this.phaseStore = phaseStore;
		this.view = view;
	}

	@Override
	public void handleEvent(ButtonEvent be) {
	
		//Current project model can be null
	   if(model.getParentProjectModelDTO()==null)
		   return;
	   
	   //Only the phase in draft project model is allowed to delete
	   ProjectModelDTO currentPojectModel = model.getParentProjectModelDTO();
	   if(currentPojectModel.getStatus()==null|| !(currentPojectModel.getStatus().equals(ProjectModelStatus.DRAFT)))
		   return;
	   	   
	   //Can not delete root phase
	   if(view.getProjectModel()!=null && view.getProjectModel().getRootPhaseModelDTO()!=null)
	   {
		  if(view.getProjectModel().getRootPhaseModelDTO().getId()==model.getId())
		  {
		   MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.CONSTANTS.deleteRootPhaseModelError(), null);
		   return;
		  }
	   }
	   

	  //Show the confirm messageBox
	   Listener<MessageBoxEvent> l=new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(
					MessageBoxEvent be) {
				if (Dialog.YES.equals(be
						.getButtonClicked()
						.getItemId())) {
					deletePhaseModel();
				}
			}
		};
		
		String confirmMessageDetials=I18N.CONSTANTS.deletePhaseModelConfirm();
		
		//ProjectModelDTO parentProjectModel = model.getParentProjectModelDTO();
		
		ProjectModelDTO parentProjectModel = view.getProjectModel();
		
	    if(parentProjectModel!=null && (parentProjectModel.getPhaseModelsDTO()==null ||parentProjectModel.getPhaseModelsDTO().size()==1))
	    {//In that case, this is the last phase
	    	
	    	confirmMessageDetials = I18N.CONSTANTS.deleteLastPhaseModelConfirm();
	    }
		
		MessageBox deleteConfirmMsgBox = MessageBox.confirm(I18N.CONSTANTS.deleteConfirm(), confirmMessageDetials, l);		
		deleteConfirmMsgBox.show();
		
		
	}


	
	
	
 private void deletePhaseModel()
 {
	 
	//Begins to RPC to delete
	   Delete deleteCommand = new Delete("PhaseModel",model.getId());
	   dispatcher.execute(deleteCommand, null, new AsyncCallback<VoidResult>(){

		@Override
		public void onFailure(Throwable caught) {
			
			MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(model.getName()), null);
			
		}

		@Override
		public void onSuccess(VoidResult result) {
			
			//Refresh the all phase models' successors
			RefreshSuccessors(model);
			
			//Refresh the phase model
			view.getPhaseStore().remove(model);
			view.getPhaseStore().commitChanges();
			
			//Refresh the project model
			view.getProjectModel().getPhaseModelsDTO().clear();
			view.getProjectModel().getPhaseModelsDTO().addAll(view.getPhaseStore().getModels());
			
			
			// Show notification.
			Notification.show(I18N.CONSTANTS
					.deleteConfirm(),
					I18N.CONSTANTS.adminPhaseModelDeleteDetail());
			
		}
		   
	   });
	 
 }
 
/**
 * 
 * Method to reset the phase models's successors after a model is deleted .
 * 
 * @param model
 * 
 * @return
 * 
 * @author HUZHE(zhe.hu32@gmail.com)
 */
private void RefreshSuccessors(PhaseModelDTO model)
 {
	//Get all phase models in the grid store
	List<PhaseModelDTO> currentPhaseModels = view.getPhaseStore().getModels();
	
	//Check if the deleted model is other phase models' successors,if so, delete that relation
	for(PhaseModelDTO p: currentPhaseModels)
	{
		if(p.getSuccessorsDTO()!=null && p.getSuccessorsDTO().size()!=0)
		{
	        if(p.getSuccessorsDTO().contains(model))
	        {
	        	p.getSuccessorsDTO().remove(model);
	        	view.getPhaseStore().update(p);
	        	view.getPhaseStore().commitChanges();
	        }
		}
	}
	
 }

	
}
