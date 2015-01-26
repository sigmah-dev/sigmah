package org.sigmah.server.handler;

import org.sigmah.client.page.Page;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.security.SecureSessionValidator;
import org.sigmah.shared.command.SecureNavigationCommand;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.SecureNavigationResult;
import org.sigmah.shared.dispatch.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler for {@link SecureNavigationCommand}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SecureNavigationCommandHandler extends AbstractCommandHandler<SecureNavigationCommand, SecureNavigationResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SecureNavigationCommandHandler.class);

	/**
	 * Injected {@link SecureSessionValidator}.
	 */
	private final SecureSessionValidator secureSessionValidator;

	/**
	 * Injected {@link Mapper}.
	 */
	private final Mapper mapper;

	@Inject
	public SecureNavigationCommandHandler(final SecureSessionValidator secureSessionValidator, final Mapper mapper) {
		this.secureSessionValidator = secureSessionValidator;
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SecureNavigationResult execute(final SecureNavigationCommand command, final UserExecutionContext context) throws CommandException {

		final User user = context.getUser();
		final Page page = command.getPage();

		final boolean granted = secureSessionValidator.isUserGranted(user, page);

		if (LOG.isTraceEnabled()) {
			if (granted) {
				LOG.trace("ACCESS GRANTED to page '{}' by user '{}'.", page, user);
			} else {
				LOG.trace("ACCESS UNAUTHORIZED to page '{}' by user '{}'.", page, user);
			}
		}

		final Authentication authentication = Handlers.createAuthentication(user, context.getLanguage(), mapper);

		return new SecureNavigationResult(granted, authentication);
	}

}
