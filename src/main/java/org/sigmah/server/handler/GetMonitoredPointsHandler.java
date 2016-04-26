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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;

import org.sigmah.server.dao.MonitoredPointDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.reminder.MonitoredPoint;
import org.sigmah.server.domain.util.DomainFilters;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.shared.command.GetMonitoredPoints;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

/**
 * Handler for the {@link GetMonitoredPoints} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetMonitoredPointsHandler extends AbstractCommandHandler<GetMonitoredPoints, ListResult<MonitoredPointDTO>> {
	private final OrgUnitDAO orgUnitDAO;
	private final ProjectDAO projectDAO;
	private final MonitoredPointDAO monitoredPointDAO;

	@Inject
	GetMonitoredPointsHandler(OrgUnitDAO orgUnitDAO, ProjectDAO projectDAO, MonitoredPointDAO monitoredPointDAO) {
		this.orgUnitDAO = orgUnitDAO;
		this.projectDAO = projectDAO;
		this.monitoredPointDAO = monitoredPointDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<MonitoredPointDTO> execute(final GetMonitoredPoints cmd, final UserExecutionContext context) throws CommandException {

		final List<MonitoredPointDTO> dtos;

		if (cmd.getProjectId() != null) {
			dtos = findProjectPoints(cmd.getProjectId(), cmd.getMappingMode(), context);

		} else {
			dtos = findAllProjectsPoints(cmd.getOrgUnitIds(), cmd.getMappingMode(), context);
		}

		return new ListResult<>(dtos);
	}

	/**
	 * Finds the given {@code projectId} corresponding monitored points.
	 * 
	 * @param projectId
	 *          The project id.
	 * @param mappingMode
	 *          The mapping mode, may be {@code null}.
	 * @param context
	 *          The user execution context.
	 * @return The monitored points DTOs.
	 */
	private List<MonitoredPointDTO> findProjectPoints(final Integer projectId, final MonitoredPointDTO.Mode mappingMode, final UserExecutionContext context) {

		// Disable the ActivityInfo filter on Userdatabase.
		DomainFilters.disableUserFilter(em());
		
		final Query query = em().createQuery("SELECT p.pointsList.points FROM Project p WHERE p.id = :projectId");
		query.setParameter("projectId", projectId);

		@SuppressWarnings("unchecked")
		final List<MonitoredPoint> monitoredPoints = query.getResultList();

		return new ArrayList<>(mapper().mapCollection(monitoredPoints, MonitoredPointDTO.class, mappingMode));
	}

	/**
	 * Finds the monitored points for all the projects.
	 * 
	 * @param mappingMode
	 *          The mapping mode, may be {@code null}.
	 * @param context
	 *          The user execution context.
	 * @return The monitored points DTOs.
	 */
	private List<MonitoredPointDTO> findAllProjectsPoints(Set<Integer> orgUnitIds, MonitoredPointDTO.Mode mappingMode, UserExecutionContext context) {
		List<OrgUnit> orgUnits = orgUnitDAO.findByIds(orgUnitIds);
		// Use a set to avoid duplicated entries.
		final Set<Integer> crawledOrgUnitIds = new HashSet<>();

		// Crawl the org units hierarchy from the user root org unit.
		for (OrgUnit orgUnit : orgUnits) {
			final List<OrgUnit> crawledOrgUnits = new ArrayList<>();
			Handlers.crawlUnits(orgUnit, crawledOrgUnits, true);

			for (OrgUnit crawledOrgUnit : crawledOrgUnits) {
				crawledOrgUnitIds.add(crawledOrgUnit.getId());
			}
		}

		List<Project> projects = projectDAO.findProjectByTeamMemberIdAndOrgUnitIds(context.getUser().getId(), crawledOrgUnitIds);
		Set<Integer> visibleProjectIds = new HashSet<>();
		for (Project project : projects) {
			if (Handlers.isProjectVisible(project, context.getUser())) {
				visibleProjectIds.add(project.getId());
			}
		}

		List<MonitoredPoint> monitoredPoints = monitoredPointDAO.findNotCompletedByProjectIds(visibleProjectIds);
		List<MonitoredPointDTO> monitoredPointDTOs = new ArrayList<>(monitoredPoints.size());
		for (MonitoredPoint monitoredPoint : monitoredPoints) {
			MonitoredPointDTO monitoredPointDTO = mapper().map(monitoredPoint, new MonitoredPointDTO(), mappingMode);

			// FIXME: Put this code into a DAO
			TypedQuery<Project> fullNameQuery = em().createQuery("SELECT p FROM Project p WHERE p.pointsList = :pointsList", Project.class);
			fullNameQuery.setParameter("pointsList", monitoredPoint.getParentList());
			Project project = fullNameQuery.getSingleResult();
			monitoredPointDTO.setProjectId(project.getId());
			monitoredPointDTO.setProjectName(project.getName());
			monitoredPointDTO.setProjectCode(project.getFullName());
			monitoredPointDTOs.add(monitoredPointDTO);
		}

		return monitoredPointDTOs;
	}

}
