package org.sigmah.server.domain;

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
