package org.sigmah.server.dao.impl;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.Profile;
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

	@Override
	public Project updateProjectTeamMembers(Project project, List<User> teamMembers, List<Profile> teamMemberProfiles, User modifier) {
		project.setTeamMembers(teamMembers);
		project.setTeamMemberProfiles(teamMemberProfiles);
		return persist(project, modifier);
	}
}
