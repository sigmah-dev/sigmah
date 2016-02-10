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

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Get all draft projects created from a specified project model
 * 
 * @author HUZHE
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjectsByModel extends AbstractCommand<ListResult<ProjectDTO>> {

	private Integer projectModelId;
	private ProjectDTO.Mode mappingMode;

	public GetProjectsByModel() {
		// Serialization.
	}

	/**
	 * @param projectModelId
	 *          The project model id.
	 * @param mappingMode
	 *          The mapping mode.
	 */
	public GetProjectsByModel(Integer projectModelId, ProjectDTO.Mode mappingMode) {
		this.projectModelId = projectModelId;
		this.mappingMode = mappingMode;
	}

	public Integer getProjectModelId() {
		return projectModelId;
	}

	public ProjectDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
