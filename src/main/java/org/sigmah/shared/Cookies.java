package org.sigmah.shared;

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
