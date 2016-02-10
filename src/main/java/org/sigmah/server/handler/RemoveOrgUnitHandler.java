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


import java.util.Date;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.RemoveOrgUnit;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dispatch.FunctionalException.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Handler for {@link RemoveOrgUnit} command.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class RemoveOrgUnitHandler extends AbstractCommandHandler<RemoveOrgUnit, VoidResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(RemoveOrgUnitHandler.class);

	/**
	 * Injected {@link OrgUnitDAO}.
	 */
	@Inject
	private OrgUnitDAO orgUnitDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final RemoveOrgUnit cmd, final UserExecutionContext context) throws CommandException {

		final Integer id = cmd.getId();
		if (id == null) {
			throw new CommandException("Invalid OrgUnit id.");
		}

		// --
		// Retrieves the removed unit.
		// --

		final OrgUnit removed = orgUnitDAO.findById(id);
		if (removed == null) {
			throw new CommandException("The org unit with id '" + id + "' doesn't exist.");
		}

		// --
		// Is root?
		// --

		if (removed.getParentOrgUnit() == null) {
			LOG.warn("The org unit with id '{}' is the root unit and cannot be removed.", id);
			throw new FunctionalException(ErrorCode.ADMIN_REMOVE_ORG_UNIT_IS_ROOT);
		}

		// --
		// Has children?
		// --

		if (removed.getChildrenOrgUnits() != null && removed.getChildrenOrgUnits().size() > 0) {
			LOG.warn("The org unit with id '{}' has children and cannot be removed.", id);
			throw new FunctionalException(ErrorCode.ADMIN_REMOVE_ORG_UNIT_HAS_CHILDREN);
		}

		// --
		// Has related projects?
		// --

		final TypedQuery<Number> query = em().createQuery("SELECT COUNT(p) FROM Project p WHERE :unit MEMBER OF p.partners", Number.class);
		query.setParameter("unit", removed);

		final Number projectsCount = query.getSingleResult();

		if (projectsCount.intValue() > 0) {
			LOG.warn("The org unit with id '{}' has projects and cannot be removed.", id);
			throw new FunctionalException(ErrorCode.ADMIN_REMOVE_ORG_UNIT_HAS_PROJECTS);
		}

		// --
		// Logical remove.
		// --

		removeOrgUnit(removed);

		return new VoidResult();
	}
	
	/**
	 * Uses a transaction to really remove the given <code>orgUnit</code>.
	 * @param orgUnit Organizational unit to remove.
	 */
	@Transactional
	protected void removeOrgUnit(OrgUnit orgUnit) {
		orgUnit.setDeleted(new Date());
		em().merge(orgUnit);
	}
}
