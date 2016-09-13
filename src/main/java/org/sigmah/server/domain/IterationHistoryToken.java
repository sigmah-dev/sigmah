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
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Iteration history token domain entity.
 * </p>
 * <p>
 * Represents an historized layout group iteration.
 * </p>
 */
@Entity
@Table(name = EntityConstants.ITERATION_HISTORY_TOKEN_TABLE)
public class IterationHistoryToken extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8517313286436440246L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ITERATION_HISTORY_TOKEN_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.ITERATION_HISTORY_TOKEN_COLUMN_DATE, nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	@NotNull
	private Date date;

	@Column(name = EntityConstants.ITERATION_HISTORY_TOKEN_COLUMN_NAME, nullable = false)
	@NotNull
	private String name;


	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@Column(name = EntityConstants.ITERATION_HISTORY_TOKEN_COLUMN_PROJECT, nullable = false)
	@NotNull
	private Integer projectId;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.ITERATION_HISTORY_TOKEN_COLUMN_CORE_VERSION, nullable = true)
	private Amendment coreVersion;

	@Column(name = EntityConstants.ITERATION_HISTORY_TOKEN_COLUMN_ITERATION, nullable = false)
	private Integer layoutGroupIterationId;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.ITERATION_HISTORY_TOKEN_COLUMN_LAYOUT_GROUP, nullable = false)
	private LayoutGroup layoutGroup;

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
		builder.append("layoutGroup", layoutGroup);
		builder.append("projectId", projectId);
		builder.append("date", date);
		builder.append("name", name);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LayoutGroup getLayoutGroup() {
		return layoutGroup;
	}

	public void setLayoutGroup(LayoutGroup layoutGroup) {
		this.layoutGroup = layoutGroup;
	}
}
