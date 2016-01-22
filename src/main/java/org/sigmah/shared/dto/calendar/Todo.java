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

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class Todo implements Serializable, Comparable<Todo> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1825998676357274501L;

	private String description;
	private int percent;
	private Date dtstart;
	private Date dtcomplete;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Todo o) {
		if (o == null)
			throw new NullPointerException();

		if (this.dtstart == null && o.dtstart == null) {
			return this.description.compareTo(o.description);
		} else if (this.dtstart == null)
			return -1;
		else if (o.dtstart == null)
			return 1;

		return this.description.compareTo(o.description);
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
		final Todo other = (Todo) obj;
		if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
			return false;
		}
		if (this.dtstart != other.dtstart && (this.dtstart == null || !this.dtstart.equals(other.dtstart))) {
			return false;
		}
		if (this.dtcomplete != other.dtcomplete && (this.dtcomplete == null || !this.dtcomplete.equals(other.dtcomplete))) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 83 * hash + (this.description != null ? this.description.hashCode() : 0);
		hash = 83 * hash + (this.dtstart != null ? this.dtstart.hashCode() : 0);
		hash = 83 * hash + (this.dtcomplete != null ? this.dtcomplete.hashCode() : 0);
		return hash;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDtcomplete() {
		return dtcomplete;
	}

	public void setDtcomplete(Date dtcomplete) {
		this.dtcomplete = dtcomplete;
	}

	public Date getDtstart() {
		return dtstart;
	}

	public void setDtstart(Date dtstart) {
		this.dtstart = dtstart;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

}
