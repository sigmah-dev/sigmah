package org.sigmah.shared.command;

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
