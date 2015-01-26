package org.sigmah.shared.command;

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
