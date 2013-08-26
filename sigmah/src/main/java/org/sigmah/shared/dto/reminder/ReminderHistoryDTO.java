package org.sigmah.shared.dto.reminder;

import java.util.Date;

import org.sigmah.shared.domain.reminder.ReminderChangeType;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ReminderHistoryDTO extends BaseModelData implements EntityDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6492226113360591166L;

	/**
	 * 
	 */

	@Override
	public String getEntityName() {
		return "reminder.ReminderHistory";
	}

	@Override
	public int getId() {
		final Integer id = (Integer) get("id");
		return id != null ? id : -1;
	}

	public void setId(int id) {
		set("id", id);
	}

	public Date getDate() {
		return get("date");
	}

	public void setDate(Date date) {
		set("date", date);
	}

	public String getValue() {
		return get("value");
	}

	public void setValue(String value) {
		set("value", value);
	}

	public int getUserId() {
		final Integer userId = (Integer) get("userId");
		return userId != null ? userId : -1;
	}

	public void setUserId(int userId) {
		set("userId", userId);
	}

	public ReminderChangeType getType() {
		return get("type");
	}

	public void setType(ReminderChangeType type) {

		set("type", type);
	}

	public void setReminder(ReminderDTO reminder) {
		set("reminder", reminder);
	}

	public ReminderDTO getReminder() {
		return get("reminder");
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof ReminderHistoryDTO)) {
			return false;
		}

		final ReminderHistoryDTO other = (ReminderHistoryDTO) obj;

		return getId() == other.getId();
	}

}