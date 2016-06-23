package org.sigmah.shared.dto.calendar;

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
import org.sigmah.client.util.DateUtils;

/**
 * Calendar types.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public enum CalendarType {

	Activity(Color.ORANGE),
	Personal(Color.BLUE),
	MonitoredPoint(Color.GREEN),
	Reminder(Color.RED),
	Dummy(Color.GRAY);

	private final Color color;

	private CalendarType(final Color color) {
		this.color = color != null ? color : Color.GRAY;
	}

	/**
	 * Returns the calendar type corresponding color code.
	 * 
	 * @return The calendar type corresponding color code.
	 */
	public int getColorCode() {
		return color.colorCode;
	}

	/**
	 * Calendar available colors.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	private static enum Color {

		// See 'sigmah.css' for color styles.

		ORANGE(1),
		BLUE(2),
		GREEN(3),
		RED(4),
		VIOLET(5),
		GRAY(6);

		private final int colorCode;

		private Color(final int colorCode) {
			this.colorCode = colorCode;
		}
	}
    
	/**
	 * Returns the given {@code type} corresponding {@link CalendarIdentifier} instance for the given {@code id}.
	 * 
	 * @param type
	 *          The calendar type.
	 * @param id
	 *          The calendar id.
	 * @return The {@link CalendarIdentifier} instance.
	 */
    static int projectId;
	public static CalendarIdentifier getIdentifier(final CalendarType type, final Integer id) {
        
        

		if (type == null) {
			throw new IllegalArgumentException("Invalid calendar type.");
		}

		switch (type) {

			case Activity:
                projectId = id;
				return new ActivityCalendarIdentifier(id, I18N.CONSTANTS.logFrameActivities(), I18N.CONSTANTS.logFrameActivitiesCode());

			case Personal:
				return new PersonalCalendarIdentifier(id);

			case MonitoredPoint:
				return new MonitoredPointCalendarIdentifier(id, I18N.CONSTANTS.monitoredPoints(), I18N.CONSTANTS.monitoredPointClosed(),
					I18N.CONSTANTS.monitoredPointExpectedDate(), DateUtils.DATE_SHORT.getPattern() , projectId);

			case Reminder:
				return new ReminderCalendarIdentifier(id, I18N.CONSTANTS.reminderPoints(), I18N.CONSTANTS.monitoredPointClosed(),
					I18N.CONSTANTS.monitoredPointExpectedDate(), DateUtils.DATE_SHORT.getPattern(),projectId);

			default:
				throw new IllegalArgumentException("Invalid calendar type.");
		}
	}

}
