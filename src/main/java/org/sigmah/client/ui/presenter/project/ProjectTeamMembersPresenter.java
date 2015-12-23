package org.sigmah.client.ui.presenter.project;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.view.project.ProjectTeamMembersView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.shared.command.GetProfiles;
import org.sigmah.shared.command.GetProjectTeamMembers;
import org.sigmah.shared.command.GetUsersByOrgUnit;
import org.sigmah.shared.command.UpdateProjectTeamMembers;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dto.TeamMemberDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

/**
 * Project's details presenter which manages the {@link ProjectTeamMembersView}.
 *
 * @author Aurélien PONÇON (aurelien.poncon@gmail.com)
 */
@Singleton
public class ProjectTeamMembersPresenter extends AbstractProjectPresenter<ProjectTeamMembersPresenter.View> {
	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectTeamMembersView.class)
	public interface View extends AbstractProjectPresenter.View {
		LayoutContainer getMainPanel();

		Button getSaveButton();

		Button getAddTeamMemberButton();

		Button getAddTeamMemberByProfileButton();

		ListStore<ModelData> getTeamMembersStore();

		void setRemoveTeamMemberButtonCreationHandler(RemoveTeamMemberButtonCreationHandler removeTeamMemberButtonCreationHandler);

		void buildAddTeamMemberDialog(AddTeamMemberHandler handler, List<UserDTO> availableUsers);

		void buildAddTeamMembersByProfileDialog(SelectTeamMembersByProfileHandler handler, List<ProfileDTO> profiles);
	}

	public interface RemoveTeamMemberButtonCreationHandler {
		void onCreateRemoveUserButton(Button button, UserDTO userDTO);
		void onCreateRemoveProfileButton(Button button, ProfileDTO userDTO);
	}

	public interface AddTeamMemberHandler {
		void onAddTeamMember(UserDTO userDTO);
	}

	public interface SelectTeamMembersByProfileHandler {
		void onSelectTeamMemberByProfile(ProfileDTO profileDTO);
	}

	private boolean modified;

	/**
	 * Presenters's initialization.
	 *
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ProjectTeamMembersPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.PROJECT_TEAM_MEMBERS;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		load();

		modified = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasValueChanged() {
		return modified;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Loads the presenter with the given project {@code team members}.
	 */
	private void load() {
		view.getTeamMembersStore().removeAll();

		dispatch.execute(new GetProjectTeamMembers(getProject().getId()), new CommandResultHandler<TeamMembersResult>() {
			@Override
			protected void onCommandSuccess(TeamMembersResult result) {
				fillTeamMembersStore(result);
			}
		}, null, new LoadingMask(view.getMainPanel()));
	}

	@Override
	public void onBind() {
		super.onBind();

		view.setRemoveTeamMemberButtonCreationHandler(new RemoveTeamMemberButtonCreationHandler() {
			@Override
			public void onCreateRemoveUserButton(Button button, final UserDTO userDTO) {
				// TODO: Verify if the user is allowed to update team members
				button.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent event) {
						view.getTeamMembersStore().remove(userDTO);
						modified = true;
						view.getSaveButton().setEnabled(true);
					}
				});
			}

			@Override
			public void onCreateRemoveProfileButton(Button button, final ProfileDTO profileDTO) {
				// TODO: Verify if the user is allowed to update team members
				button.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent event) {
						view.getTeamMembersStore().remove(profileDTO);
						modified = true;
						view.getSaveButton().setEnabled(true);
					}
				});
			}
		});

		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			@SuppressWarnings("unchecked")
			public void componentSelected(ButtonEvent event) {
				List<UserDTO> teamMembers = (List<UserDTO>) (List) view.getTeamMembersStore().findModels(TeamMemberDTO.TYPE,
						TeamMemberDTO.TeamMemberType.TEAM_MEMBER);
				List<ProfileDTO> teamMemberProfiles = (List<ProfileDTO>) (List)view.getTeamMembersStore().findModels(TeamMemberDTO.TYPE,
						TeamMemberDTO.TeamMemberType.TEAM_MEMBER_PROFILE);
				dispatch.execute(
						new UpdateProjectTeamMembers(getProject().getId(), teamMembers, teamMemberProfiles),
						new CommandResultHandler<TeamMembersResult>() {

							@Override
							public void onCommandFailure(final Throwable caught) {
								N10N.error(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError());
							}

							@Override
							protected void onCommandSuccess(TeamMembersResult result) {
								N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.saveConfirm());

								fillTeamMembersStore(result);
							}
						},
						view.getSaveButton(), new LoadingMask(view.getMainPanel())
				);
				modified = false;
				view.getSaveButton().setEnabled(false);
			}
		});

		view.getAddTeamMemberButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Set<Integer> alreadySelectedUserIds = new HashSet<Integer>();
				for (ModelData modelData : view.getTeamMembersStore().findModels(TeamMemberDTO.TYPE, TeamMemberDTO.TeamMemberType.TEAM_MEMBER)) {
					alreadySelectedUserIds.add((Integer) modelData.get(TeamMemberDTO.ID));
				}
				dispatch.execute(
						new GetUsersByOrgUnit(getProject().getOrgUnitId(), alreadySelectedUserIds),
						new CommandResultHandler<ListResult<UserDTO>>() {
							@Override
							protected void onCommandSuccess(ListResult<UserDTO> results) {
								view.buildAddTeamMemberDialog(new AddTeamMemberHandler() {
									@Override
									public void onAddTeamMember(UserDTO userDTO) {
										userDTO.set(TeamMemberDTO.TYPE, TeamMemberDTO.TeamMemberType.TEAM_MEMBER);
										userDTO.set(TeamMemberDTO.ORDER, 3);

										view.getTeamMembersStore().add(userDTO);
										modified = true;
										view.getSaveButton().setEnabled(true);
									}
								}, results.getData());
							}
						},
						view.getAddTeamMemberButton(), new LoadingMask(view.getMainPanel())
				);
			}
		});

		view.getAddTeamMemberByProfileButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent event) {
				dispatch.execute(
						new GetProfiles(ProfileDTO.Mode.BASE),
						new CommandResultHandler<ListResult<ProfileDTO>>() {
							@Override
							protected void onCommandSuccess(ListResult<ProfileDTO> result) {
								view.buildAddTeamMembersByProfileDialog(new SelectTeamMembersByProfileHandler() {
									@Override
									public void onSelectTeamMemberByProfile(ProfileDTO profileDTO) {
										profileDTO.set(TeamMemberDTO.TYPE, TeamMemberDTO.TeamMemberType.TEAM_MEMBER_PROFILE);
										profileDTO.set(TeamMemberDTO.ORDER, 2);

										view.getTeamMembersStore().add(profileDTO);
										modified = true;
										view.getSaveButton().setEnabled(true);
									}
								}, result.getData());
							}
						}, view.getAddTeamMemberByProfileButton(), new LoadingMask(view.getMainPanel())
				);
			}
		});
	}

	private void fillTeamMembersStore(TeamMembersResult result) {
		UserDTO projectManager = result.getProjectManager();
		// As the ListStore doesn't support duplicated IDs, let's modify the ID of the manager
		projectManager.set(TeamMemberDTO.ID, Integer.MAX_VALUE);
		projectManager.set(TeamMemberDTO.TYPE, TeamMemberDTO.TeamMemberType.MANAGER);
		projectManager.set(TeamMemberDTO.ORDER, 1);

		List<ProfileDTO> teamMemberProfiles = result.getTeamMemberProfiles();
		for (ProfileDTO profileDTO : teamMemberProfiles) {
			profileDTO.set(TeamMemberDTO.TYPE, TeamMemberDTO.TeamMemberType.TEAM_MEMBER_PROFILE);
			profileDTO.set(TeamMemberDTO.ORDER, 2);
		}

		List<UserDTO> teamMembers = result.getTeamMembers();
		for (UserDTO userDTO : teamMembers) {
			userDTO.set(TeamMemberDTO.TYPE, TeamMemberDTO.TeamMemberType.TEAM_MEMBER);
			userDTO.set(TeamMemberDTO.ORDER, 3);
		}

		view.getTeamMembersStore().removeAll();
		view.getTeamMembersStore().add(projectManager);
		view.getTeamMembersStore().add(teamMemberProfiles);
		view.getTeamMembersStore().add(teamMembers);
	}
}
