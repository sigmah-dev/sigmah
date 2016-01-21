package org.sigmah.client.dispatch;

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
 * Implementations of this interface can be added to a {@link DispatchAsync} implementation to intercept exceptions
 * which return from further up the chain.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ExceptionHandler {

	/**
	 * Exception handler status enumeration.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum Status {

		/**
		 * Stops the result handling (does not execute command callback implementation).
		 */
		STOP,

		/**
		 * Continues the result handling process and transmits it to command callback implementation.
		 */
		CONTINUE,
		
		/**
		 * Switch the current state to offline and retry the current command.
		 */
		RETRY_OFFLINE
	}

	/**
	 * This method is called when an exception occurs. Return {@link Status#STOP} to indicate that the exception has been
	 * handled and further processing should not occur. Return {@link Status#CONTINUE} to indicate that further processing
	 * should occur.
	 * 
	 * @param e
	 *          The exception.
	 * @return The status after execution.
	 */
	Status onFailure(final Throwable e);

}
