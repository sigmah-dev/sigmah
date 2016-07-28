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


import com.google.inject.persist.Transactional;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.domain.UserPermission;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.RemovePartner;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link RemovePartner} command
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
@Deprecated
public class RemovePartnerHandler extends AbstractCommandHandler<RemovePartner, VoidResult> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final RemovePartner cmd, final UserExecutionContext context) throws CommandException {

		// verify the current user has access to this site.
		final UserDatabase db = em().find(UserDatabase.class, cmd.getDatabaseId());
		if (!db.getOwner().getId().equals(context.getUser().getId())) {
			final UserPermission perm = db.getPermissionByUser(context.getUser());
			if (perm == null || perm.isAllowDesign()) {
				throw new CommandException("Illegal access exception.");
			}
		}

		// Check to see if there are already sites associated with this partner.
		final TypedQuery<Number> countQuery =
				em().createQuery(
					"select count(s) from Site s where "
						+ "s.activity.id in (select a.id from Activity a where a.database.id = :dbId) and "
						+ "s.partner.id = :partnerId and "
						+ "s.dateDeleted is null", Number.class);

		countQuery.setParameter("dbId", cmd.getDatabaseId());
		countQuery.setParameter("partnerId", cmd.getPartnerId());

		final int siteCount = countQuery.getSingleResult().intValue();

		if (siteCount > 0) {
			throw new CommandException("Partner has sites exception.");
		}

		removePartner(db, cmd.getPartnerId());

		return new VoidResult();
	}
	
	@Transactional
	protected void removePartner(UserDatabase db, int partnerId) {
		db.getPartners().remove(em().getReference(OrgUnit.class, partnerId));
		// NOTE: Call to merge added. Needs to verify if really needed.
		em().merge(db);
	}
}
