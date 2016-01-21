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


import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.logframe.LogFrame;
import org.sigmah.server.domain.logframe.LogFrameCopyContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.CopyLogFrame;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.logframe.LogFrameDTO;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.util.List;
import javax.persistence.TypedQuery;

/**
 * Handler for the CopyLogFrame command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class CopyLogFrameHandler extends AbstractCommandHandler<CopyLogFrame, LogFrameDTO> {

	private final Mapper mapper;

	@Inject
	public CopyLogFrameHandler(Mapper mapper) {

		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogFrameDTO execute(final CopyLogFrame cmd, final UserExecutionContext context) throws CommandException {
		final Project project = em().find(Project.class, cmd.getDestinationId());
		final LogFrame logFrame = em().find(LogFrame.class, cmd.getSourceId());

		final LogFrame copy = replaceLogFrame(project, logFrame, cmd);
		return mapper.map(copy, LogFrameDTO.class);
	}

	@Transactional
	protected LogFrame replaceLogFrame(Project project, LogFrame source, CopyLogFrame cmd) {
		final LogFrame previousLogFrame = project.getLogFrame();
		if (previousLogFrame != null) {
			project.setLogFrame(null);
			previousLogFrame.setParentProject(null);
			em().merge(project);
			em().remove(previousLogFrame);
		}
		
		// Double check that previous log frames have been deleted.
		final TypedQuery<LogFrame> query = em().createQuery("SELECT l FROM LogFrame l WHERE l.parentProject = :project", LogFrame.class);
		query.setParameter("project", project);
		List<LogFrame> logFrames = query.getResultList();
		
		if(logFrames.size() > 0) {
			throw new IllegalStateException("The previous LogFrame has not been deleted. This should never happen, please check the transaction state.");
		}
		
		// Copying the new log frame.
		final LogFrame copy = source.copy(LogFrameCopyContext.toProject(project).withStrategy(cmd.getIndicatorCopyStrategy()));
		copy.setParentProject(project);
		project.setLogFrame(copy);

		em().merge(project);

		return project.getLogFrame();
	}
}
