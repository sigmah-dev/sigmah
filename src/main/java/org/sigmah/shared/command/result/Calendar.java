package org.sigmah.shared.command.result;

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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sigmah.shared.dto.calendar.CalendarIdentifier;
import org.sigmah.shared.dto.calendar.CalendarType;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.Todo;

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class Calendar implements Result {

	
	private CalendarIdentifier identifier;
	private CalendarType type;
	private String name;
	private Map<Date, List<Event>> events;
	private Collection<Todo> tasks;
	private int style;
	private boolean editable;

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
		final Calendar other = (Calendar) obj;
		if (this.identifier != other.identifier && (this.identifier == null || !this.identifier.equals(other.identifier))) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
		return hash;
	}

	public CalendarIdentifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(CalendarIdentifier identifier) {
		this.identifier = identifier;
	}

	public CalendarType getType() {
		return type;
	}

	public void setType(CalendarType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Date, List<Event>> getEvents() {
		return events;
	}

	public void setEvents(Map<Date, List<Event>> events) {
		this.events = events;
	}

	public Collection<Todo> getTasks() {
		return tasks;
	}

	public void setTasks(Collection<Todo> tasks) {
		this.tasks = tasks;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
