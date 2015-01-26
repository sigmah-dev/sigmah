package org.sigmah.server.i18n;

import org.sigmah.shared.Language;

/**
 * Offers the access to all text constant files defined in the server side.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface I18nServer {

	/**
	 * Return the given {@code language} and {@code key} corresponding value.
	 * 
	 * @param language
	 *          The language.
	 * @param key
	 *          The value key.
	 * @return The given {@code language} and {@code key} corresponding value.
	 */
	String t(final Language language, final String key);

	/**
	 * Return the given {@code language} and {@code key} corresponding value, completed with the {@code parameters}.
	 * 
	 * @param language
	 *          The language.
	 * @param key
	 *          The value key.
	 * @param parameters
	 *          Parameters used to fill dynamic fields of the value.
	 * @return The given {@code language} and {@code key} corresponding value, completed with the {@code parameters}.
	 */
	String t(final Language language, final String key, final Object... parameters);

}
