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

import java.util.List;
import java.util.Set;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.domain.UserPermission;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.AddPartner;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.inject.Inject;

/**
 * Adds a partner (ie {@link OrgUnit}) from {@link AddPartner} command.
 * 
 * @author Alex Bertram (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class AddPartnerHandler extends AbstractCommandHandler<AddPartner, CreateResult> {

	private final Mapper mapper;

	@Inject
	public AddPartnerHandler(final Mapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CreateResult execute(final AddPartner cmd, final UserExecutionContext context) throws CommandException {

		final UserDatabase db = em().find(UserDatabase.class, cmd.getDatabaseId());

		if (!db.getOwner().getId().equals(context.getUser().getId())) {
			UserPermission perm = db.getPermissionByUser(context.getUser());
			if (perm == null || !perm.isAllowManageAllUsers()) {
				throw new CommandException("Illegal Access : The user does not have the manageAllUsers permission.");
			}
		}

		// First check to see if an organization by this name is already a partner.
		final Set<OrgUnit> dbPartners = db.getPartners();

		for (OrgUnit partner : dbPartners) {
			if (partner.getName().equals(cmd.getPartner().getName())) {
				throw new CommandException("Duplicate Exception");
			}
		}

		// Now try to match this partner by name.
		@SuppressWarnings("unchecked")
		List<OrgUnit> allPartners = em().createQuery("select p from OrgUnit p where p.name = ?1").setParameter(1, cmd.getPartner().getName()).getResultList();

		if (allPartners.size() != 0) {
			db.getPartners().add(allPartners.get(0));
			return new CreateResult(mapper.map(allPartners.get(0), new OrgUnitDTO(), OrgUnitDTO.Mode.BASE));
		}

		// Nope, have to create a new record.
		final OrgUnit newPartner = new OrgUnit();
		newPartner.setName(cmd.getPartner().getName());
		newPartner.setFullName(cmd.getPartner().getFullName());
		em().persist(newPartner);

		db.getPartners().add(newPartner);

		return new CreateResult(mapper.map(newPartner, new OrgUnitDTO(), OrgUnitDTO.Mode.BASE));
	}
}
