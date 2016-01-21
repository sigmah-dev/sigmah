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
