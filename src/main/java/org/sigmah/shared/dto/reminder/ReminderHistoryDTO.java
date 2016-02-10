package org.sigmah.shared.dto.reminder;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
