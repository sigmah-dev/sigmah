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
import org.sigmah.shared.dto.ProjectModelDTO;

/**
 * Retrieves a project model.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectModel extends AbstractCommand<ProjectModelDTO> {

	private Integer modelId;
	private String status;
	private ProjectModelDTO.Mode mappingMode;

	protected GetProjectModel() {
		// Serialization.
	}

	public GetProjectModel(Integer modelId, ProjectModelDTO.Mode mappingMode) {
		this(modelId, null, mappingMode);
	}

	public GetProjectModel(Integer modelId, String status, ProjectModelDTO.Mode mappingMode) {
		this.modelId = modelId;
		this.status = status;
		this.mappingMode = mappingMode;
	}

	public Integer getModelId() {
		return modelId;
	}

	public String getStatus() {
		return status;
	}

	public ProjectModelDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
