package org.sigmah.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * Server side localization version of {@link UIConstants}
 * 
 * @author Tom Miette
 */
public class UIConstantsTranslator implements Translator {

	  /**
	   * Log.
	   */
 	  private final static Log log = LogFactory.getLog(UIConstantsTranslator.class);
	  /**
	   * The translation properties file prefix.
	   */ 
	  private static final String PROPERTIES_FILES_PREFIX = "org/sigmah/client/i18n/UIConstants";

	  /**
	   * The translation properties files.
	   */
	  private final HashMap<String, Properties> propertiesMap;

	  /**
	   * The GWT default locale.
	   */
	  private final Locale defaultLocale;

	  public UIConstantsTranslator(Locale locale) {
	    propertiesMap = new HashMap<String, Properties>();
	    defaultLocale = locale;
	  }

	  /**
	   * Loads and returns the properties set for the given locale.
	   * 
	   * @param locale
	   *          The locale.
	   * @return The properties set.
	   */
	  private Properties getProperties(Locale locale) {

	    final StringBuilder sb = new StringBuilder();
	    sb.append(PROPERTIES_FILES_PREFIX);
	    if (locale != null && !locale.equals(defaultLocale)) {
	      sb.append("_" + locale.getLanguage());
	    }
	    sb.append(".properties");

	    final String file = sb.toString();

	    Properties properties = propertiesMap.get(file);

	    if (properties == null) {

	      properties = new Properties();
	      propertiesMap.put(file, properties);

	      try {

	        if (log.isInfoEnabled()) {
	          log.info("Loading properties from file '" + file + "'.");
	        }

	        properties.load(this.getClass().getClassLoader().getResourceAsStream(file));

	      } catch (IOException e) {
	        if (log.isErrorEnabled()) {
	          log.error("Properties loading failure with file '" + file + "'.", e);
	        }
	      }
	    }

	    return properties;
	  }

	  @Override
	  public String translate(String key) {
	    return translate(key, null);
	  }

	  @Override
	  public String translate(String key, Locale locale) {
	    return getProperties(locale).getProperty(key);
	  }
	}