package org.sigmah.server.handler;

import java.util.HashSet;

import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.ProjectMapper;
import org.sigmah.shared.command.UpdateProjectFavorite;
import org.sigmah.shared.command.UpdateProjectFavorite.UpdateType;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler for {@link UpdateProjectFavorite} command
 * 
 * @author HUZHE
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateProjectFavoriteHandler extends AbstractCommandHandler<UpdateProjectFavorite, CreateResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UpdateProjectFavoriteHandler.class);

	@Inject
	private ProjectDAO projectDAO;

	@Inject
	private ProjectMapper projectMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CreateResult execute(final UpdateProjectFavorite cmd, final UserExecutionContext context) throws CommandException {

		final User user = context.getUser();
		final Integer projectId = cmd.getProjectId();

		LOG.debug("User: '{}' ; Project id: '{}'.", user, projectId);

		final Project project = projectDAO.findById(projectId);

		if (project == null || user == null) {
			throw new CommandException("Invalid command arguments.");
		}

		if (cmd.getUpdateType() == UpdateType.REMOVE) {

			if (project.getFavoriteUsers() == null) {
				throw new CommandException("Project does not possess favorite users ; user cannot be removed.");
			}

			if (!project.getFavoriteUsers().contains(user)) {
				throw new CommandException("User is not present among project favorite users ; it cannot be removed.");
			}

			project.getFavoriteUsers().remove(user);

		} else {
			if (project.getFavoriteUsers() == null) {
				project.setFavoriteUsers(new HashSet<User>());
			}

			project.getFavoriteUsers().add(user);
		}

		final Project resultProject = projectDAO.persist(project, user);
		final ProjectDTO resultProjectDTOLight = projectMapper.map(resultProject, false);

		return new CreateResult(resultProjectDTOLight);

	}
}
