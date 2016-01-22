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


import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.service.UserPermissionPolicy;
import org.sigmah.shared.command.MoveOrgUnit;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dispatch.FunctionalException.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.sigmah.server.domain.User;

/**
 * Handler for {@link MoveOrgUnit} command.
 * 
 * @author Tom Miette (tmiette@ideia.fr) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class MoveOrgUnitHandler extends AbstractCommandHandler<MoveOrgUnit, VoidResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MoveOrgUnitHandler.class);

	private final OrgUnitDAO orgUnitDAO;
	private final UserPermissionPolicy permissionPolicy;

	@Inject
	public MoveOrgUnitHandler(OrgUnitDAO orgUnitDAO, UserPermissionPolicy permissionPolicy) {
		this.orgUnitDAO = orgUnitDAO;
		this.permissionPolicy = permissionPolicy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final MoveOrgUnit cmd, final UserExecutionContext context) throws CommandException {

		// Controlling command arguments.
		final Integer id = cmd.getId();
		final Integer parentId = cmd.getParentId();

		if (id == null || parentId == null) {
			throw new CommandException("Invalid command arguments.");
		}

		// Retrieves the moved unit.
		final OrgUnit moved = orgUnitDAO.findById(id);
		if (moved == null) {
			throw new CommandException("The org unit with id '" + id + "' doesn't exist.");
		}

		// Retrieves the parent unit.
		final OrgUnit parent = orgUnitDAO.findById(parentId);
		if (parent == null) {
			throw new CommandException("The org unit with id '" + parentId + "' doesn't exist.");
		}

		// Do not move an org unit as the parent of itself.
		if (id.equals(parentId)) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Cannot move an org unit as its own child.");
			}
			throw new FunctionalException(ErrorCode.ADMIN_MOVE_ORG_UNIT_ITSELF_AS_PARENT);
		}

		// Checks that my new parent is not already one of my child !
		final boolean theKidIsMySon = theKidIsMySon(moved, parentId);
		if (theKidIsMySon) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Cycle detected: cannot move an org unit as a child of one of its children.");
			}
			throw new FunctionalException(ErrorCode.ADMIN_MOVE_ORG_UNIT_CYCLE_DETECTED);
		}

		// Performs the move.
		moved.setParentOrgUnit(parent);
		performMove(moved, context.getUser());

		return new VoidResult();
	}
	
	@Transactional
	protected void performMove(OrgUnit moonWalk, User michael) throws CommandException {
		orgUnitDAO.persist(moonWalk, michael);

		// [UserPermission trigger] Updates UserPermission table when org unit changes its parent.
		permissionPolicy.deleteUserPermssionByOrgUnit(moonWalk);
		permissionPolicy.updateUserPermissionByOrgUnit(moonWalk);
	}

	/**
	 * Let's sing...
	 * 
	 * @param me
	 *          Mickael J.
	 * @param theKidName
	 *          Billie Jean son's name.
	 * @see Thriller
	 * @since 1982
	 */
	// Wouaaww!! nice work Tom Miette... Is that what you were doing all day at Ideia? ;-)
	private static boolean theKidIsMySon(final OrgUnit me, final Integer theKidName) {

		boolean sheSaysIAmTheOne = false;

		if (me.getChildrenOrgUnits() != null) {

			// For each of my sons.
			for (final OrgUnit son : me.getChildrenOrgUnits()) {

				// My son ?
				if (son.getId().equals(theKidName)) {
					sheSaysIAmTheOne = true;
				}
				// Son of my son ?
				else {
					sheSaysIAmTheOne = theKidIsMySon(son, theKidName);
				}

				// Damn it, Billie Jean was right...
				if (sheSaysIAmTheOne) {
					break;
				}

			}

		}

		return sheSaysIAmTheOne;

	}
}
