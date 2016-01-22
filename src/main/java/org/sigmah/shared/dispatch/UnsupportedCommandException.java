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

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

/**
 * Exception thrown if no handler can be found for a command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UnsupportedCommandException extends CommandException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7759072862530169401L;

	protected UnsupportedCommandException() {
		// Serialization.
	}

	@SuppressWarnings({ "unchecked"
	})
	public UnsupportedCommandException(final Command<? extends Result> command) {
		this((Class<? extends Command<? extends Result>>) command.getClass());
	}

	public UnsupportedCommandException(final Class<? extends Command<? extends Result>> commandClass) {
		super("No handler is registered for " + commandClass.getName());
	}

}
