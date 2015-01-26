package org.sigmah.client.page.admin.users;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.users.AdminUsersPresenter.View;
import org.sigmah.client.page.admin.users.form.ProfileSigmahForm;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.DeleteProfiles;
import org.sigmah.shared.command.GetUsersWithProfiles;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.UserListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.UserDTO;
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
		if (UIActions.delete.equals(actionId)) {
			view.confirmDeleteSelected(new ConfirmCallback() {
				public void confirmed() {
					onDeleteConfirmed(view.getProfilesSelection());
				}
			}, "Profile");
		} else if (UIActions.add.equals(actionId)) {
			onAdd();
		}else if(UIActions.refresh.equals(actionId)){
			onRefresh();
		}		
	}

	protected void onRefresh() {
		AdminUsersPresenter.refreshProfilePanel(dispatcher, view);		
	}

	protected void onDeleteConfirmed(final List<ProfileDTO> selection) {
		final StringBuilder sbnames = new StringBuilder();
		for(ProfileDTO pgs : selection){
			sbnames.append(pgs.getName());
			sbnames.append(", ");
		}
		
		dispatcher.execute(new GetUsersWithProfiles(), view.getProfilesLoadingMonitor(), new AsyncCallback<UserListResult>() {
			final List<ProfileDTO> profilesToDelete = new ArrayList<ProfileDTO>();
			final StringBuilder sb = new StringBuilder();
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(sbnames.toString()), null);
			}

			@Override
			public void onSuccess(UserListResult result) {
				String message = "";
				final List<UserDTO> users = result.getList();
				for(final ProfileDTO selectedProfile : selection) {
					boolean canBeDeleted = true;
					for(UserDTO user : users) {
						for(ProfileDTO usersProfile : user.getProfilesDTO()){
							if( usersProfile.getId() == selectedProfile.getId()){
								canBeDeleted = false;
								message += user.getName() + ", " ;
							} 
						}
					}
					
					if(!message.isEmpty()){
						message = message.substring(0, message.lastIndexOf(", "));
					}
					
					if( canBeDeleted) {
						profilesToDelete.add(selectedProfile);
						sb.append(selectedProfile.getName() + ", ");
					}else {
						MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.adminProfilesWarnUsersLinked(message, selectedProfile.getName()), null);			
					}
				}
				
				dispatcher.execute(new DeleteProfiles(profilesToDelete), view.getProfilesLoadingMonitor(), new AsyncCallback<VoidResult>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(sb.toString()), null);
						
					}

					@Override
					public void onSuccess(VoidResult result) {
						Notification.show(I18N.CONSTANTS.infoConfirmation(),
								I18N.CONSTANTS.adminProfilesDeleteSuccess());
						for (ProfileDTO profile : profilesToDelete){
							view.getAdminProfilesStore().remove(profile);
						}
					}
				});
			} 
		});
	}

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
