package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetUsersByOrganization} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetUsersByOrganizationHandler extends AbstractCommandHandler<GetUsersByOrganization, ListResult<UserDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetUsersByOrganizationHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<UserDTO> execute(final GetUsersByOrganization cmd, final UserExecutionContext context) throws CommandException {

		final List<UserDTO> userDTOList = new ArrayList<UserDTO>();

		LOG.debug("Gets users for organization #{}.", cmd.getOrganizationId());

		final Integer userId = cmd.getUserId();

		if (userId == null) {

			final TypedQuery<User> q = em().createQuery("SELECT u FROM User u WHERE u.organization.id = :orgid", User.class);
			q.setParameter("orgid", cmd.getOrganizationId());

			final List<User> users = q.getResultList();

			if (users != null) {
				for (final User u : users) {
					final UserDTO userDTO = mapper().map(u, new UserDTO(), cmd.getMappingMode());
					userDTO.setCompleteName(userDTO.getFirstName() != null ? userDTO.getFirstName() + " " + userDTO.getName() : userDTO.getName());
					userDTOList.add(userDTO);
				}
			}

		} else {

			final TypedQuery<User> q = em().createQuery("SELECT u FROM User u WHERE u.id = :userid AND u.organization.id = :orgid", User.class);
			q.setParameter("userid", userId);
			q.setParameter("orgid", cmd.getOrganizationId());

			try {
				final User u = q.getSingleResult();
				final UserDTO userDTO = mapper().map(u, new UserDTO(), cmd.getMappingMode());
				userDTO.setCompleteName(userDTO.getFirstName() != null ? userDTO.getFirstName() + " " + userDTO.getName() : userDTO.getName());
				userDTOList.add(userDTO);
			} catch (NoResultException e) {
				// nothing.
			}
		}

		LOG.debug("Found {} users.", userDTOList.size());

		return new ListResult<UserDTO>(userDTOList);
	}
}
