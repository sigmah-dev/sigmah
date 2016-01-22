package org.sigmah.server.handler;

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
