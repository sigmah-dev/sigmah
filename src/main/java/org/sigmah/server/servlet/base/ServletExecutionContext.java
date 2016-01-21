package org.sigmah.server.servlet.base;

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

import javax.servlet.http.HttpServletRequest;

import org.sigmah.server.domain.User;
import org.sigmah.server.util.Languages;
import org.sigmah.shared.Language;

/**
 * Servlet execution context provided to all servlet methods (including dispatch servlet).
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ServletExecutionContext {

	/**
	 * Anonymous user representing a non-authenticated user.<br/>
	 * Its <em>id</em> is {@code null}.
	 */
	public static final User ANONYMOUS_USER;

	static {
		ANONYMOUS_USER = new User();
		ANONYMOUS_USER.setId(null);
		ANONYMOUS_USER.setActive(true);
		ANONYMOUS_USER.setEmail("anonymous@nowhere.com");
		ANONYMOUS_USER.setName("anonymous");
	}

	/**
	 * The user executing command.
	 */
	private final User user;

	/**
	 * The HTTP servlet request.
	 */
	private final HttpServletRequest request;

	/**
	 * Origin page token.
	 */
	private final String originPageToken;

	/**
	 * The user language.
	 */
	private final Language language;

	/**
	 * Initializes a new servlet execution context.
	 * 
	 * @param user
	 *          See {@link #user}.
	 * @param request
	 *          See {@link #request}.
	 * @param originPageToken
	 *          See {@link #originPageToken}.
	 */
	protected ServletExecutionContext(final User user, final HttpServletRequest request, final String originPageToken) {
		this.user = user;
		this.request = request;
		this.originPageToken = originPageToken;
		this.language = Languages.getLanguage(request);
	}

	/**
	 * Returns the user executing the process.
	 * 
	 * @return The user or {@link #ANONYMOUS_USER} (never {@code null}).
	 */
	public final User getUser() {
		return user != null ? user : ANONYMOUS_USER;
	}

	/**
	 * The current HTTP request.
	 * 
	 * @return The current HTTP request.
	 */
	public final HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Returns the page token from which the process has been executed.
	 * 
	 * @return The page token from which the process has been executed or {@code null}.
	 */
	public final String getOriginPageToken() {
		return originPageToken;
	}

	/**
	 * The user language.
	 * 
	 * @return The user language, never {@code null}.
	 */
	public final Language getLanguage() {
		return language;
	}

	/**
	 * Returns if the user is anonymous.
	 * 
	 * @return {@code true} if the user is anonymous, {@code false} otherwise.
	 */
	public final boolean isAnonymous() {
		return user == null || user.equals(ANONYMOUS_USER);
	}

}
