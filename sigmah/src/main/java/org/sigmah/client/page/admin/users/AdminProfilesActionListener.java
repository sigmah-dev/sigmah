package org.sigmah.client.page.admin.users;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.users.AdminUsersPresenter.View;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.config.form.ProfileSigmahForm;
import org.sigmah.shared.command.DeleteList;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.profile.Profile;
import org.sigmah.shared.dto.profile.ProfileDTO;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminProfilesActionListener implements ActionListener {
	private final View view;
	private final Dispatcher dispatcher;
	
	public AdminProfilesActionListener(View view, Dispatcher dispatcher){
		this.view = view;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void onUIAction(String actionId) {
		/*if (UIActions.delete.equals(actionId)) {
            view.confirmDeleteSelected(new ConfirmCallback() {
                public void confirmed() {
                    onDeleteConfirmed(view.getProfilesSelection());
                }
            });
        } else*/ if (UIActions.add.equals(actionId)) {
            onAdd();
        }		
	}
	
	/*protected void onDeleteConfirmed(final List<ProfileDTO> selection) {
		
		List<Integer> ids = new ArrayList<Integer>();
		String names = "";
		for(ProfileDTO s : selection){
			ids.add(s.getId());
			names = s.getName() + ", " + names;
		}
		
		final String toDelete = names;
		final DeleteList delete = new DeleteList(Profile.class, ids);
        dispatcher.execute(delete, null, new AsyncCallback<VoidResult>() {

            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
            }

            @Override
            public void onSuccess(VoidResult result) {
            	for(ProfileDTO model : selection){
            		 view.getAdminProfilesStore().remove(model);
            	}    
            }
        });
	}*/
	
	private void onAdd() {
		final Window window = new Window();
		
		final ProfileSigmahForm form = view.showNewProfileForm(window, new AsyncCallback<CreateResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				window.hide();
				
			}

			@Override
			public void onSuccess(CreateResult result) {
				window.hide();
				view.getAdminProfilesStore().add((ProfileDTO)result.getEntity());
				view.getAdminProfilesStore().commitChanges();
			}			
		}, null);
		
        
        window.add(form);
        window.show();
		
	}
}
