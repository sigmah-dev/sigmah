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

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.logframe.LogFrame;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.logframe.LogFrameActivityDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import org.sigmah.server.domain.ProjectModel;

/**
 * Handler for update log frame command.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 */
public class UpdateLogFrameHandler extends AbstractCommandHandler<UpdateLogFrame, LogFrameDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UpdateLogFrameHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogFrameDTO execute(final UpdateLogFrame cmd, final UserExecutionContext context) throws CommandException {

		LogFrameDTO logFrameDTO = cmd.getLogFrame();
		LogFrame logFrame = null;

		// Maps the log frame.
		if (logFrameDTO != null) {
			logFrame = mapper().map(logFrameDTO, new LogFrame());
		}

		// Sets the log frame parent project.
		if (logFrame != null) {
			final Project project = em().find(Project.class, cmd.getProjectId());
			logFrame.setParentProject(project);
			
			final ProjectModel projectModel = project.getProjectModel();
			if (projectModel != null && projectModel.getLogFrameModel() != null) {
				logFrame.setLogFrameModel(projectModel.getLogFrameModel());
            }
		}
			
		if (logFrame != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Merges the log frame.");
			}

			logFrame = updateLogFrame(logFrame, logFrameDTO);

			// Re-map as DTO.
			logFrameDTO = mapper().map(logFrame, new LogFrameDTO());
		}

		return logFrameDTO;
	}

	/**
	 * Update the given log frame with the data contained in the DTO.
	 * <p/>
	 * Executed in a transaction.
	 * 
	 * @param logFrame
	 * @param logFrameDTO DTO containing the values.
	 * @return 
	 */
	@Transactional
	protected LogFrame updateLogFrame(LogFrame logFrame, LogFrameDTO logFrameDTO) {
		// Merges log frame.
		logFrame = em().merge(logFrame);
		
		// Update the project activities advancement
		List<LogFrameActivityDTO> activities = logFrameDTO.getAllActivitiesDTO();
		int countActivities = 0;
		int projectActivitiesAdvancement = 0;
		
		if (activities != null && !activities.isEmpty()) {
			for (final LogFrameActivityDTO activity : activities) {
				countActivities++;
				projectActivitiesAdvancement += activity.getAdvancement();
			}
		}
		
		if (countActivities > 0) {
			projectActivitiesAdvancement /= countActivities;
		}
		
		logFrame.getParentProject().setActivityAdvancement(projectActivitiesAdvancement);
		em().merge(logFrame.getParentProject());
		
		return logFrame;
	}

}
