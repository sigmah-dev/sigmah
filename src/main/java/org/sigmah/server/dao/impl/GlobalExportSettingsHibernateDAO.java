package org.sigmah.server.dao.impl;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.GlobalExportSettingsDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.export.GlobalExportSettings;

/**
 * {@link GlobalExportSettingsDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GlobalExportSettingsHibernateDAO extends AbstractDAO<GlobalExportSettings, Integer> implements GlobalExportSettingsDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GlobalExportSettings getGlobalExportSettingsByOrganization(final Integer organizationId) {

		final TypedQuery<GlobalExportSettings> query = em().createQuery("FROM GlobalExportSettings ges WHERE ges.organization.id = :organizationId", entityClass);
		query.setParameter("organizationId", organizationId);

		return query.getSingleResult();
	}

}
