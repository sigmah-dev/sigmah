package org.sigmah.server;

/**
 * Defines a properties provider.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 */
public interface Configurator {
	 /**
	   * Gets the property for the given key.
	   * 
	   * @param key
	   *          The property's key.
	   * @return The property's value or <code>null</code> if there is not property for this key.
	   */
	  String getProperty(String key);

	  /**
	   * Gets the property for the given key.
	   * 
	   * @param key
	   *          The property's key.
	   * @param defaultValue
	   *          The default value returned if the property isn't found.
	   * @return The property's value or the default value if there is not property for this key.
	   */
	  String getProperty(String key, String defaultValue);

	  /**
	   * Sets the given property.
	   * 
	   * @param key
	   *          The property's key.
	   * @param value
	   *          The property's value.
	   */
	  void setProperty(String key, String value);


	  /**
	   * Returns <code>true</code> if the given key exists.
	   * 
	   * @param key
	   *          The key.
	   * @return <code>true</code> if the key exists, <code>false</code> otherwise.
	   */
	  boolean containsKey(String key);
}
