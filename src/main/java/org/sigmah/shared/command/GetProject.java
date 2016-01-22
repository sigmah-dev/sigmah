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
 * Retrieves a project available to the user.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProject extends AbstractCommand<ProjectDTO> {

	/**
	 * The project id to retrieve.
	 */
	private Integer projectId;

	/**
	 * Optional amendment id retrieving a specific version of the project.
	 */
	private Integer amendmentId;

	/**
	 * Optional mapping mode.
	 */
	private ProjectDTO.Mode mappingMode;

	protected GetProject() {
		// Serialization.
	}

	public GetProject(Integer projectID) {
		projectId = projectID;
	}

	/**
	 * Retrieves the project corresponding to the given {@code projectId}.
	 * 
	 * @param projectId
	 *          The project id.
	 * @param mappingMode
	 *          The mapping mode.
	 */
	public GetProject(Integer projectId, ProjectDTO.Mode mappingMode) {
		this(projectId, null, mappingMode);
	}

	/**
	 * Retrieves the project corresponding to the given {@code projectId} and {@code amendmentId}.
	 * 
	 * @param projectId
	 *          The project id.
	 * @param amendmentId
	 *          The amendment id.
	 * @param mappingMode
	 *          The mapping mode.
	 */
	public GetProject(Integer projectId, Integer amendmentId, ProjectDTO.Mode mappingMode) {
		this.projectId = projectId;
		this.amendmentId = amendmentId;
		this.mappingMode = mappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("projectId", projectId);
		builder.append("amendmentId", amendmentId);
		builder.append("mappingMode", mappingMode);
	}

	public Integer getProjectId() {
		return projectId;
	}

	public Integer getAmendmentId() {
		return amendmentId;
	}

	public ProjectDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
