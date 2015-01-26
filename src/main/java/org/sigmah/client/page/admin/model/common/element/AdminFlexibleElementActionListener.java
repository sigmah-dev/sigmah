package org.sigmah.client.page.admin.model.common.element;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.admin.model.common.element.AdminFlexibleElementsPresenter.View;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.DeleteFlexibleElements;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminFlexibleElementActionListener implements ActionListener {
	private final View view;
	private final Dispatcher dispatcher;
	
	public AdminFlexibleElementActionListener(View view, Dispatcher dispatcher){
		this.view = view;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void onUIAction(String actionId) {
		if (UIActions.delete.equals(actionId)) {
            view.confirmDeleteSelected(new ConfirmCallback() {
                public void confirmed() {
                    onDeleteConfirmed(view.getDeleteSelection());
                }
            });
        }else if (UIActions.add.equals(actionId)) {
            onAdd();
        }else if(UIActions.edit.equals(actionId)) {
            onAddGroup();
        }
		
	}
	
	protected void onDeleteConfirmed(final List<FlexibleElementDTO> selection) {
		String notDeletableNames = "";
		List<Integer> ids = new ArrayList<Integer>();
		String names = "";
		for(FlexibleElementDTO s : selection){
			ids.add(s.getId());
			names = s.getLabel() + ", " + names;
			if(s instanceof DefaultFlexibleElementDTO){
				notDeletableNames += DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO)s).getType()) + ", ";
			}
		}
		
		if("".equals(notDeletableNames)){
			final String toDelete = names;
			final DeleteFlexibleElements delete = new DeleteFlexibleElements(selection);
	        dispatcher.execute(delete, null, new AsyncCallback<VoidResult>() {

	            @Override
	            public void onFailure(Throwable caught) {
	                MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
	            }

	            @Override
	            public void onSuccess(VoidResult result) {
	            	//update view   
	            	for(FlexibleElementDTO s : selection){
	            		view.getFieldsStore().remove(s);
	            	}
	            	
	            	//Feedback 
	            	 Notification.show(
								I18N.CONSTANTS
										.infoConfirmation(),
								I18N.CONSTANTS
										 .adminFlexibleDeleteFlexibleElementsConfirm());
	            	
	            	//FIXME update model
	            }
	        });
		}else{
			notDeletableNames = notDeletableNames.substring(0, notDeletableNames.lastIndexOf(", "));
			MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.adminErrorDeleteDefaultFlexible(notDeletableNames), null);
		}
	}	
	
	private void onAdd() {	
		view.showNewFlexibleElementForm(null,false);		
	}
	
	private void onAddGroup() {	
		view.showNewGroupForm(null, false);
	}
}
