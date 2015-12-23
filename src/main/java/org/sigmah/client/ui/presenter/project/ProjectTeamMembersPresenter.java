package org.sigmah.client.ui.presenter.project;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.view.project.ProjectTeamMembersView;
import org.sigmah.shared.command.GetProjectTeamMembers;
import org.sigmah.shared.command.result.TeamMembersResult;
import org.sigmah.shared.dto.TeamMemberDTO;
import org.sigmah.shared.dto.UserDTO;

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

		ListStore<ModelData> getTeamMembersStore();
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

	private void fillTeamMembersStore(TeamMembersResult result) {
		UserDTO projectManager = result.getProjectManager();
		// As the ListStore doesn't support duplicated IDs, let's modify the ID of the manager
		projectManager.set(TeamMemberDTO.ID, Integer.MAX_VALUE);
		projectManager.set(TeamMemberDTO.TYPE, TeamMemberDTO.TeamMemberType.MANAGER);
		projectManager.set(TeamMemberDTO.ORDER, 1);

		List<UserDTO> teamMembers = result.getTeamMembers();
		for (UserDTO userDTO : teamMembers) {
			userDTO.set(TeamMemberDTO.TYPE, TeamMemberDTO.TeamMemberType.TEAM_MEMBER);
			userDTO.set(TeamMemberDTO.ORDER, 3);
		}

		view.getTeamMembersStore().removeAll();
		view.getTeamMembersStore().add(projectManager);
		view.getTeamMembersStore().add(teamMembers);
	}
}
