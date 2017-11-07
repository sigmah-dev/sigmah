package org.sigmah.client.ui.zone;

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

import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.util.ClientUtils;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Defines zones.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public enum Zone implements IsSerializable {

	// Zones tokens must be unique.

	APP_LOADER("app-loader"),

	ORG_BANNER("org-banner"),
	AUTH_BANNER("auth-banner"),
	OFFLINE_BANNER("offline-banner"),
	MENU_BANNER("menu-banner"),
	MESSAGE_BANNER("message-banner"),
	SEARCH_BANNER("search-banner"),

	;

	/**
	 * The zone token.
	 */
	private final String token;

	/**
	 * Initializes a new {@link Zone}.
	 * 
	 * @param token
	 *          The zone token.
	 * @throws IllegalArgumentException
	 *           If the {@code token} is invalid.
	 */
	private Zone(final String token) {
		if (ClientUtils.isBlank(token)) {
			throw new IllegalArgumentException("Invalid zone token: '" + token + "'.");
		}
		this.token = token;
	}

	/**
	 * Returns the zone token.
	 * 
	 * @return The zone token.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Retrieves the {@link Zone} corresponding to the given {@code token}.
	 * 
	 * @param token
	 *          The zone token.
	 * @return the {@link Zone} corresponding to the given {@code token} or {@code null} if no zone exists for this token.
	 */
	public static Zone fromString(final String token) {
		for (final Zone zone : values()) {
			if (zone.token.equals(token)) {
				return zone;
			}
		}
		return null;
	}

	/**
	 * Returns a new request for this page.
	 * 
	 * @return A new {@link ZoneRequest} instance for the current zone.
	 */
	public ZoneRequest request() {
		return new ZoneRequest(this);
	}

	/**
	 * A convenience method for calling {@code request().addData(key, value)}.
	 * 
	 * @param key
	 *          The data parameter key.
	 * @param value
	 *          The data parameter value.
	 * @return A {@code ZoneRequest} instance with the given parameter.
	 */
	public ZoneRequest requestWith(final RequestParameter key, final Object value) {
		return request().addData(key, value);
	}

}
