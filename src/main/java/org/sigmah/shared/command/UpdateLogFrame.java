package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.logframe.LogFrameDTO;

/**
 * Update log frame command.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class UpdateLogFrame extends AbstractCommand<LogFrameDTO> {

	private LogFrameDTO logFrame;
	private Integer projectId;

	public UpdateLogFrame() {
		// Serialization.
	}

	public UpdateLogFrame(final LogFrameDTO logFrame, final Integer projectId) {
		this.logFrame = logFrame;
		this.projectId = projectId;
	}

	public LogFrameDTO getLogFrame() {
		return logFrame;
	}

	public Integer getProjectId() {
		return projectId;
	}

}
