package org.sigmah.shared;

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

/**
 * List of the cookies used by the application.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class Cookies {

	/**
	 * Authentication token cookie name.
	 */
	private static final String COOKIE_NAME_PREFIX = "sigmah-";

	/**
	 * Authentication token cookie name.
	 */
	public static final String AUTH_TOKEN_COOKIE = COOKIE_NAME_PREFIX + "authToken";

	/**
	 * Current language cookie name.
	 */
	public static final String LANGUAGE_COOKIE = COOKIE_NAME_PREFIX + "language";

	/**
	 * Cookies path.
	 */
	public static final String COOKIE_PATH = "/";

	/**
	 * Cookies domain.
	 */
	public static final String COOKIE_DOMAIN = null;

	/**
	 * Secured cookies ?
	 */
	public static final boolean COOKIE_SECURED = false;

	protected Cookies() {
		// Only provides static methods.
	}

}
