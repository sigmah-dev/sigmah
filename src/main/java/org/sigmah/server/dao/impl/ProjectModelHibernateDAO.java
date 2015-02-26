package org.sigmah.server.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
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

		final StringBuilder builder = new StringBuilder(
			"SELECT "
		  + "  pm "
		  + "FROM "
		  + "  ProjectModel pm "
		  + "  LEFT JOIN FETCH pm.projectBanner pb "
		  + "  LEFT JOIN FETCH pm.projectDetails pd "
		  + "  LEFT JOIN FETCH pm.logFrameModel lfm "
		  + "WHERE "
		  + "  EXISTS ("
		  + "    SELECT 1 FROM ProjectModelVisibility pmv WHERE pmv.organization.id = :organizationId"
		  + "  )");
		if (CollectionUtils.isNotEmpty(status)) {
			builder.append(" AND pm.status IN :status ");
		}
		if(!status.contains(ProjectModelStatus.UNDER_MAINTENANCE)) {
			builder.append(" AND (pm.dateMaintenance is null OR pm.dateMaintenance > :now)");
		}
		builder.append("ORDER BY pm.name");

		final TypedQuery<ProjectModel> query = em().createQuery(builder.toString(), ProjectModel.class);
		query.setParameter("organizationId", organizationId);
		if (CollectionUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		if(!status.contains(ProjectModelStatus.UNDER_MAINTENANCE)) {
			query.setParameter("now", new Timestamp(new Date().getTime()));
		}

		return query.getResultList();
	}

}
