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


import java.io.Serializable;

import org.sigmah.client.util.ToStringBuilder;

/**
 * An abstract superclass for exceptions that can be thrown by the Dispatch system.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class DispatchException extends Exception implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6473670236082515600L;

	private String causeClassname;

	protected DispatchException() {
		// Serialization.
	}

	public DispatchException(final String message) {
		super(message);
	}

	public DispatchException(final Throwable cause) {
		super(cause);
		if(cause != null) {
			this.causeClassname = cause.getClass().getName();
		}
	}

	public DispatchException(final String message, final Throwable cause) {
		super(message + " (" + cause.getMessage() + ")");
		this.causeClassname = cause.getClass().getName();
	}

	public String getCauseClassname() {
		return causeClassname;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("Exception", super.toString());
		builder.append("Cause", causeClassname);
		return builder.toString();
	}

}
