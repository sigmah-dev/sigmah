package org.sigmah.server.handler;

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


import java.util.*;

import javax.persistence.TypedQuery;

import com.google.inject.Inject;

import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.util.DomainFilters;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.handler.util.ProjectMapper;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.referential.ProjectModelType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler for {@link GetProjects} command.
 *
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjectsHandler extends AbstractCommandHandler<GetProjects, ListResult<ProjectDTO>> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(GetProjectsHandler.class);

	/**
	 * Injected project mapper.
	 */
	private final ProjectMapper projectMapper;
	private final ProjectDAO projectDAO;

	@Inject
	public GetProjectsHandler(final ProjectMapper projectMapper, ProjectDAO projectDAO) {
		this.projectMapper = projectMapper;
		this.projectDAO = projectDAO;
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * <p>
	 * Gets the projects list from the database.
	 * </p>
	 *
	 * @return A {@link ListResult} containing the projects list.
	 */
	@Override
	public ListResult<ProjectDTO> execute(final GetProjects cmd, final UserExecutionContext context) throws CommandException {
		return execute(cmd, context.getUser());
	}

	public ListResult<ProjectDTO> execute(final GetProjects cmd, final User user) {
		// Disable the ActivityInfo filter on Userdatabase.
		DomainFilters.disableUserFilter(em());

		LOG.debug("Gets projects for following command: {}", cmd);

		// Retrieves command parameters.
		final Set<Project> projects = new HashSet<Project>();
		final ProjectModelType modelType = cmd.getModelType();

		// ---------------
		// Projects which I own or I manage.
		// ---------------

		if (cmd.getViewOwnOrManage()) {
			final TypedQuery<Project> ownerManagerQuery = em().createQuery("SELECT p FROM Project p WHERE p.owner = :ouser OR p.manager = :muser", Project.class);
			ownerManagerQuery.setParameter("ouser", user);
			ownerManagerQuery.setParameter("muser", user);
			projects.addAll(ownerManagerQuery.getResultList());

			projects.addAll(projectDAO.findProjectByTeamMemberIdAndOrgUnitIds(user.getId(), new HashSet<>(cmd.getOrgUnitsIds())));
		}

		// ---------------
		// Favorites projects.
		// ---------------

		if (cmd.isFavoritesOnly()) {
			final TypedQuery<Project> favoritesQuery = em().createQuery("FROM Project p WHERE :user MEMBER OF p.favoriteUsers", Project.class);
			favoritesQuery.setParameter("user", user);

			projects.addAll(favoritesQuery.getResultList());
		}

		// ---------------
		// Projects in my visible organization units.
		// ---------------

		final List<Integer> ids = cmd.getOrgUnitsIds();

		// Use a set to be avoid duplicated entries.
		final Set<OrgUnit> units = new HashSet<OrgUnit>();

		// Checks if there is at least one org unit id specified.
		if (ids == null) {
			LOG.debug("No org unit specified, gets all projects for the user org unit.");

			// Crawl the org units hierarchy from the user root org unit.
			Handlers.crawlUnits(user, units, true);

		} else {
			// Crawl the org units hierarchy from each specified org unit.
			OrgUnit unit;
			for (final Integer id : ids) {
				if ((unit = em().find(OrgUnit.class, id)) != null) {
					Handlers.crawlUnits(unit, units, true);
				}
			}
		}

		// Keep a link between projects and orgUnits.
		final HashMap<Integer, Integer> projectIdToOrgUnitId = new HashMap<Integer, Integer>();

		// Creating the query to retrieve projects
		final TypedQuery<Project> query = buildQuery(cmd);

		// Retrieves all the corresponding org units.
		for (final OrgUnit unit : units) {

			// Builds and executes the query.
			fillQuery(query, cmd, user, unit);

			int count = 0;
			final List<Project> listResults = query.getResultList();
			for (final Project project : listResults) {
				projectIdToOrgUnitId.put(project.getId(), unit.getId());

				if (modelType == null) {
					projects.add(project);
					count++;
				}
				// Filters by model type.
				else {
					if (project.getProjectModel().getVisibility(user.getOrganization()) == modelType) {
						projects.add(project);
						count++;
					}
				}
			}

			LOG.debug("Found {}/{} projects for org unit #{}.", count, listResults.size(), unit.getName());
		}

		Set<Integer> projectIds = new HashSet<>();
		List<Project> visibleProjects = new ArrayList<>();
		for (Project project : projects) {
			if (!projectIds.add(project.getId())) {
				continue;
			}

			if (!Handlers.isProjectVisible(project, user)) {
				continue;
			}

			visibleProjects.add(project);
		}


		// ---------------
		// Mapping and return.
		// ---------------

		final List<ProjectDTO> projectsDTO = new ArrayList<>();
		final IsMappingMode mappingMode = cmd.getMappingMode(); // May be null.

		if (mappingMode == ProjectDTO.Mode._USE_PROJECT_MAPPER) {
			// Using custom project mapper.
			for (final Project project : visibleProjects) {
				projectsDTO.add(projectMapper.map(project, true));
			}

		} else {
			// Using provided mapping mode.
			for (final Project project : visibleProjects) {
				final ProjectDTO projectDTO = mapper().map(project, new ProjectDTO(), cmd.getMappingMode());
				// Filling the orgUnitId using the map made when querying by OrgUnits.
				projectDTO.setOrgUnitId(projectIdToOrgUnitId.get(project.getId()));
				projectsDTO.add(projectDTO);
			}
		}

		LOG.debug("Found {} project(s).", visibleProjects.size());

		return new ListResult<>(projectsDTO);
	}

	private TypedQuery<Project> buildQuery(GetProjects getProjects) {
		final StringBuilder stringBuilder = new StringBuilder("SELECT p FROM Project p WHERE :unit MEMBER OF p.partners");

		if (getProjects.isFavoritesOnly()) {
			stringBuilder.append(" AND :user MEMBER OF p.favoriteUsers");
		}

		return em().createQuery(stringBuilder.toString(), Project.class);
	}

	private void fillQuery(TypedQuery<Project> query, GetProjects getProjects, User user, OrgUnit unit) {
		query.setParameter("unit", unit);
		if (getProjects.isFavoritesOnly()) {
			query.setParameter("user", user);
		}
	}
}
