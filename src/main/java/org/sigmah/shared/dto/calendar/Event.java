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

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.sigmah.shared.command.result.Calendar;

/**
 * Calendar event.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class Event implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4231012925362130117L;

	public static final String CALENDAR_ID = "calendarId";
	public static final String SUMMARY = "summary";
	public static final String DATE = "date";
	public static final String START_TIME = "startDate";
	public static final String END_TIME = "endDate";
	public static final String DESCRIPTION = "description";
	public static final String REFERENCE_ID = "referenceid";
        public static final String EVENT_TYPE = "eventtype";
        
        private String eventtype;
	private Integer identifier;
	private String summary;
	private String description;
	private Date dtstart;
	private Date dtend;
	private Calendar parent;
        private Integer referenceid;

	public Event() {
		// Serialization.
	}

	public Event(String summary, String description, Date dtstart, Date dtend, Calendar parent, String eventtype, Integer referenceid) {
		this.summary = summary;
		this.description = description;
		this.dtstart = dtstart;
		this.dtend = dtend;
		this.parent = parent;
                this.eventtype = eventtype;
                this.referenceid = referenceid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Event other = (Event) obj;
		return this.identifier == other.identifier || (this.identifier != null && this.identifier.equals(other.identifier));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
		return hash;
	}
	
	/**
	 * Update the event with the given values.
	 * @param values Map of values to set.
	 */
	public void fillValues(Map<String, Object> values) {
		final CalendarWrapper calendarWrapper = (CalendarWrapper) values.get(Event.CALENDAR_ID);
		setParent(calendarWrapper.getCalendar());

		setSummary((String) values.get(Event.SUMMARY));
		setDescription((String) values.get(Event.DESCRIPTION));
                setEventType((String) values.get(Event.EVENT_TYPE));
                setReferenceId((Integer) values.get(Event.REFERENCE_ID));
                
		final Date day = (Date) values.get(Event.DATE);
		final Object startHourSerialized = values.get(Event.START_TIME);
		final Object endHourSerialized = values.get(Event.END_TIME);

		if (startHourSerialized instanceof Long) {
			final Date startHour = new Date((Long) startHourSerialized);
			setDtstart(startHour);
			
			if (endHourSerialized instanceof Long) {
				final Date endHour = new Date((Long) endHourSerialized);
				setDtend(endHour);
			} else {
				setDtend(null);
			}

		} else {
			setDtstart(new Date(day.getYear(), day.getMonth(), day.getDate()));
			setDtend(new Date(day.getYear(), day.getMonth(), day.getDate() + 1));
		}
	}

	public Integer getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Integer identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDtend() {
		return dtend;
	}

	public void setDtend(Date dtend) {
		this.dtend = dtend;
	}

	public Date getDtstart() {
		return dtstart;
	}

	public void setDtstart(Date dtstart) {
		this.dtstart = dtstart;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

        public String getEventType() {
		return eventtype;
	}

	public void setEventType(String eventtype) {
		this.eventtype = eventtype;
	}
        
        public Integer getReferenceId() {
		return referenceid;
	}

	public void setReferenceId(Integer referenceid) {
		this.referenceid = referenceid;
	}
        
	public Calendar getParent() {
		return parent;
	}

	public void setParent(Calendar parent) {
		this.parent = parent;
	}
	
	@SuppressWarnings("deprecation")
	public Date getKey() {
		return new Date(dtstart.getYear(), dtstart.getMonth(), dtstart.getDate());
	}
}
