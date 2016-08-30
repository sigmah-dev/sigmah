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
import org.sigmah.server.domain.UserPermission;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUsers;
import org.sigmah.shared.command.result.UserResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserPermissionDTO;

import com.extjs.gxt.ui.client.Style;
import java.util.ArrayList;

/**
 * Handler for {@link GetUsers} command.
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetUsersHandler extends AbstractCommandHandler<GetUsers, UserResult> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserResult execute(final GetUsers cmd, final UserExecutionContext context) throws CommandException {

		String orderByClause = "";

		if (cmd.getSortInfo().getSortDir() != Style.SortDir.NONE) {
			String dir = cmd.getSortInfo().getSortDir() == Style.SortDir.ASC ? "asc" : "desc";
			String property = null;
			String field = cmd.getSortInfo().getSortField();

			if ("name".equals(field)) {
				property = "up.user.name";
			} else if ("email".equals(field)) {
				property = "up.user.email";
			} else if ("partner".equals(field)) {
				property = "up.partner.name";
			} else if (field != null && field.startsWith("allow")) {
				property = "up." + field;
			}

			if (property != null) {
				orderByClause = " order by " + property + " " + dir;
			}
		}

		final TypedQuery<UserPermission> query =
				em().createQuery("select up from UserPermission up where  up.database.id = :dbId and up.user.id <> :currentUserId " + orderByClause,
					UserPermission.class);
		query.setParameter("dbId", cmd.getDatabaseId());
		query.setParameter("currentUserId", context.getUser().getId());

		if (cmd.getOffset() > 0) {
			query.setFirstResult(cmd.getOffset());
		}
		if (cmd.getLimit() > 0) {
			query.setMaxResults(cmd.getLimit());
		}

		final List<UserPermission> perms = query.getResultList();
		final ArrayList<UserPermissionDTO> models = new ArrayList<>();
		
		for (final UserPermission permission : perms) {
			models.add(mapper().map(permission, new UserPermissionDTO()));
		}

		final TypedQuery<Number> countQuery =
				em().createQuery("select count(up) from UserPermission up where up.database.id = :dbId and up.user.id <> :currentUserId ", Number.class);
		countQuery.setParameter("dbId", cmd.getDatabaseId());
		countQuery.setParameter("currentUserId", context.getUser().getId());

		final int totalCount = countQuery.getSingleResult().intValue();

		return new UserResult(models, cmd.getOffset(), totalCount);
	}
}
