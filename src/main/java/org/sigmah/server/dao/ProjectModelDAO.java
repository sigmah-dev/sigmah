package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.ProjectModel} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public interface ProjectModelDAO extends DAO<ProjectModel, Integer> {

	/**
	 * Finds the project models visible to the given {@code organizationId}.
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @return The project models visible to the given {@code organizationId}, ordered by name.
	 */
	List<ProjectModel> findProjectModelsVisibleToOrganization(Integer organizationId);

	/**
	 * Finds the project models visible to the given {@code organizationId} which status is included into given
	 * {@code status} filters (if any).
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @param status
	 *          Filters project models which status is included into given {@code status}.<br>
	 *          Ignored if {@code null} or empty.
	 * @return The project models visible to the given {@code organizationId}, ordered by name.
	 */
	List<ProjectModel> findProjectModelsVisibleToOrganization(Integer organizationId, List<ProjectModelStatus> status);

}
