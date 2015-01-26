package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Retrieves the list of project models available to the user.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectModels extends AbstractCommand<ListResult<ProjectModelDTO>> {

	/**
	 * The mapping mode (may be {@code null}).
	 */
	private ProjectModelDTO.Mode mappingMode;

	/**
	 * The filtered status (may be {@code null}).<br>
	 * If {@code null}, a default filter is set.
	 */
	private ProjectModelStatus[] statusFilters;

	protected GetProjectModels() {
		// Serialization.
	}

	/**
	 * <p>
	 * Retrieves the {@link ProjectModelDTO} of the authenticated user's organization.
	 * </p>
	 * <p>
	 * If no {@code statusFilters} is set, a default filter will only retrieve project models with status
	 * {@link ProjectModelStatus#READY} or {@link ProjectModelStatus#USED}.
	 * </p>
	 * 
	 * @param mappingMode
	 *          The mapping mode. If {@code null}, default mapping is processed.
	 * @param statusFilters
	 *          Only retrieves project models which status is included into the given {@code statusFilters}. If
	 *          {@code null} or empty, default filter is set.
	 */
	public GetProjectModels(ProjectModelDTO.Mode mappingMode, ProjectModelStatus... statusFilters) {
		this.mappingMode = mappingMode;
		this.statusFilters = statusFilters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("mappingMode", mappingMode);
		builder.append("statusFilters", statusFilters);
	}

	public ProjectModelDTO.Mode getMappingMode() {
		return mappingMode;
	}

	public ProjectModelStatus[] getStatusFilters() {
		return statusFilters;
	}

}
