package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.OrgUnitModel} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public interface OrgUnitModelDAO extends DAO<OrgUnitModel, Integer> {

	/**
	 * Finds the OrgUnit models visible to the given {@code organizationId}.
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @return The OrgUnit models visible to the given {@code organizationId}, ordered by name.
	 */
	List<OrgUnitModel> findOrgUnitModelsVisibleToOrganization(Integer organizationId);

	/**
	 * Finds the OrgUnit models visible to the given {@code organizationId} which status is included into given
	 * {@code status} filters (if any).
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @param status
	 *          Filters OrgUnit models which status is included into given {@code status}.<br>
	 *          Ignored if {@code null} or empty.
	 * @return The OrgUnit models visible to the given {@code organizationId}, ordered by name.
	 */
	List<OrgUnitModel> findOrgUnitModelsVisibleToOrganization(Integer organizationId, List<ProjectModelStatus> status);

}
