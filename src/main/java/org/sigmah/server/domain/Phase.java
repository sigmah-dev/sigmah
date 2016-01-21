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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Phase domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PHASE_TABLE)
public class Phase extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7265918761740982615L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PHASE_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.PHASE_COLUMN_START_DATE, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = EntityConstants.PHASE_COLUMN_END_DATE, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToOne
	@JoinColumn(name = EntityConstants.PHASE_MODEL_COLUMN_ID, nullable = false)
	@NotNull
	private PhaseModel phaseModel;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.PROJECT_COLUMN_ID, nullable = false)
	@NotNull
	private Project parentProject;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Phase() {
	}

	public Phase(PhaseModel phaseModel) {
		this.phaseModel = phaseModel;
	}

	/**
	 * Starts a phase.
	 */
	public void start() {
		startDate = new Date();
	}

	/**
	 * Returns if the phase is active (start date isn't <code>null</code>).
	 * 
	 * @return If the phase is active.
	 */
	@Transient
	public boolean isActive() {
		return startDate != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("startDate", startDate);
		builder.append("endDate", endDate);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public Project getParentProject() {
		return parentProject;
	}

	public void setParentProject(Project parentProject) {
		this.parentProject = parentProject;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public PhaseModel getPhaseModel() {
		return phaseModel;
	}

	public void setPhaseModel(PhaseModel phaseModel) {
		this.phaseModel = phaseModel;
	}

}
