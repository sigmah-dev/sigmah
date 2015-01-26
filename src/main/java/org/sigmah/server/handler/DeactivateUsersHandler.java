package org.sigmah.server.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeactivateUsers;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.util.List;

/**
 * Deactivates or activates the given users.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeactivateUsersHandler extends AbstractCommandHandler<DeactivateUsers, VoidResult> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(DeactivateUsersHandler.class);

	private final UserDAO userDAO;

	@Inject
	public DeactivateUsersHandler(final UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final DeactivateUsers cmd, final UserExecutionContext context) throws CommandException {

		if (CollectionUtils.isEmpty(cmd.getUsers())) {
			return new VoidResult();
		}

		performDeactivation(cmd.getUsers(), context);

		return new VoidResult();
	}

	/**
	 * Deactivate the given users in a transaction.
	 * 
	 * @param users List of users to deactivate. 
	 * @param context Execution context.
	 */
	@Transactional
	protected void performDeactivation(final List<UserDTO> users, final UserExecutionContext context) {
		for (final UserDTO userDTO : users) {
			LOG.debug("Updating activated state of the following user : {}", userDTO);

			User user = userDAO.findById(userDTO.getId());
			if (user == null) {
				user = userDAO.findUserByEmail(userDTO.getEmail());
			}

			user.setActive(!userDTO.getActive());
			userDAO.persist(user, context.getUser());
		}
	}

}
