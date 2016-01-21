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
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;

/**
 * Retrieves the linked projects of a given project id.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetLinkedProjects extends AbstractCommand<ListResult<ProjectFundingDTO>> {

	/**
	 * The current project id which retrieved linked projects are related to.
	 */
	private Integer projectId;

	/**
	 * The type of linked projects to retrieve.
	 */
	private LinkedProjectType type;

	/**
	 * The mapping mode specifying the scope of data to retrieve.
	 */
	private ProjectDTO.Mode mappingMode;

	public GetLinkedProjects() {
		// Serialization.
	}

	public GetLinkedProjects(Integer projectId, LinkedProjectType type, ProjectDTO.Mode mappingMode) {
		this.projectId = projectId;
		this.type = type;
		this.mappingMode = mappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("projectId", projectId);
		builder.append("type", type);
		builder.append("mappingMode", mappingMode);
	}

	public Integer getProjectId() {
		return projectId;
	}

	public LinkedProjectType getType() {
		return type;
	}

	public ProjectDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
