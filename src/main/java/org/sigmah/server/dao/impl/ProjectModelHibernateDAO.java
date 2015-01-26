package org.sigmah.server.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.ProjectModelDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * {@link ProjectModelDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ProjectModelHibernateDAO extends AbstractDAO<ProjectModel, Integer> implements ProjectModelDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectModel> findProjectModelsVisibleToOrganization(final Integer organizationId) {
		return findProjectModelsVisibleToOrganization(organizationId, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectModel> findProjectModelsVisibleToOrganization(final Integer organizationId, final List<ProjectModelStatus> status) {

		final StringBuilder builder = new StringBuilder();

		builder.append("SELECT ");
		builder.append("  pm ");
		builder.append("FROM ");
		builder.append("  ProjectModel pm ");
		builder.append("  LEFT JOIN FETCH pm.projectBanner pb ");
		builder.append("  LEFT JOIN FETCH pm.projectDetails pd ");
		builder.append("  LEFT JOIN FETCH pm.logFrameModel lfm ");
		builder.append("WHERE ");
		builder.append("  EXISTS (");
		builder.append("    SELECT 1 FROM ProjectModelVisibility pmv WHERE pmv.organization.id = :organizationId");
		builder.append("  )");
		if (CollectionUtils.isNotEmpty(status)) {
			builder.append(" AND pm.status IN :status ");
		}
		builder.append("ORDER BY ");
		builder.append("  pm.name");

		final TypedQuery<ProjectModel> query = em().createQuery(builder.toString(), ProjectModel.class);
		query.setParameter("organizationId", organizationId);
		if (CollectionUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}

		return query.getResultList();
	}

}
