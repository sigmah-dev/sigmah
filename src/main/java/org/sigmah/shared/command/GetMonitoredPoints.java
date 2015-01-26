package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

/**
 * Request to retrieve the monitored points of every project available to the current user.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetMonitoredPoints extends AbstractCommand<ListResult<MonitoredPointDTO>> {

	private Integer projectId;
	private MonitoredPointDTO.Mode mappingMode;

	protected GetMonitoredPoints() {
		// Serialization.
	}

	public GetMonitoredPoints(MonitoredPointDTO.Mode mappingMode) {
		this(null, mappingMode);
	}

	public GetMonitoredPoints(final Integer projectId, MonitoredPointDTO.Mode mappingMode) {
		this.projectId = projectId;
		this.mappingMode = mappingMode;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public MonitoredPointDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
