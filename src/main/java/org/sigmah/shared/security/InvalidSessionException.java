package org.sigmah.shared.security;

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

import org.sigmah.shared.dispatch.CommandException;

/**
 * <p>
 * Invalid session exception.
 * </p>
 * <p>
 * Thrown when user session token is invalid (expired, corrupted, etc.).
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class InvalidSessionException extends CommandException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7539134845675500300L;

	public InvalidSessionException() {
		// Serialization.
	}

	public InvalidSessionException(String message) {
		super(message);
	}
}
