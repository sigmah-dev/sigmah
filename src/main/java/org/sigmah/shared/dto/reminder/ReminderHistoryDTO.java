package org.sigmah.shared.dto.reminder;

import java.util.Date;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.ReminderChangeType;

/**
 * ReminderHistoryDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReminderHistoryDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6492226113360591166L;

	/**
	 * DTO corresponding domain entity name.
	 */
	public static final String ENTITY_NAME = "reminder.ReminderHistory";

	// DTO attributes keys.
	public static final String DATE = "date";
	public static final String VALUE = "value";
	public static final String TYPE = "type";
	public static final String USER_ID = "userId";
	public static final String REMINDER = "reminder";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(DATE, getDate());
		builder.append(VALUE, getValue());
		builder.append(TYPE, getType());
		builder.append(USER_ID, getUserId());
	}

	public Date getDate() {
		return get(DATE);
	}

	public void setDate(Date date) {
		set(DATE, date);
	}

	public String getValue() {
		return get(VALUE);
	}

	public void setValue(String value) {
		set(VALUE, value);
	}

	public int getUserId() {
		return get(USER_ID);
	}

	public void setUserId(int userId) {
		set(USER_ID, userId);
	}

	public ReminderChangeType getType() {
		return get(TYPE);
	}

	public void setType(ReminderChangeType type) {
		set(TYPE, type);
	}

	public ReminderDTO getReminder() {
		return get(REMINDER);
	}

	public void setReminder(ReminderDTO reminder) {
		set(REMINDER, reminder);
	}

}
