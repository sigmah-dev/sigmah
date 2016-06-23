package org.sigmah.server.servlet.util;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.validation.ConstraintViolation;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.domain.User;

import com.google.gwt.dom.client.Style.Display;

/**
 * Servlets utility class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class Servlets {

	/**
	 * UTF-8 charset.
	 */
	public static final String UTF8_CHARSET = StandardCharsets.UTF_8.displayName();

	/**
	 * Reads all the content of the given input stream.
	 * 
	 * @param in
	 *          The input stream.
	 * @return The input stream content as a string.
	 * @throws IOException
	 */
	public static String readAll(final InputStream in) throws IOException {

		final StringBuilder sb = new StringBuilder();

		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

		}

		return sb.toString();
	}

	/**
	 * Returns the given {@code user} corresponding <em>loggable</em> string.
	 * If the {@code user} is {@code null}, the method returns "<em>unknow</em>" value.
	 * 
	 * @param user
	 *          The user instance (may be {@code null}).
	 * @return the given {@code user} corresponding <em>loggable</em> string.
	 */
	public static String logUser(final User user) {
		if (user == null) {
			return "unknown";
		}
		return User.getUserShortName(user) + " (" + user.getId() + ')';
	}

	/**
	 * Returns the given {@code violations} corresponding <em>loggable</em> string.
	 * 
	 * @param violations
	 *          The constraint violations collection.
	 * @return The given {@code violations} corresponding <em>loggable</em> string.
	 */
	public static String logConstraints(final Collection<ConstraintViolation<?>> violations) {

		final StringBuilder sb = new StringBuilder();

		if (CollectionUtils.isEmpty(violations)) {
			return sb.toString();
		}

		for (final ConstraintViolation<?> violation : violations) {
			sb.append("Constraint violation[");
			sb.append("Class: ");
			sb.append(violation.getRootBeanClass().getCanonicalName());
			sb.append(" ; ");
			sb.append("Property: ");
			sb.append(violation.getPropertyPath().iterator().next().getName());
			sb.append(" ; ");
			sb.append("Message: ");
			sb.append(violation.getMessage());
			sb.append(" ; ");
			sb.append("Invalid value: ");
			sb.append(String.valueOf(violation.getInvalidValue()));
			sb.append("]\n");
		}

		return sb.toString();
	}

	/**
	 * Returns the given {@code displayed} corresponding CSS {@code display} property value.
	 * 
	 * <pre>
	 * cssDisplay(true) → 'block'
	 * cssDisplay(false) → 'none'
	 * </pre>
	 * 
	 * @param displayed
	 *          {@code true} to get <em>block</em> display, {@code false} to get <em>none</em> display.
	 * @return The given {@code displayed} corresponding CSS {@code display} property value.
	 */
	public static String cssDisplay(final boolean displayed) {
		return displayed ? Display.BLOCK.getCssName() : Display.NONE.getCssName();
	}

	/**
	 * Utility class private constructor.
	 */
	private Servlets() {
		// Provides only static methods.
	}

}
