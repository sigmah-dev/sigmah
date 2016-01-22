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

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.shared.command.GetOrgUnitsByModel;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * {@link GetOrgUnitsByModel} command execution.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetOrgUnitsByModelHandler extends AbstractCommandHandler<GetOrgUnitsByModel, ListResult<OrgUnitDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<OrgUnitDTO> execute(final GetOrgUnitsByModel cmd, final UserExecutionContext context) throws CommandException {

		if (cmd == null || cmd.getOrgUnitModelId() == null) {
			throw new CommandException("Invalid command arguments.");
		}

		final TypedQuery<OrgUnit> query = em().createQuery("SELECT o from OrgUnit o WHERE o.orgUnitModel.id = :orgUnitId", OrgUnit.class);
		query.setParameter("orgUnitId", cmd.getOrgUnitModelId());

		final List<OrgUnit> orgUnitList = query.getResultList();
		final List<OrgUnitDTO> orgUnitDTOList = new ArrayList<OrgUnitDTO>();

		if (orgUnitList == null) {
			return null;
		}

		for (OrgUnit orgUnit : orgUnitList) {
			if (isOrgUnitVisible(orgUnit, context.getUser())) {
				orgUnitDTOList.add(mapper().map(orgUnit, new OrgUnitDTO(), cmd.getMappingMode()));
			}
		}

		return new ListResult<OrgUnitDTO>(orgUnitDTOList);
	}

	/**
	 * Returns if the org unit is visible for the given user.
	 * 
	 * @param orgUnit
	 *          The org unit.
	 * @param user
	 *          The user.
	 * @return If the org unit is visible for the user.
	 */
	private static boolean isOrgUnitVisible(final OrgUnit orgUnit, final User user) {

		if (orgUnit.getDeleted() != null) {
			return false;
		}

		// Checks that the user can see this org unit.
		final HashSet<OrgUnit> units = new HashSet<OrgUnit>();
		Handlers.crawlUnits(user.getOrgUnitWithProfiles().getOrgUnit(), units, true);

		for (final OrgUnit unit : units) {
			if (orgUnit.getId().equals(unit.getId())) {
				return true;
			}
		}

		return false;
	}

}
