package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * The command to change the current phase of a project. This command can be used to ends a project too.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ChangePhase extends AbstractCommand<ProjectDTO> {

	/**
	 * The project id.
	 */
	private int projectId;

	/**
	 * The id of the phase to activate. If this id is <code>null</code>, the current phase will be ended and no other
	 * phase will be activated (closes the project).
	 */
	private Integer phaseId;

	public ChangePhase() {
		// Serialization.
	}

	public ChangePhase(int projectId, Integer phaseId) {
		this.projectId = projectId;
		this.phaseId = phaseId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public Integer getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Integer phaseId) {
		this.phaseId = phaseId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChangePhase other = (ChangePhase) obj;
		if (projectId != other.projectId)
			return false;
		if (phaseId != other.phaseId)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("projectId", projectId);
		builder.append("phaseId", phaseId);
	}

}
