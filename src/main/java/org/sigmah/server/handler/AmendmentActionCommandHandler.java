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


import java.util.Arrays;

import org.sigmah.server.dao.AmendmentDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Amendment;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.service.AmendmentService;
import org.sigmah.shared.command.AmendmentActionCommand;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.referential.AmendmentState;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.shared.dto.referential.AmendmentAction;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.security.UnauthorizedAccessException;

/**
 * Handle actions made on amendments.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class AmendmentActionCommandHandler extends AbstractCommandHandler<AmendmentActionCommand, ProjectDTO> {

	/**
	 * Injected {@link AmendmentService}.
	 */
	@Inject
	private AmendmentService amendmentPolicy;

	/**
	 * Injected {@link ProjectDAO}.
	 */
	@Inject
	private ProjectDAO projectDAO;

	/**
	 * Injected {@link AmendmentDAO}.
	 */
	@Inject
	private AmendmentDAO amendmentDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectDTO execute(final AmendmentActionCommand cmd, final UserExecutionContext context) throws CommandException {
		final Integer projectId = cmd.getProjectId();
		final org.sigmah.shared.dto.referential.AmendmentAction action = cmd.getAction();

		if (projectId == null || action == null) {
			throw new CommandException("Invalid command arguments: " + action);
		}

		final Project project = projectDAO.findById(projectId);

		if (Arrays.binarySearch(project.getAmendmentState().getActions(), action) == -1) {
			throw new IllegalStateException("The action '"
				+ action
				+ "' cannot be applied on the project '"
				+ project.getName()
				+ "' (state "
				+ project.getAmendmentState()
				+ ')');
		}

		performAction(action, project, cmd.getName(), context);

		return mapper().map(project, new ProjectDTO());
	}

	/**
	 * Perform the given action in a transaction.
	 * 
	 * @param action Action to perform.
	 * @param project Project to modify.
	 * @param name Core version name.
	 * @param context Execution context.
	 * @throws org.sigmah.shared.dispatch.CommandException If the action could not be executed.
	 */
	@Transactional
	protected void performAction(final AmendmentAction action, final Project project, final String name, final UserExecutionContext context) throws CommandException {

		switch (action) {
			case LOCK:
				if (!Handlers.isGranted(context.getUser().getOrgUnitsWithProfiles(), project.getOrgUnit(), GlobalPermissionEnum.LOCK_PROJECT)) {
					throw new UnauthorizedAccessException(GlobalPermissionEnum.LOCK_PROJECT + " permission is required to lock projects.");
				}
				project.setAmendmentState(AmendmentState.LOCKED);
				break;

			case UNLOCK:
				if (!Handlers.isGranted(context.getUser().getOrgUnitsWithProfiles(), project.getOrgUnit(), GlobalPermissionEnum.LOCK_PROJECT)) {
					throw new UnauthorizedAccessException(GlobalPermissionEnum.LOCK_PROJECT + " permission is required to unlock projects.");
				}
				project.setAmendmentState(AmendmentState.DRAFT);
				break;

			case VALIDATE:
				// BUGFIX #738: verifying the user rights before validating.
				if (!Handlers.isGranted(context.getUser().getOrgUnitsWithProfiles(), project.getOrgUnit(), GlobalPermissionEnum.VALID_AMENDEMENT)) {
					throw new UnauthorizedAccessException(GlobalPermissionEnum.VALID_AMENDEMENT + " permission is required to validate projects.");
				}
				validateAmendment(project, context);
				createAmendment(project, name);
				break;
				
			default:
				throw new UnsupportedOperationException("Command not supported:" + action);
		}

		// Always persist updated project.
		projectDAO.persist(project, context.getUser());
	}

	protected void createAmendment(final Project project, final String name) {
		// Changes the project state to draft and save the current state as a new amendment.
		final Amendment newAmendment = amendmentPolicy.createAmendment(project, name);
		
		int version = project.getAmendmentVersion() + 1;
		
		// Updating the project
		project.setAmendmentVersion(version);
		project.setAmendmentRevision(1);
		project.setAmendmentState(AmendmentState.LOCKED);
		
		project.getAmendments().add(newAmendment);
	}

	protected void validateAmendment(final Project project, final UserExecutionContext context) {
		// Archive the active state (if one does exist) and activate the current one.
		for (final Amendment amendment : project.getAmendments()) {
			if (amendment.getState() == AmendmentState.ACTIVE) {
				amendment.setState(AmendmentState.ARCHIVED);
				amendmentDAO.persist(amendment, context.getUser());
			}
		}
		
		project.setAmendmentState(AmendmentState.ACTIVE);
	}
}
