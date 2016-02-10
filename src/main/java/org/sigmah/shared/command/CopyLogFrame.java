package org.sigmah.shared.command;

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

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.referential.IndicatorCopyStrategy;

/**
 * Ask the server to replace a log frame by an other one.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CopyLogFrame extends AbstractCommand<LogFrameDTO> {

	/**
	 * ID of the log frame to copy.
	 */
	private int sourceId;
	/**
	 * ID of the project of destination.
	 */
	private int destinationId;

	/**
	 * Strategy for copying referenced indicators
	 */
	private IndicatorCopyStrategy indicatorCopyStrategy = IndicatorCopyStrategy.DUPLICATE;

	public CopyLogFrame() {
		// Serialization.
	}

	/**
	 * @param sourceLogFrameId
	 *          the id of the logframe to copy
	 * @param destinationProjectId
	 *          the id of the project whose logframe should be replaced
	 */
	public CopyLogFrame(int sourceLogFrameId, int destinationProjectId) {
		this.sourceId = sourceLogFrameId;
		this.destinationId = destinationProjectId;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getDestinationId() {
		return destinationId;
	}

	public IndicatorCopyStrategy getIndicatorCopyStrategy() {
		return indicatorCopyStrategy;
	}

	public static CopyLogFrame from(final Integer sourceLogFrameId) {
		CopyLogFrame command = new CopyLogFrame();
		command.setSourceId(sourceLogFrameId);
		return command;
	}

	public CopyLogFrame to(final ProjectDTO project) {
		this.destinationId = project.getId();
		return this;
	}

	public CopyLogFrame to(final Integer destinationProjectId) {
		this.destinationId = destinationProjectId;
		return this;
	}

	public CopyLogFrame with(final IndicatorCopyStrategy strategy) {
		this.indicatorCopyStrategy = strategy;
		return this;
	}

}
