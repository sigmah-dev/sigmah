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

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.page.RequestParameter;

/**
 * Zone request.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ZoneRequest {

	private Zone zone;
	private Map<RequestParameter, Object> dataParameters; // Can be null.

	public ZoneRequest(final Zone zone) {
		this(zone, null);
	}

	public ZoneRequest(final Zone zone, final Map<RequestParameter, Object> dataParameters) {
		this.zone = zone;
		this.dataParameters = dataParameters;
	}

	public Zone getZone() {
		return zone;
	}

	/**
	 * <p>
	 * Adds a new <b>data object</b> parameter to the current {@code ZoneRequest} with the given data parameter
	 * {@code key} and {@code value}.
	 * </p>
	 * <p>
	 * If a <b>data object</b> parameter with the same {@code key} was previously specified, the returned request contains
	 * the new value.
	 * </p>
	 * 
	 * @param key
	 *          The new data parameter key.
	 * @param value
	 *          The new data parameter value.
	 * @return The {@code PageRequest} instance with its new <b>data object</b> parameter.
	 */
	public ZoneRequest addData(final RequestParameter key, final Object value) {
		if (dataParameters == null) {
			dataParameters = new HashMap<RequestParameter, Object>(1);
		}
		dataParameters.put(key, value);
		return this;
	}

	/**
	 * Returns the <b>data object</b> contained in the zone request instance.
	 * 
	 * @param <T>
	 *          Data type.
	 * @param key
	 *          The data parameter key.
	 * @return The given {@code key} corresponding data contained in the page request, or {@code null} if the data doesn't
	 *         exist or if its type is not {@code T}.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getData(final RequestParameter key) {

		if (dataParameters == null) {
			return null;
		}

		try {

			return (T) dataParameters.get(key);

		} catch (final ClassCastException e) {
			return null;
		}
	}

}
