package org.sigmah.server.dao;

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

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.User;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.Project} domain object.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ProjectDAO extends DAO<Project, Integer> {

	/**
	 * Retrieves the {@link Project} list related to the given {@code pmodels}.
	 *
	 * @param pmodels
	 *          The {@link ProjectModel} collection.
	 * @return The {@link Project} list related to the given {@code pmodels}.
	 */
	List<Project> getProjects(Collection<ProjectModel> pmodels);

	/**
	 * Retrieves the <b>active</b> (not deleted) <b>draft</b> {@link Project}s related to the given {@code ownerId}.<br>
	 * Draft projects have a project model with {@link ProjectModelStatus#DRAFT} status.
	 *
	 * @param ownerId
	 *          The user (owner) id.
	 * @return The <b>active</b> (not deleted) <b>draft</b> {@link Project}s related to the given {@code ownerId} sorted
	 *         by fullname.
	 */
	List<Project> findDraftProjects(Integer ownerId);

	Project updateProjectTeamMembers(Project project, List<User> teamMembers, User modifier);
}
