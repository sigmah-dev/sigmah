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

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.TypedQuery;
import org.sigmah.server.dao.ProjectFundingDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetValueFromLinkedProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link GetValueFromLinkedProjects} command.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class GetValueFromLinkedProjectsHandler extends AbstractCommandHandler<GetValueFromLinkedProjects, ListResult<String>> {
	
	/**
	 * Injected project project funding DAO.
	 */
	@Inject
	private ProjectFundingDAO projectFundingDAO;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListResult<String> execute(GetValueFromLinkedProjects command, UserDispatch.UserExecutionContext context) throws CommandException {
		final List<ProjectFunding> fundings = projectFundingDAO.getLinkedProjects(command.getProjectId(), command.getType());
		
		final List<Integer> containerIds = new ArrayList<>();
		for (final ProjectFunding funding : fundings) {
			final Project project;
			switch (command.getType()) {
				case FUNDED_PROJECT:
					project = funding.getFunded();
					break;
				case FUNDING_PROJECT:
					project = funding.getFunding();
					break;
				default:
					throw new CommandException("Unsupported linked project type: " + command.getType());
			}
			containerIds.add(project.getId());
		}
		
		if (containerIds.isEmpty()) {
			return new ListResult<>(Collections.<String>emptyList());
		}

		final TypedQuery<String> query = em().createQuery("SELECT v.value FROM Value v WHERE v.containerId IN :containerIds AND v.element.id = :elementId", String.class);
		query.setParameter("containerIds", containerIds);
		query.setParameter("elementId", command.getElementId());

		return new ListResult<>(new ArrayList<>(query.getResultList()));
	}
	
}
