package org.sigmah.client.page.admin.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.users.AdminUsersPresenter.View;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.admin.users.form.PrivacyGroupSigmahForm;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.DeletePrivacyGroups;
import org.sigmah.shared.command.GetOrgUnitModels;
import org.sigmah.shared.command.GetProfilesWithDetails;
import org.sigmah.shared.command.GetProjectModels;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.OrgUnitModelListResult;
import org.sigmah.shared.command.result.ProfileWithDetailsListResult;
import org.sigmah.shared.command.result.ProjectModelListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.domain.profile.PrivacyGroupPermissionEnum;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

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

				@Override
				public void confirmed() {
					onDeleteConfirmed(view.getPrivacyGroupsSelection());					
				}
			},"PrivacyGroup");
		}else if (UIActions.add.equals(actionId)) {
			onAdd();
		}

	}

	protected void onDeleteConfirmed(final List<PrivacyGroupDTO> selection) {
		final StringBuilder sb = new StringBuilder();
		final ArrayList<PrivacyGroupDTO> privacyGroupsToDelete = new ArrayList<PrivacyGroupDTO>();
		final ArrayList<ProjectModelDTO> projectModels = new ArrayList<ProjectModelDTO>();
		final ArrayList<OrgUnitModelDTO> orgUnitModels = new ArrayList<OrgUnitModelDTO>();
		final ArrayList<ProfileDTO> profiles =  new ArrayList<ProfileDTO>();

		final StringBuilder sbnames = new StringBuilder();
		for(PrivacyGroupDTO pgs : selection){
			sbnames.append(pgs.getTitle());
			sbnames.append(", ");
		}
		
		final GetProjectModels cmd = new GetProjectModels();
		cmd.setFullVersion(true);
		dispatcher.execute(cmd , view.getPrivacyGroupsLoadingMonitor(), new AsyncCallback<ProjectModelListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(sbnames.toString()), null);
			}

			@Override
			public void onSuccess(ProjectModelListResult result) {
				projectModels.addAll(result.getFullVersionModelList());

				dispatcher.execute(new GetOrgUnitModels(), view.getPrivacyGroupsLoadingMonitor(), new AsyncCallback<OrgUnitModelListResult>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(sbnames.toString()), null);
					}

					@Override
					public void onSuccess(OrgUnitModelListResult result) {
						orgUnitModels.addAll(result.getList());

						dispatcher.execute(new GetProfilesWithDetails(), view.getPrivacyGroupsLoadingMonitor(), new AsyncCallback<ProfileWithDetailsListResult>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(sbnames.toString()), null);
							}

							@Override
							public void onSuccess(ProfileWithDetailsListResult result) {
								profiles.addAll(result.getList());

								String elementNamesList = "";
								boolean canBeDeleted = true;


								for(final PrivacyGroupDTO selectedPrivacyGroup : selection) {
									//checks if there are elements with the privacy group to delete
									for(ProjectModelDTO projectModel : projectModels) {
										for(FlexibleElementDTO flexibleElement : projectModel.getAllElements()){
											if( flexibleElement.getPrivacyGroup() != null && flexibleElement.getPrivacyGroup().getId() == selectedPrivacyGroup.getId()){
												canBeDeleted = false;
												if(flexibleElement instanceof DefaultFlexibleElementDTO){
													elementNamesList += DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO)flexibleElement).getType()) + ", ";
												}
												elementNamesList += flexibleElement instanceof DefaultFlexibleElementDTO? I18N.CONSTANTS.flexibleElementDefault() :  flexibleElement.getElementLabel() + " ( Model : " + projectModel.getName()+ "), ";
											} 
										}
									}

									for(OrgUnitModelDTO orgUnitModel : orgUnitModels) {
										for(FlexibleElementDTO flexibleElement : orgUnitModel.getAllElements()){
											if( flexibleElement.getPrivacyGroup() != null && flexibleElement.getPrivacyGroup().getId() == selectedPrivacyGroup.getId()){
												canBeDeleted = false;
												elementNamesList += flexibleElement.getLabel() + " ( OrgUnit : " + orgUnitModel.getName() + "), ";
											} 
										}
									}
									if(!elementNamesList.isEmpty()){
										elementNamesList = elementNamesList.substring(0, elementNamesList.lastIndexOf(", "));
									}

									String profilesNamesList = "";
									//Checks if there aren't profile using the privacy group
									for(ProfileDTO profile : profiles) {
										for (Entry<PrivacyGroupDTO, PrivacyGroupPermissionEnum> entry : profile.getPrivacyGroups().entrySet()){
											if( entry.getKey().getId() == selectedPrivacyGroup.getId()){
												canBeDeleted = false;
												profilesNamesList +=  profile.getName() + ", ";
											} 
										}
									}

									if(!profilesNamesList.isEmpty()){
										profilesNamesList = profilesNamesList.substring(0, profilesNamesList.lastIndexOf(", "));
									}

									//If the privacy group isn't linked to anything, it can be deleted
									if( canBeDeleted) {
										privacyGroupsToDelete.add(selectedPrivacyGroup);
										sb.append(selectedPrivacyGroup.getTitle() + ", ");
									}else {
										MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.adminPrivacyGroupsWarnFieldsLinked(elementNamesList, profilesNamesList, selectedPrivacyGroup.getTitle()), null);			
									}
								}

								if(privacyGroupsToDelete.size() != 0){
									dispatcher.execute(new DeletePrivacyGroups(privacyGroupsToDelete), view.getPrivacyGroupsLoadingMonitor(), new AsyncCallback<VoidResult>() {

										@Override
										public void onFailure(Throwable caught) {
											MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(sb.toString()), null);

										}

										@Override
										public void onSuccess(VoidResult result) {
											Notification.show(I18N.CONSTANTS.infoConfirmation(),
													I18N.CONSTANTS.adminPrivacyGroupsDeleteSuccess());
											for (PrivacyGroupDTO privacyGroup : privacyGroupsToDelete){
												view.getAdminPrivacyGroupsStore().remove(privacyGroup);
											}
										}
									});
								}
							}

						} );
					}

				} );
			}

		} );


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
