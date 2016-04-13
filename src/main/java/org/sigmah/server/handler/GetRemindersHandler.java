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

import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.ReminderDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.reminder.Reminder;
import org.sigmah.server.domain.util.DomainFilters;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.reminder.ReminderDTO;

/**
 * Handler for the {@link GetReminders} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetRemindersHandler extends AbstractCommandHandler<GetReminders, ListResult<ReminderDTO>> {
	private final OrgUnitDAO orgUnitDAO;
	private final ProjectDAO projectDAO;
	private final ReminderDAO reminderDAO;

	@Inject
	GetRemindersHandler(OrgUnitDAO orgUnitDAO, ProjectDAO projectDAO, ReminderDAO reminderDAO) {
		this.orgUnitDAO = orgUnitDAO;
		this.projectDAO = projectDAO;
		this.reminderDAO = reminderDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ReminderDTO> execute(final GetReminders cmd, final UserExecutionContext context) throws CommandException {

		final List<ReminderDTO> dtos;

		if (cmd.getProjectId() != null) {
			dtos = findProjectReminders(cmd.getProjectId(), cmd.getMappingMode(), context);

		} else {
			dtos = findAllProjectsReminders(cmd.getOrgUnitIds(), cmd.getMappingMode(), context);
		}

		return new ListResult<>(dtos);
	}

	/**
	 * Finds the given {@code projectId} corresponding reminders.
	 * 
	 * @param projectId
	 *          The project id.
	 * @param mappingMode
	 *          The mapping mode, may be {@code null}.
	 * @param context
	 *          The user execution context.
	 * @return The reminders DTOs.
	 */
	private List<ReminderDTO> findProjectReminders(final Integer projectId, final ReminderDTO.Mode mappingMode, final UserExecutionContext context) {

		// Disable the ActivityInfo filter on Userdatabase.
		DomainFilters.disableUserFilter(em());
		
		final Query query = em().createQuery("SELECT p.remindersList.reminders FROM Project p WHERE p.id = :projectId");
		query.setParameter("projectId", projectId);

		@SuppressWarnings("unchecked")
		final List<Reminder> reminders = query.getResultList();

		return new ArrayList<ReminderDTO>(mapper().mapCollection(reminders, ReminderDTO.class, mappingMode));
	}

	/**
	 * Finds the reminders for all the projects.
	 * 
	 * @param mappingMode
	 *          The mapping mode, may be {@code null}.
	 * @param context
	 *          The user execution context.
	 * @return The reminders DTOs.
	 */
	private List<ReminderDTO> findAllProjectsReminders(Set<Integer> orgUnitIds, ReminderDTO.Mode mappingMode, UserExecutionContext context) {
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

		List<Reminder> reminders = reminderDAO.findNotCompletedByProjectIds(visibleProjectIds);
		return mapper().mapCollection(reminders, ReminderDTO.class, mappingMode);
	}

}
