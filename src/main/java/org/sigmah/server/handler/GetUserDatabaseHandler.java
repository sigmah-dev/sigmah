package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUserDatabase;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDatabaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetUserDatabase} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetUserDatabaseHandler extends AbstractCommandHandler<GetUserDatabase, ListResult<UserDatabaseDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetUserDatabaseHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<UserDatabaseDTO> execute(final GetUserDatabase cmd, final UserExecutionContext context) throws CommandException {

		final List<UserDatabaseDTO> userDatabaseDTOList = new ArrayList<UserDatabaseDTO>();

		// Creates selection query.
		final TypedQuery<UserDatabase> query = em().createQuery("SELECT u FROM UserDatabase u  WHERE u.owner = :userId ORDER BY u.id", UserDatabase.class);
		query.setParameter("userId", context.getUser().getId());

		// Gets all users entities.
		final List<UserDatabase> dbs = query.getResultList();

		// Mapping (entity -> dto).
		if (dbs != null) {
			for (final UserDatabase oneDB : dbs) {
				userDatabaseDTOList.add(mapper().map(oneDB, new UserDatabaseDTO()));
			}
		}

		LOG.debug("Found {} databases.", userDatabaseDTOList.size());

		return new ListResult<UserDatabaseDTO>(userDatabaseDTOList);
	}
}
