package org.sigmah.server.conf;

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
