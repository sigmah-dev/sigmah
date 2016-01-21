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
 * Unauthorized access exception.
 * </p>
 * <p>
 * Thrown if a user try to execute an action with insufficient rights.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UnauthorizedAccessException extends CommandException {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = -227740094906497505L;

	public UnauthorizedAccessException() {
		// Serialization.
	}

	public UnauthorizedAccessException(String message) {
		super(message);
	}

}
