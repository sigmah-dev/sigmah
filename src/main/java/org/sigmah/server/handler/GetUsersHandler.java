package org.sigmah.server.handler;

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
 * Handler for {@link GetUsers} command
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
