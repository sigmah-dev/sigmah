package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.ReminderDTO;

/**
 * Request to retrieve the reminder of every project available to the current user.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetReminders extends AbstractCommand<ListResult<ReminderDTO>> {

	private Integer projectId;
	private ReminderDTO.Mode mappingMode;

	protected GetReminders() {
		// Serialization.
	}

	public GetReminders(ReminderDTO.Mode mappingMode) {
		this(null, mappingMode);
	}

	public GetReminders(final Integer projectId, ReminderDTO.Mode mappingMode) {
		this.projectId = projectId;
		this.mappingMode = mappingMode;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public ReminderDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
