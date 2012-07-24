package org.sigmah.server;

import java.util.Locale;

/**
 * Defines a translator.
 * 
 * @author Tom Miette
 */

public interface Translator {
	/**
	   * Gets the translation string for the given key with the default locale.
	   * 
	   * @param key
	   *          The translation key.
	   * @return The translated string.
	   */
	  public String translate(String key);

	  /**
	   * Gets the translation string for the given key and the given locale.
	   * 
	   * @param key
	   *          The translation key.
	   * @param locale
	   *          The locale.
	   * @return The translated string.
	   */
	  public String translate(String key, Locale locale);
}
