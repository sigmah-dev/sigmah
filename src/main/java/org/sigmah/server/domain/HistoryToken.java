package org.sigmah.server.domain;

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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.ValueEventChangeType;

/**
 * <p>
 * History token domain entity.
 * </p>
 * <p>
 * Represents an history value.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.HISTORY_TOKEN_TABLE)
public class HistoryToken extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4117487522284514885L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.HISTORY_TOKEN_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.HISTORY_TOKEN_COLUMN_DATE, nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	@NotNull
	private Date date;

	@Column(name = EntityConstants.HISTORY_TOKEN_COLUMN_VALUE, nullable = false, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String value;

	@Column(name = EntityConstants.HISTORY_TOKEN_COLUMN_TYPE, nullable = true)
	@Enumerated(value = EnumType.STRING)
	private ValueEventChangeType type;
	
	@Column(name = EntityConstants.HISTORY_TOKEN_COLUMN_COMMENT, nullable = true)
	private String comment;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@Column(name = EntityConstants.HISTORY_TOKEN_COLUMN_ELEMENT_ID, nullable = false)
	@NotNull
	private Integer elementId;

	@Column(name = EntityConstants.PROJECT_COLUMN_ID, nullable = false)
	@NotNull
	private Integer projectId;

	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.HISTORY_TOKEN_COLUMN_USER_ID, nullable = true)
	private User user;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.HISTORY_TOKEN_COLUMN_CORE_VERSION, nullable = true)
	private Amendment coreVersion;

	@Column(name = EntityConstants.VALUE_COLUMN_LAYOUT_GROUP_ITERATION, nullable = true)
	private Integer layoutGroupIterationId;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("elementId", elementId);
		builder.append("projectId", projectId);
		builder.append("date", date);
		builder.append("value", value);
		builder.append("type", type);
		builder.append("layoutGroupIterationId", layoutGroupIterationId);
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

	public Integer getElementId() {
		return elementId;
	}

	public void setElementId(Integer elementId) {
		this.elementId = elementId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ValueEventChangeType getType() {
		return type;
	}

	public void setType(ValueEventChangeType type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Amendment getCoreVersion() {
		return coreVersion;
	}

	public void setCoreVersion(Amendment coreVersion) {
		this.coreVersion = coreVersion;
	}

	public Integer getLayoutGroupIterationId() {
		return layoutGroupIterationId;
	}

	public void setLayoutGroupIterationId(Integer layoutGroupIterationId) {
		this.layoutGroupIterationId = layoutGroupIterationId;
	}
}
