package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Retrieves the given id(s) corresponding project(s) list.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjectsFromId extends AbstractCommand<ListResult<ProjectDTO>> {

	/**
	 * List of the projects ids.
	 */
	private List<Integer> ids;

	/**
	 * The mapping mode specifying the scope of data to retrieve.
	 */
	private ProjectDTO.Mode mappingMode;

	protected GetProjectsFromId() {
		// Serialization.
	}

	public GetProjectsFromId(List<Integer> ids, ProjectDTO.Mode mappingMode) {
		this.ids = ids;
		this.mappingMode = mappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("ids", ids);
		builder.append("mappingMode", mappingMode);
	}

	public List<Integer> getIds() {
		return ids;
	}

	public ProjectDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
