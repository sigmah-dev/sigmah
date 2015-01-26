package org.sigmah.shared.command;

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
