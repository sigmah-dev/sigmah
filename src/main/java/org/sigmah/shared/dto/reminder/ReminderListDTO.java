package org.sigmah.shared.dto.reminder;

import java.util.List;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity reminder.ReminderList.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReminderListDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6658962865288286355L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "reminder.ReminderList";
	}

	// Id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	// Reminders
	public List<ReminderDTO> getReminders() {
		return get("reminders");
	}

	public void setReminders(List<ReminderDTO> reminders) {
		set("reminders", reminders);
	}
}
