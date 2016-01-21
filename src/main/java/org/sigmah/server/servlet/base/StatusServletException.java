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

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;

/**
 * Custom {@link ServletException} carrying a response status code.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see com.google.gwt.http.client.Response
 */
public class StatusServletException extends ServletException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8523719458748389335L;

	/**
	 * The response status code.
	 */
	private final int statusCode;

	public StatusServletException(final int statusCode) {
		this(statusCode, null, null);
	}

	public StatusServletException(final int statusCode, final Throwable cause) {
		this(statusCode, null, cause);
	}

	public StatusServletException(final int statusCode, final String message) {
		this(statusCode, message, null);
	}

	public StatusServletException(final int statusCode, final String message, final Throwable cause) {
		super(statusCode + (StringUtils.isNotBlank(message) ? ": " + message : ""), cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

}
