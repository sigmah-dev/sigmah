package org.sigmah.server.util;

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


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.sigmah.shared.Cookies;
import org.sigmah.shared.Language;

/**
 * <b>Server-side</b> utility class for {@link Language} enum.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.shared.Language
 */
public final class Languages {

	/**
	 * <p>
	 * Default language.
	 * </p>
	 * <p>
	 * This default value is set <em>only</em> when user has no cookie <b>and</b> no available language can be detected
	 * from HTTP request header.
	 * </p>
	 * <p>
	 * This default value should match the <b>fallback</b> {@code locale} property's value set into {@code Sigmah.gwt.xml}
	 * module file:
	 * 
	 * <pre>
	 * &lt;set-property-fallback name="locale" value="<b><u>en</u></b>" /&gt;
	 * </pre>
	 * 
	 * </p>
	 */
	public static final Language DEFAULT_LANGUAGE = Language.EN;

	/**
	 * HTTP request {@code Accept-Language} header name.
	 */
	private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";

	/**
	 * <p>
	 * Gets the current user {@link Language}.
	 * </p>
	 * <p>
	 * The method follows this pattern:
	 * <ol>
	 * <li>Determines language instance from language cookie in {@code request}. If cookie is missing or does not
	 * reference a valid available language, method attempts rule #2.</li>
	 * <li>Determines language instance from {@code request} headers ; see
	 * {@code http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4}. If language cannot be determined from
	 * request headers, method applies rule #3.</li>
	 * <li><em>Last resort (should most-likely never happen):</em> returns a default locale ; see
	 * {@link #DEFAULT_LANGUAGE}.</li>
	 * </ol>
	 * </p>
	 * 
	 * @param request
	 *          The HTTP request.
	 * @return The current user {@link Language}. If no language can be determined from {@code request}, the method
	 *         returns a default value (never returns {@code null}).
	 */
	public static Language getLanguage(final HttpServletRequest request) {

		// Attempt to retrieve locale from cookies.
		final Cookie[] cookies = request != null ? request.getCookies() : null;
		if (cookies != null) {
			for (final Cookie cookie : cookies) {
				if (Cookies.LANGUAGE_COOKIE.equals(cookie.getName())) {
					final Language language = Language.fromString(cookie.getValue());
					if (language != null) {
						return language;
					}
					break;
				}
			}
		}

		// Attempt to retrieve locale from request headers.
		final String acceptLanguageHeader = request != null ? request.getHeader(ACCEPT_LANGUAGE_HEADER) : null;
		if (StringUtils.isNotBlank(acceptLanguageHeader)) {

			for (final String acceptLanguageToken : acceptLanguageHeader.split(",|;|-|_")) {

				if (acceptLanguageToken.contains("=")) {
					continue; // Not a valid language token.
				}

				final Language language = Language.fromString(acceptLanguageToken);

				if (language != null) {
					return language;
				}
			}
		}

		// Last resort: default value.
		return DEFAULT_LANGUAGE;
	}

	/**
	 * Returns the given {@code language} or default language if {@code null}.<br/>
	 * Ensures that a valid language is always returned.
	 * 
	 * @param language
	 *          The language instance.
	 * @return The given {@code language} or default language if {@code null} (never returns {@code null}).
	 * @see Languages#DEFAULT_LANGUAGE
	 */
	public static final Language notNull(final Language language) {
		return language != null ? language : DEFAULT_LANGUAGE;
	}

	/**
	 * Returns the given {@code language} corresponding file suffix.
	 * 
	 * <pre>
	 * getFileSuffix(EN, null) -> "" (<em>Special case for default language</em>)
	 * getFileSuffix(FR, null) -> "_fr"
	 * getFileSuffix(ES, null) -> "_es"
	 * getFileSuffix(EN, ".properties") -> ".properties" (<em>Special case for default language</em>)
	 * getFileSuffix(FR, ".properties") -> "_fr.properties"
	 * getFileSuffix(ES, ".properties") -> "_es.properties"
	 * </pre>
	 * 
	 * @param language
	 *          The language instance, may be {@code null}.
	 * @param extension
	 *          (optional) The file extension to append at the end of the suffix. Should contain the extension separator.
	 * @return The given {@code language} corresponding file suffix or empty if default language, never {@code null}.
	 */
	public static final String getFileSuffix(final Language language, final String extension) {

		if (notNull(language) == DEFAULT_LANGUAGE) {
			// FIXME REMOVE THIS BLOCK IF "_en" I18N FILES ARE RESTORED
			return "" + StringUtils.trimToEmpty(extension);
		}

		return '_' + notNull(language).getLocale() + StringUtils.trimToEmpty(extension);
	}

	/**
	 * Utility class constructor (unused).
	 */
	private Languages() {
		// Only provides static methods.
	}

}
