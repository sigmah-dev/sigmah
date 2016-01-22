package org.sigmah.client.ui.presenter.reminder;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.conf.PropertyName;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.google.gwt.core.client.GWT;

/**
 * Reminder types (reminder / monitored point).
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see ReminderDTO
 * @see MonitoredPointDTO
 */
public enum ReminderType {

	/**
	 * Reminder: "To Do".
	 * 
	 * @see ReminderDTO
	 */
	REMINDER,

	/**
	 * Monitored point: "expected action".
	 * 
	 * @see MonitoredPointDTO
	 */
	MONITORED_POINT;

	/**
	 * Returns the given {@code value} corresponding {@link ReminderType}.
	 * 
	 * @param value
	 *          The string value (case insensitive).
	 * @return The given {@code value} corresponding {@link ReminderType}, or {@code null}.
	 */
	public static ReminderType fromString(final String value) {
		try {

			return ReminderType.valueOf(value.toUpperCase());

		} catch (final Exception e) {
			// Digest.
			return null;
		}
	}

	/**
	 * Returns the given {@code dto} corresponding {@link ReminderType}.
	 * 
	 * @param dto
	 *          The DTO value.<br/>
	 *          Only {@link ReminderDTO} and {@link MonitoredPointDTO} are supported.
	 * @return The given {@code dto} corresponding {@link ReminderType}, or {@code null} if {@code dto} is {@code null}.
	 */
	public static <D extends EntityDTO<?>> ReminderType fromDTO(final D dto) {

		if (dto instanceof ReminderDTO) {
			return ReminderType.REMINDER;

		} else if (dto instanceof MonitoredPointDTO) {
			return ReminderType.MONITORED_POINT;

		} else {
			return null;
		}
	}

	/**
	 * Returns the given {@code type} corresponding entity name.
	 * 
	 * @param type
	 *          The reminder type.
	 * @return The given {@code type} corresponding entity name.
	 */
	public static String getEntityName(final ReminderType type) {

		if (type == null) {
			throw new IllegalArgumentException("Invalid reminder type.");
		}

		switch (type) {
			case REMINDER:
				return new ReminderDTO().getEntityName();
			case MONITORED_POINT:
				return new MonitoredPointDTO().getEntityName();
			default:
				return null;
		}
	}

	/**
	 * Returns the given {@code type} and {@code creation} corresponding title.
	 * 
	 * @param type
	 *          The reminder type.
	 * @param creation
	 *          {@code true} in case of a creation, {@code false} in case of a modification.
	 * @return The given {@code mode} and {@code creation} corresponding title.
	 */
	public static String getTitle(final ReminderType type, final boolean creation) {

		if (type == null) {
			throw new IllegalArgumentException("Invalid reminder type.");
		}

		if (!GWT.isClient()) {
			throw new UnsupportedOperationException("Client i18n resources cannot be accessed on server-side.");
		}

		switch (type) {
			case REMINDER:
				return creation ? I18N.CONSTANTS.reminderAdd() : I18N.CONSTANTS.reminderUpdate();
			case MONITORED_POINT:
				return creation ? I18N.CONSTANTS.monitoredPointAdd() : I18N.CONSTANTS.monitoredPointUpdate();
			default:
				return PropertyName.error(type.name());
		}
	}

	/**
	 * Returns the given {@code type} and {@code creation} corresponding header.
	 * 
	 * @param type
	 *          The reminder type.
	 * @param creation
	 *          {@code true} in case of a creation, {@code false} in case of a modification.
	 * @return The given {@code mode} and {@code creation} corresponding header.
	 */
	public static String getHeader(final ReminderType type, final boolean creation) {

		if (type == null) {
			throw new IllegalArgumentException("Invalid reminder type.");
		}

		if (!GWT.isClient()) {
			throw new UnsupportedOperationException("Client i18n resources cannot be accessed on server-side.");
		}

		switch (type) {
			case REMINDER:
				return creation ? I18N.CONSTANTS.reminderAddDetails() : I18N.CONSTANTS.reminderUpdateDetails();
			case MONITORED_POINT:
				return creation ? I18N.CONSTANTS.monitoredPointAddDetails() : I18N.CONSTANTS.monitoredPointUpdateDetails();
			default:
				return PropertyName.error(type.name());
		}
	}

}
