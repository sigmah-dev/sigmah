package org.sigmah.client.page.admin.users;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.users.AdminUsersPresenter.View;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.config.form.PrivacyGroupSigmahForm;
import org.sigmah.shared.command.DeleteList;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.profile.PrivacyGroup;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminPrivacyGroupsActionListener implements ActionListener {
	private final View view;
	private final Dispatcher dispatcher;
	
	public AdminPrivacyGroupsActionListener(View view, Dispatcher dispatcher){
		this.view = view;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void onUIAction(String actionId) {
		if (UIActions.delete.equals(actionId)) {
            view.confirmDeleteSelected(new ConfirmCallback() {
                public void confirmed() {
                    onDeleteConfirmed(view.getPrivacyGroupsSelection());
                }
            });
        } else if (UIActions.add.equals(actionId)) {
            onAdd();
        }
		
	}
	
	protected void onDeleteConfirmed(final List<PrivacyGroupDTO> selection) {
		
		List<Integer> ids = new ArrayList<Integer>();
		String names = "";
		for(PrivacyGroupDTO s : selection){
			ids.add(s.getId());
			names = s.getTitle() + ", " + names;
		}
		
		final String toDelete = names;
		final DeleteList delete = new DeleteList(PrivacyGroup.class, ids);
        dispatcher.execute(delete, null, new AsyncCallback<VoidResult>() {

            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
            }

            @Override
            public void onSuccess(VoidResult result) {
            	for(PrivacyGroupDTO model : selection){
            		 view.getAdminPrivacyGroupsStore().remove(model);
            	}    
            }
        });
	}
	
	private void onAdd() {
		final Window window = new Window();
		
		final PrivacyGroupSigmahForm form = view.showNewPrivacyGroupForm(window, new AsyncCallback<CreateResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				window.hide();
				
			}

			@Override
			public void onSuccess(CreateResult result) {
				window.hide();
				view.getAdminPrivacyGroupsStore().add((PrivacyGroupDTO)result.getEntity());
				view.getAdminPrivacyGroupsStore().commitChanges();
			}			
		}, null);
		
        
        window.add(form);
        window.show();
		
	}
}
