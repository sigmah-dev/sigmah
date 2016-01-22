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

import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectsByModel;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Handler for {@link GetProjectsByModel} command.
 * 
 * @author HUZHE (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectsByModelHandler extends AbstractCommandHandler<GetProjectsByModel, ListResult<ProjectDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ProjectDTO> execute(final GetProjectsByModel cmd, final UserExecutionContext context) throws CommandException {

		if (cmd == null || cmd.getProjectModelId() == null) {
			throw new CommandException("Invalid command arguments");
		}

		final TypedQuery<Project> query = em().createQuery("SELECT p FROM Project p WHERE p.projectModel.id = :projectId", Project.class);
		query.setParameter("projectId", cmd.getProjectModelId());

		final List<Project> projects = query.getResultList();

		return new ListResult<>(mapper().mapCollection(projects, ProjectDTO.class, cmd.getMappingMode()));
	}

}
