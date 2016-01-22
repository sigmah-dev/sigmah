package org.sigmah.client.page;

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
 * Thrown of a page URL cannot be parsed.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class PageParsingException extends Exception {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = -7562901661618711604L;

	public PageParsingException() {
	}

	public PageParsingException(String message) {
		super(message);
	}

	public PageParsingException(Throwable cause) {
		super(cause);
	}

	public PageParsingException(String message, Throwable cause) {
		super(message, cause);
	}

}
