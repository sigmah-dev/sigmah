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
import org.sigmah.shared.dto.IsModel.ModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * GetAvailableStatusForModel command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetAvailableStatusForModel extends AbstractCommand<ListResult<ProjectModelStatus>> {

	/**
	 * The model type.
	 */
	private ModelType modelType;

	/**
	 * The model id.
	 */
	private Integer modelId;

	/**
	 * The current model status.
	 */
	private ProjectModelStatus status;

	protected GetAvailableStatusForModel() {
		// Serialization.
	}

	/**
	 * Retrieves the available {@link ProjectModelStatus} for the given arguments.
	 * 
	 * @param modelType
	 *          The model type.
	 * @param modelId
	 *          The model id.
	 * @param status
	 *          The model current status.
	 * @see ModelType
	 */
	public GetAvailableStatusForModel(ModelType modelType, Integer modelId, ProjectModelStatus status) {
		this.modelType = modelType;
		this.modelId = modelId;
		this.status = status;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public Integer getModelId() {
		return modelId;
	}

	public ProjectModelStatus getStatus() {
		return status;
	}

}
