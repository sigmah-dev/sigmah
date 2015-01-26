package org.sigmah.server.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.export.GlobalExport;
import org.sigmah.server.domain.export.GlobalExportSettings;

/**
 * GlobalExportDAO implementation.
 * 
 * @author sherzod
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GlobalExportHibernateDAO extends AbstractDAO<GlobalExport, Integer> implements GlobalExportDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectModel> getProjectModelsByOrganization(final Organization organization) {
		final TypedQuery<ProjectModel> query = em().createQuery("SELECT pmv.model FROM ProjectModelVisibility pmv WHERE pmv.organization=:org", ProjectModel.class);
		query.setParameter("org", organization);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GlobalExport> getGlobalExports(final Date from, final Date to) {
		final TypedQuery<GlobalExport> query = em().createQuery("FROM GlobalExport e where e.date between :fromDate and :toDate", entityClass);
		query.setParameter("fromDate", from);
		query.setParameter("toDate", to);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GlobalExport> getOlderExports(final Date oldDate, final Organization organization) {
		final TypedQuery<GlobalExport> query = em().createQuery("FROM GlobalExport e WHERE e.organization = :org and e.date < :oldDate", entityClass);
		query.setParameter("oldDate", oldDate);
		query.setParameter("org", organization);
		return query.getResultList();
	}

	@Override
	public List<GlobalExportSettings> getGlobalExportSettings() {

		final TypedQuery<GlobalExportSettings> query = em().createQuery("FROM GlobalExportSettings ges", GlobalExportSettings.class);
		return query.getResultList();

	}

}
