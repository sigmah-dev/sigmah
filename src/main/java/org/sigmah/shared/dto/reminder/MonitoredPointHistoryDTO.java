package org.sigmah.shared.dto.reminder;

import java.util.Date;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.ReminderChangeType;

/**
 * MonitoredPointHistoryDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class MonitoredPointHistoryDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8640742685576164607L;

	/**
	 * DTO corresponding domain entity name.
	 */
	public static final String ENTITY_NAME = "reminder.MonitoredPointHistory";

	// DTO attributes keys.
	public static final String DATE = ReminderHistoryDTO.DATE;
	public static final String VALUE = ReminderHistoryDTO.VALUE;
	public static final String TYPE = ReminderHistoryDTO.TYPE;
	public static final String USER_ID = ReminderHistoryDTO.USER_ID;
	public static final String MONITORED_POINT = "monitoredPoint";

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

	public Integer getUserId() {
		return get(USER_ID);
	}

	public void setUserId(Integer userId) {
		set(USER_ID, userId);
	}

	public ReminderChangeType getType() {
		return get(TYPE);
	}

	public void setType(ReminderChangeType type) {
		set(TYPE, type);
	}

	public MonitoredPointDTO getMonitoredPoint() {
		return get(MONITORED_POINT);
	}

	public void setMonitoredPoint(MonitoredPointDTO monitoredPoint) {
		set(MONITORED_POINT, monitoredPoint);
	}

}
