package org.sigmah.client.util;

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
 * Client-side {@code toString} builder implementation.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.apache.commons.lang3.builder.ToStringBuilder
 */
public final class ToStringBuilder {

	/**
	 * To string properties separator.
	 */
	private static final String SEPARATOR = ", ";

	/**
	 * The inner buffer.
	 */
	private final StringBuilder buffer = new StringBuilder();

	/**
	 * The object instance (may be {@code null}).
	 */
	private final Object object;

	/**
	 * <p>
	 * Initializes a new {@code ToStringBuilder} for the given {@code object} instance.
	 * </p>
	 * 
	 * @param object
	 *          The object instance (may be {@code null}).
	 */
	public ToStringBuilder(final Object object) {
		this.object = object;
	}

	/**
	 * Appends the given {@code name} and {@code value} to the current builder.
	 * 
	 * @param name
	 *          The property name.
	 * @param value
	 *          The property value.
	 * @return the current builder instance.
	 */
	public ToStringBuilder append(final String name, final Object value) {

		appendSeparator();

		buffer.append(name);
		buffer.append('=');
		buffer.append(value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		final String className;

		if (object == null) {
			className = null;

		} else {
			className = object.getClass().getName();
		}

		return className + '[' + buffer + ']';
	}

	/**
	 * Appends comma separator if necessary.
	 * 
	 * @see #SEPARATOR
	 */
	private void appendSeparator() {
		if (buffer.length() > 0) {
			buffer.append(SEPARATOR);
		}
	}

}
