package org.sigmah.shared.dispatch;

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
 * <p>
 * Command exception.
 * </p>
 * <p>
 * Exception thrown by services when there is a low-level problem while processing a command execution.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CommandException extends DispatchException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2274470850863936403L;

	protected CommandException() {
		// Serialization.
	}

	public CommandException(final String message) {
		super(message);
	}

	public CommandException(final Throwable cause) {
		super(cause);
	}

	public CommandException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
