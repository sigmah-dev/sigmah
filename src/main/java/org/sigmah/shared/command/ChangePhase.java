package org.sigmah.shared.command;

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
