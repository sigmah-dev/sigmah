package org.sigmah.server.security.impl;

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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.sigmah.client.page.Page;
import org.sigmah.client.security.SecureDispatchAsync.CommandExecution;
import org.sigmah.server.dao.AuthenticationDAO;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.domain.User;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.security.SecureSessionValidator;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * <p>
 * The {@link SecureSessionValidator} implementation.
 * </p>
 * <p>
 * This service is used to validate dispatch servlet commands <em>and</em> additional servlet(s) processes.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.server.dispatch.SecureDispatchServlet
 * @see org.sigmah.server.servlet.base.AbstractServlet
 */
public class AuthenticationSecureSessionValidator implements SecureSessionValidator {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationSecureSessionValidator.class);

	/**
	 * The injected {@code AuthenticationDAO}.
	 */
	private final AuthenticationDAO authenticationDAO;

	/**
	 * The injected {@code Mapper}.
	 */
	private final Mapper mapper;

	/**
	 * AuthenticationSecureSessionValidator initialization.
	 * 
	 * @param authenticationDAO
	 *          Injected DAO.
	 */
	@Inject
	public AuthenticationSecureSessionValidator(final AuthenticationDAO authenticationDAO, final Mapper mapper) {
		this.authenticationDAO = authenticationDAO;
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Access validate(final String authenticationToken, final Servlet servlet, final ServletMethod method, final String originPageToken) {
		return validate(authenticationToken, AccessRights.servletToken(servlet, method), originPageToken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Access validate(final String authenticationToken, final CommandExecution<? extends Command<?>, ? extends Result> commandExecution) {
		return validate(authenticationToken, AccessRights.commandToken(commandExecution.getCommand().getClass()), commandExecution.getCurrentPageToken());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUserGranted(final User user, final Page page) {
		return isUserGranted(user, AccessRights.pageToken(page), null);
	}

	/**
	 * Validates the access to the given {@code resourceToken} resource for the {@code authenticationToken}.
	 * 
	 * @param authenticationToken
	 *          The authentication token.
	 * @param resourceToken
	 *          The resource token.
	 * @param originPageToken
	 *          The origin page token.
	 * @return The validation access result.
	 */
	private Access validate(final String authenticationToken, final String resourceToken, final String originPageToken) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Starting validation of authentication token '{}' for resource '{}'.", authenticationToken, resourceToken);
		}

		try {

			if (StringUtils.isBlank(authenticationToken) || "null".equalsIgnoreCase(authenticationToken)) {

				if (LOG.isTraceEnabled()) {
					LOG.trace("No authentication token (anonymous user): '{}'.", authenticationToken);
				}

				final boolean commandAuthorizedForAnonymous = isUserGranted(null, resourceToken, originPageToken);

				if (commandAuthorizedForAnonymous) {
					if (LOG.isTraceEnabled()) {
						LOG.trace("ACCESS GRANTED for authentication token '{}'.", authenticationToken);
					}
					return new Access(AccessType.ACCESS_GRANTED, null);

				} else {
					if (LOG.isTraceEnabled()) {
						LOG.trace("ACCESS UNAUTHORIZED for authentication token '{}'.", authenticationToken);
					}
					return new Access(AccessType.UNAUTHORIZED_ACCESS, null);
				}
			}

			// Retrieves the authentication token corresponding user.
			final Authentication authentication = authenticationDAO.findById(authenticationToken);

			// Invalid token ?
			if (authentication == null) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("ACCESS UNAUTHORIZED - Invalid session, no Authentication found in database for token '{}'.", authenticationToken);
				}
				return new Access(AccessType.INVALID_SESSION, null);
			}

			if (authentication.getUser() == null) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("ACCESS UNAUTHORIZED - Invalid session, no User for Authentification token '{}'.", authenticationToken);
				}
				return new Access(AccessType.INVALID_SESSION, null);
			}

			final User user = authentication.getUser(); // Cannot be null at this point.

			final boolean processAuthorizedForUser = isUserGranted(user, resourceToken, originPageToken);

			if (processAuthorizedForUser) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("ACCESS GRANTED - User '{}' is granted to execute process.", user);
				}

				return new Access(AccessType.ACCESS_GRANTED, user);
			}

			if (LOG.isTraceEnabled()) {
				LOG.trace("ACCESS UNAUTHORIZED - User '{}' does not have required permission to execute process.", user);
			}

			return new Access(AccessType.UNAUTHORIZED_ACCESS, user);

		} catch (final Throwable e) {

			if (LOG.isErrorEnabled()) {
				LOG.error("Error while validating the authentication token '" + authenticationToken + "'.", e);
			}

			return new Access(AccessType.INVALID_SESSION, null);
		}
	}

	/**
	 * Returns the grant access to the given {@code resourceToken} for the {@code user}.
	 * 
	 * @param user
	 *          The user.
	 * @param resourceToken
	 *          The resource token to secure.
	 * @param originPageToken
	 *          The origin page token, may be {@code null}.
	 * @return {@code true} if the {@code user} is granted to access {@code resourceToken}, {@code false} otherwise.
	 */
	private boolean isUserGranted(final User user, final String resourceToken, final String originPageToken) {

		if (user != null && BooleanUtils.isFalse(user.getActive())) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("User '{}' cannot access resource '{}' because it is no longer active.", user, resourceToken);
			}
			return false;
		}

		return AccessRights.isGranted(user, resourceToken, originPageToken, mapper);
	}

}
