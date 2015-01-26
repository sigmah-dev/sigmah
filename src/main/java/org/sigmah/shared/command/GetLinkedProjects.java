package org.sigmah.shared.command;

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
