package org.sigmah.server.i18n;

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
