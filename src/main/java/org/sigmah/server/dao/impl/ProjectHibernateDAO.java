package org.sigmah.server.dao.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * {@link ProjectDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectHibernateDAO extends AbstractDAO<Project, Integer> implements ProjectDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Project> getProjects(final Collection<ProjectModel> pmodels) {
		final TypedQuery<Project> query = em().createQuery("FROM Project p WHERE  p.projectModel IN (:pmodels)", Project.class);
		query.setParameter("pmodels", pmodels);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Project> findDraftProjects(final Integer ownerId) {

		final StringBuilder builder = new StringBuilder();

		builder.append("SELECT ");
		builder.append("  p ");
		builder.append("FROM ");
		builder.append("  Project p ");
		builder.append("  LEFT JOIN p.projectModel model ");
		builder.append("  LEFT JOIN FETCH p.logFrame logFrame ");
		builder.append("WHERE ");
		builder.append("  p.owner.id = :ownerId ");
		builder.append("  AND model.status = :draftStatus ");
		builder.append("  AND p.dateDeleted IS NULL ");
		builder.append("ORDER BY p.fullName");

		final TypedQuery<Project> query = em().createQuery(builder.toString(), entityClass);
		query.setParameter("ownerId", ownerId);
		query.setParameter("draftStatus", ProjectModelStatus.DRAFT);

		return query.getResultList();
	}

}
