package org.sigmah.server.domain.calendar;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Personnal Event domain entity.
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PERSONAL_EVENT_TABLE)
@FilterDefs({ @FilterDef(name = EntityFilters.HIDE_DELETED)
})
@Filters({ @Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.PERSONAL_EVENT_HIDE_DELETED_CONDITION)
})
public class PersonalEvent extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4972704490321613870L;

	/**
	 * Event identifier.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PERSONAL_EVENT_COLUMN_ID)
	private Integer id;

	/**
	 * Identifier of the parent calendar of this event.
	 */
	@Column(name = EntityConstants.PERSONAL_EVENT_COLUMN_CALENDAR_ID)
	private Integer calendarId;

	/**
	 * Title of the event (a short description).
	 */
	@Column(name = EntityConstants.PERSONAL_EVENT_COLUMN_SUMMARY)
	@Size(max = EntityConstants.PERSONAL_EVENT_SUMMARY_MAX_LENGTH)
	private String summary;
	/**
	 * Body of the event.
	 */
	@Column(name = EntityConstants.PERSONAL_EVENT_COLUMN_DESCRIPTION)
	@Size(max = EntityConstants.PERSONAL_EVENT_DESCRIPTION_MAX_LENGTH)
	private String description;
	/**
	 * Start date of the event.
	 */
	@Column(name = EntityConstants.PERSONAL_EVENT_COLUMN_START_DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	/**
	 * End date of the event.
	 */
	@Column(name = EntityConstants.PERSONAL_EVENT_COLUMN_END_DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	/**
	 * Creation date of the event.
	 */
	@Column(name = EntityConstants.PERSONAL_EVENT_COLUMN_DATE_CREATED)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	/**
	 * Date of deletion.
	 */
	@Column(name = EntityConstants.PERSONAL_EVENT_COLUMN_DATE_DELETED)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		dateDeleted = new Date();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public boolean isDeleted() {
		return dateDeleted != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("summary", summary);
		builder.append("description", description);
		builder.append("startDate", startDate);
		builder.append("endDate", endDate);
		builder.append("dateCreated", dateCreated);
		builder.append("dateDeleted", dateDeleted);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Integer calendarId) {
		this.calendarId = calendarId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}
}
