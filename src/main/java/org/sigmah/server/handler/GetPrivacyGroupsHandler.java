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

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetPrivacyGroups;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

/**
 * Handler for {@link GetPrivacyGroups} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetPrivacyGroupsHandler extends AbstractCommandHandler<GetPrivacyGroups, ListResult<PrivacyGroupDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<PrivacyGroupDTO> execute(GetPrivacyGroups cmd, final UserExecutionContext context) throws CommandException {

		final TypedQuery<PrivacyGroup> query = em().createQuery("SELECT p FROM PrivacyGroup p WHERE p.organization.id = :orgid ORDER BY p.id", PrivacyGroup.class);
		query.setParameter("orgid", context.getUser().getOrganization().getId());

		final List<PrivacyGroup> resultPrivacyGroups = query.getResultList();

		return new ListResult<PrivacyGroupDTO>(mapper().mapCollection(resultPrivacyGroups, PrivacyGroupDTO.class));
	}

}
