package org.sigmah.shared.command;

import java.util.Arrays;
import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateMonitoredPoints extends AbstractCommand<ListResult<MonitoredPointDTO>> {

	private List<MonitoredPointDTO> list;

	public UpdateMonitoredPoints() {
		// Serialization.
	}

	public UpdateMonitoredPoints(final MonitoredPointDTO... points) {
		this(points != null ? Arrays.asList(points) : null);
	}

	public UpdateMonitoredPoints(final List<MonitoredPointDTO> list) {
		this.list = list;
	}

	public List<MonitoredPointDTO> getList() {
		return list;
	}

}
