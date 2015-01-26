package org.sigmah.shared.command;

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
