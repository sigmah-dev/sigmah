package org.sigmah.server.handler;

import org.sigmah.server.dao.AuthenticationDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.security.Authenticator;
import org.sigmah.shared.command.LoginCommand;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dispatch.CommandException;

import com.google.inject.Inject;

/**
 * Handler for {@link LoginCommand}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LoginCommandHandler extends AbstractCommandHandler<LoginCommand, Authentication> {

	/**
	 * Injected {@link Authenticator} service.
	 */
	private final Authenticator authenticator;

	/**
	 * Injected {@link AuthenticationDAO}.
	 */
	private final AuthenticationDAO authenticationDAO;

	/**
	 * Injected {@link Mapper}.
	 */
	private final Mapper mapper;

	@Inject
	public LoginCommandHandler(final Authenticator authenticator, final AuthenticationDAO authenticationDAO, final Mapper mapper) {
		this.authenticator = authenticator;
		this.authenticationDAO = authenticationDAO;
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Authentication execute(final LoginCommand command, final UserExecutionContext context) throws CommandException {

		// Authenticates received credentials.
		final User user = authenticator.authenticate(command.getLogin(), command.getPassword());

		final org.sigmah.server.domain.Authentication newAuth = authenticationDAO.persist(new org.sigmah.server.domain.Authentication(user), user);

		final Authentication authentication = Handlers.createAuthentication(user, command.getLanguage(), mapper);
		authentication.setAuthenticationToken(newAuth.getId());

		return authentication;
	}

}
