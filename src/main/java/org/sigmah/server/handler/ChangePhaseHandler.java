package org.sigmah.server.handler;

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

		// Closes the current phase.
		currentPhase.setEndDate(new Date());

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
			newCurrentPhase.setStartDate(new Date());
			project.setCurrentPhase(newCurrentPhase);
		}

		LOG.debug("Saves modifications.");

		// Saves the new project state.
		projectDAO.persist(project, context.getUser());

		return mapper().map(project, ProjectDTO.class);
	}
}
