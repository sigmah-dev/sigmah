package org.sigmah.server.handler;

import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUsersWithProfiles;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves the organization users list.
 * 
 * @author nrebiai (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetUsersWithProfilesHandler extends AbstractCommandHandler<GetUsersWithProfiles, ListResult<UserDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetUsersWithProfilesHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<UserDTO> execute(final GetUsersWithProfiles cmd, final UserExecutionContext context) throws CommandException {

		// --
		// Retrieves users.
		// --

		final TypedQuery<User> query = em().createQuery("SELECT u FROM User u WHERE u.organization = :org ORDER BY u.name", User.class);
		query.setParameter("org", context.getUser().getOrganization());

		final List<User> users = query.getResultList();

		// --
		// Builds result.
		// --

		final List<UserDTO> dtos = mapper().mapCollection(users, UserDTO.class, UserDTO.Mode.WITH_BASE_ORG_UNIT_AND_BASE_PROFILES);

		LOG.debug("Found {} users.", dtos.size());

		return new ListResult<UserDTO>(dtos);
	}

}
