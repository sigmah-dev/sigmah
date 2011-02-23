package org.sigmah.client.page.admin.users;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.users.AdminUsersPresenter.View;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.config.form.UserSigmahForm;
import org.sigmah.shared.command.DeactivateUsers;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.UserDTO;

import org.sigmah.client.dispatch.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminUsersActionListener implements ActionListener {
	
	private final View view;
	private final Dispatcher dispatcher;
	
	public AdminUsersActionListener(View view, Dispatcher dispatcher){
		this.view = view;
		this.dispatcher = dispatcher;
	}

	@Override
	public void onUIAction(String actionId) {
		if (UIActions.delete.equals(actionId)) {
            view.confirmDeleteSelected(new ConfirmCallback() {
                public void confirmed() {
                    onDeleteConfirmed(view.getUsersSelection());
                }
            });
        } else if (UIActions.add.equals(actionId)) {
            onAdd();
        } else if(UIActions.refresh.equals(actionId)){
        	onRefresh();
        }
		
	}
	
	protected void onDeleteConfirmed(final List<UserDTO> selection) {
		
		List<Integer> ids = new ArrayList<Integer>();
		String names = "";
		for(UserDTO s : selection){
			ids.add(s.getId());
			names = s.getName() + ", " + names;
		}
		
		final String toDelete = names;
		final DeactivateUsers deactivate = new DeactivateUsers(selection);
        dispatcher.execute(deactivate, null, new AsyncCallback<VoidResult>() {

            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
            }

            @Override
            public void onSuccess(VoidResult result) {
            	for(UserDTO model : selection){
            		model.setActive(!model.getActive());
            		view.getAdminUsersStore().update(model);
            	}    
            }
        });
	}

	protected void onRefresh() {
		AdminUsersPresenter.refreshUserPanel(dispatcher, view);		
	}
	
	protected void onAdd() {

		final Window window = new Window();
		
		final UserSigmahForm form = view.showNewUserForm(window, new AsyncCallback<CreateResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				window.hide();
				
			}

			@Override
			public void onSuccess(CreateResult result) {
				window.hide();
				//refresh view
				view.getAdminUsersStore().add((UserDTO)result.getEntity());
				view.getAdminUsersStore().commitChanges();
			}			
		}, null);
		
        
        window.add(form);
        window.show();

    }
}
