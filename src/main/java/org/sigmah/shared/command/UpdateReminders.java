package org.sigmah.shared.command;

import java.util.Arrays;
import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.ReminderDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateReminders extends AbstractCommand<ListResult<ReminderDTO>> {

	private List<ReminderDTO> list;

	public UpdateReminders() {
		// Serialization.
	}

	public UpdateReminders(final ReminderDTO... reminders) {
		this(reminders != null ? Arrays.asList(reminders) : null);
	}

	public UpdateReminders(final List<ReminderDTO> list) {
		this.list = list;
	}

	public List<ReminderDTO> getList() {
		return list;
	}

}
