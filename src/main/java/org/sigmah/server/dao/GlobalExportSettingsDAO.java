package org.sigmah.server.dao;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.export.GlobalExportSettings;

/**
 * {@link GlobalExportSettings} DAO interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface GlobalExportSettingsDAO extends DAO<GlobalExportSettings, Integer> {

	/**
	 * Finds the given {@code organizationId} corresponding {@link GlobalExportSettings}.
	 * 
	 * @param organizationId
	 *          The {@link Organization} id.
	 * @return The given {@code organizationId} corresponding {@link GlobalExportSettings}.
	 */
	GlobalExportSettings getGlobalExportSettingsByOrganization(Integer organizationId);

}
