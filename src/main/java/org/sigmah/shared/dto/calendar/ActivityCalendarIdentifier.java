package org.sigmah.shared.dto.calendar;

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
