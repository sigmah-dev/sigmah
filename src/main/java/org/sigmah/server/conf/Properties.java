package org.sigmah.server.conf;

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

import org.sigmah.shared.conf.PropertyKey;

/**
 * Properties files accessor.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface Properties {

	/**
	 * Gets the property value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The property key.
	 * @return the property value corresponding to the given {@code key} or {@code null}.
	 */
	String getProperty(PropertyKey key);

	/**
	 * Gets the property value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The property key.
	 * @param defaultValue
	 *          The default value to return if no property with given {@code key} is found.
	 * @return the property value corresponding to the given {@code key} or {@code defaultValue}.
	 */
	String getProperty(PropertyKey key, String defaultValue);

	/**
	 * Gets the boolean property value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The property key.
	 * @return the property boolean value corresponding to the given {@code key} ({@code false} if property not found).
	 */

	boolean getBooleanProperty(PropertyKey key);

	/**
	 * Gets the Integer property value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The property key.
	 * @return the property Integer value corresponding to the given {@code key} ({@code null} if property not found).
	 */
	Integer getIntegerProperty(PropertyKey key);

	/**
	 * Gets the int property value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The property key.
	 * @param defaultValue
	 *          The default value to return if no property with given {@code key} is found.
	 * @return the property int value corresponding to the given {@code key} ({@code defaultValue} if property not found).
	 */
	Integer getIntegerProperty(PropertyKey key, Integer defaultValue);

	/**
	 * Gets the Long property value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The property key.
	 * @return the property Long value corresponding to the given {@code key} ({@code null} if property not found).
	 */
	Long getLongProperty(PropertyKey key);

	/**
	 * Gets the long property value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The property key.
	 * @param defaultValue
	 *          The default value to return if no property with given {@code key} is found.
	 * @return the property long value corresponding to the given {@code key} ({@code defaultValue} if property not
	 *         found).
	 */
	Long getLongProperty(PropertyKey key, Long defaultValue);

}
