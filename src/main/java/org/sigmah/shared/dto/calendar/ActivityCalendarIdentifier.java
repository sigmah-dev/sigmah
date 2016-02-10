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

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ActivityCalendarIdentifier implements CalendarIdentifier {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1227917435084017958L;

	private int projectId;
	private String calendarName;
	private String activityPrefix;

	public ActivityCalendarIdentifier() {
		// Serialization.
	}

	public ActivityCalendarIdentifier(int projectId, String calendarName, String activityPrefix) {
		this.projectId = projectId;
		this.calendarName = calendarName;
		this.activityPrefix = activityPrefix;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getActivityPrefix() {
		return activityPrefix;
	}

	public void setActivityPrefix(String activityPrefix) {
		this.activityPrefix = activityPrefix;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ActivityCalendarIdentifier other = (ActivityCalendarIdentifier) obj;
		if (this.projectId != other.projectId) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + this.projectId;
		return hash;
	}
}
