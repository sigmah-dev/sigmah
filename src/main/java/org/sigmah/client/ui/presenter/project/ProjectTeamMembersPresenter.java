package org.sigmah.client.ui.presenter.project;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
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
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;

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

		Button getAddTeamMemberButton();

		Button getAddTeamMemberByProfileButton();

		ListStore<ModelData> getTeamMembersStore();

		void setRemoveTeamMemberButtonCreationHandler(RemoveTeamMemberButtonCreationHandler removeTeamMemberButtonCreationHandler);

		void buildAddTeamMemberDialog(AddTeamMemberHandler handler, List<UserDTO> availableUsers);

		void buildAddTeamMembersByProfileDialog(SelectTeamMembersByProfileHandler handler, List<ProfileDTO> profiles);
	}

	public interface RemoveTeamMemberButtonCreationHandler {
		void onCreateRemoveUserButton(Image imageButton, UserDTO userDTO);
		void onCreateRemoveProfileButton(Image imageButton, ProfileDTO userDTO);
	}

	public interface AddTeamMemberHandler {
		void onAddTeamMember(UserDTO userDTO);
	}

	public interface SelectTeamMembersByProfileHandler {
		void onSelectTeamMemberByProfile(ProfileDTO profileDTO);
	}

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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasValueChanged() {
		return false;
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
			public void onCreateRemoveUserButton(Image imageButton, final UserDTO userDTO) {
				if (!isEditable()) {
					imageButton.setVisible(false);
					return;
				}

				imageButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent clickEvent) {
						view.getTeamMembersStore().remove(userDTO);

						save();
					}
				});
			}

			@Override
			public void onCreateRemoveProfileButton(Image imageButton, final ProfileDTO profileDTO) {
				if (!isEditable()) {
					imageButton.setVisible(false);
					return;
				}

				imageButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent clickEvent) {
						view.getTeamMembersStore().remove(profileDTO);

						save();
					}
				});
			}
		});

		addButtonHandlers();
	}

	private void addButtonHandlers() {
		if (!isEditable()) {
			view.getAddTeamMemberButton().setEnabled(false);
			view.getAddTeamMemberByProfileButton().setEnabled(false);
			return;
		}

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

										save();
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

										save();
									}
								}, result.getData());
							}
						}, view.getAddTeamMemberByProfileButton(), new LoadingMask(view.getMainPanel())
				);
			}
		});
	}

	private void save() {
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
				view.getAddTeamMemberButton(), new LoadingMask(view.getMainPanel())
		);
	}

	private void fillTeamMembersStore(TeamMembersResult result) {
		UserDTO projectManager = result.getProjectManager();
		// ProjectManager can be null if the current user doesn't have sufficient UserPermission
		if (projectManager != null) {
			// As the ListStore doesn't support duplicated IDs, let's modify the ID of the manager
			projectManager.set(TeamMemberDTO.ID, Integer.MAX_VALUE);
			projectManager.set(TeamMemberDTO.TYPE, TeamMemberDTO.TeamMemberType.MANAGER);
			projectManager.set(TeamMemberDTO.ORDER, 1);
		}

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
		if (projectManager != null) {
			view.getTeamMembersStore().add(projectManager);
		}
		view.getTeamMembersStore().add(teamMemberProfiles);
		view.getTeamMembersStore().add(teamMembers);
	}

	private boolean isEditable() {
		return ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_PROJECT)
				&& ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_PROJECT_TEAM_MEMBERS);
	}
}
