package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.referential.ProjectModelType;

/**
 * Retrieves the list of projects available to the user.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjects extends AbstractCommand<ListResult<ProjectDTO>> {

	/**
	 * The type of model of the projects for the current user organization (set to <code>null</code> to ignore this
	 * filter).
	 */
	private ProjectModelType modelType;

	/**
	 * List of organizational units ids in which the projects will be searched (set to <code>null</code> to ignore this
	 * filter).
	 */
	private List<Integer> orgUnitsIds;

    /**
     * Fetch only the favorites projects.
     */
    private boolean favoritesOnly;
    
	/**
	 * If the project that the current user own or manage must be retrieved.
	 */
	private boolean viewOwnOrManage;

	/**
	 * The mapping mode specifying the scope of data to retrieve.
	 */
	private ProjectDTO.Mode mappingMode;
    
	public GetProjects() {
		// Serialization.
	}

	public GetProjects(ProjectModelType modelType, ProjectDTO.Mode mappingMode) {
		this(null, modelType, mappingMode);
	}

	public GetProjects(List<Integer> orgUnitsIds, ProjectDTO.Mode mappingMode) {
		this(orgUnitsIds, null, mappingMode);
	}

	public GetProjects(List<Integer> orgUnitsIds, ProjectModelType modelType, ProjectDTO.Mode mappingMode) {
		this.modelType = modelType;
		this.orgUnitsIds = orgUnitsIds;
		this.mappingMode = mappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("modelType", modelType);
		builder.append("orgUnitsIds", orgUnitsIds);
		builder.append("favoritesOnly", favoritesOnly);
		builder.append("viewOwnOrManage", viewOwnOrManage);
		builder.append("mappingMode", mappingMode);
	}

	public ProjectModelType getModelType() {
		return modelType;
	}

	/**
	 * Sets the type of model of the projects for the current user organization (set to <code>null</code> to ignore this
	 * filter).
	 * 
	 * @param modelType
	 *          The type.
	 */
	public void setModelType(ProjectModelType modelType) {
		this.modelType = modelType;
	}

	public List<Integer> getOrgUnitsIds() {
		return orgUnitsIds;
	}

    public boolean isFavoritesOnly() {
        return favoritesOnly;
    }

    public void setFavoritesOnly(boolean favoritesOnly) {
        this.favoritesOnly = favoritesOnly;
    }

	public void setViewOwnOrManage(boolean viewOwnOrManage) {
		this.viewOwnOrManage = viewOwnOrManage;
	}

	public boolean getViewOwnOrManage() {
		return viewOwnOrManage;
	}

	public ProjectDTO.Mode getMappingMode() {
		return mappingMode;
	}

	public void setMappingMode(ProjectDTO.Mode mappingMode) {
		this.mappingMode = mappingMode;
	}

	/**
	 * Sets the list of organizational units ids in which the projects will be searched (set to <code>null</code> to
	 * ignore this filter).
	 * 
	 * @param orgUnitsIds
	 *          The list.
	 */
	public void setOrgUnitsIds(List<Integer> orgUnitsIds) {
		this.orgUnitsIds = orgUnitsIds;
	}

}
