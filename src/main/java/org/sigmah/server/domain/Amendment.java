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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.logframe.LogFrame;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.AmendmentState;

/**
 * <p>
 * Amendment domain entity.
 * </p>
 * Represents a version of a project.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.AMENDMENT_TABLE)
public class Amendment extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7236305883626202632L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.AMENDMENT_COLMUN_ID)
	private Integer id;

	@Column(name = EntityConstants.AMENDMENT_COLMUN_NAME)
	private String name;

	@Column(name = EntityConstants.AMENDMENT_COLMUN_VERSION)
	private Integer version;

	@Column(name = EntityConstants.AMENDMENT_COLMUN_REVISION)
	private Integer revision;

	@Column(name = EntityConstants.AMENDMENT_COLMUN_STATUS)
	@Enumerated(EnumType.STRING)
	private AmendmentState state;

	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = EntityConstants.AMENDMENT_COLMUN_DATE, nullable = true)
	private Date date;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = EntityConstants.LOGFRAME_COLUMN_ID, nullable = true)
	private LogFrame logFrame;

	@ManyToOne
	@JoinColumn(name = EntityConstants.PROJECT_COLUMN_ID)
	private Project parentProject;

	@ManyToMany
	private List<HistoryToken> values;

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
		builder.append("version", version);
		builder.append("revision", revision);
		builder.append("name", name);
		builder.append("state", state);
		builder.append("date", date);

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

	public Project getParentProject() {
		return parentProject;
	}

	public void setParentProject(Project parentProject) {
		this.parentProject = parentProject;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getRevision() {
		return revision;
	}

	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	public AmendmentState getState() {
		return state;
	}

	public void setState(AmendmentState state) {
		this.state = state;
	}

	public LogFrame getLogFrame() {
		return logFrame;
	}

	public void setLogFrame(LogFrame logFrame) {
		this.logFrame = logFrame;
	}

	public List<HistoryToken> getValues() {
		return values;
	}

	public void setValues(List<HistoryToken> values) {
		this.values = values;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
