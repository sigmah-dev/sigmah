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


import java.util.Date;

import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Phase;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.ChangePhase;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The handler for {@link ChangePhase} command.
 * 
 * @author tmi (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ChangePhaseHandler extends AbstractCommandHandler<ChangePhase, ProjectDTO> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(ChangePhaseHandler.class);

	/**
	 * Injected {@link ProjectDAO}.
	 */
	@Inject
	private ProjectDAO projectDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectDTO execute(final ChangePhase cmd, final UserExecutionContext context) throws CommandException {

		// Gets the project.
		final Project project = projectDAO.findById(cmd.getProjectId());

		// Gets the current phase.
		final Phase currentPhase = project.getCurrentPhase();

		LOG.debug("Closing the current phase #{}.", currentPhase.getId());

		final Date date = new Date();
		
		// Closes the current phase.
		currentPhase.setEndDate(date);

		// If the id of the phase to activate isn't null, activates it.
		if (cmd.getPhaseId() != null) {

			LOG.debug("Try to activate phase #{}.", cmd.getPhaseId());

			// Searches for the given phase phase.
			Phase newCurrentPhase = null;
			for (final Phase phase : project.getPhases()) {
				if (phase.getId().equals(cmd.getPhaseId())) {
					newCurrentPhase = phase;
				}
			}

			if (newCurrentPhase == null) {
				// The activated phase cannot be found: error.
				LOG.error("The phase with id #{} doesn't exist.", cmd.getPhaseId());
				throw new CommandException("The phase to activate doesn't exist.");
			}

			LOG.debug("Activates the new phase #{}.", cmd.getPhaseId());

			// Activates the new phase.
			newCurrentPhase.setStartDate(date);
			project.setCurrentPhase(newCurrentPhase);
			
		} else {
			project.setCloseDate(date);
		}

		LOG.debug("Saves modifications.");

		// Saves the new project state.
		projectDAO.persist(project, context.getUser());

		return mapper().map(project, ProjectDTO.class);
	}
}
