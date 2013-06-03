package org.sigmah.server;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Implementation of a configurator which loads the properties from local properties files. The properties are
 * periodically reloaded.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 */
public class ReloadPropertiesConfigurator implements Configurator {

	/**
	   * Log.
	   */
	  private static final Logger log = LoggerFactory.getLogger(ReloadPropertiesConfigurator.class);

	  /**
	   * The set of properties.
	   */
	  private final PropertiesFilesSet propertiesFilesSet;

	  /**
	   * The set of properties.
	   */
	  private final Properties properties;

	  /**
	   * Period in seconds to reload the configurator.
	   */
	  private final int reloadPeriod;

	  /**
	   * Last reload date.
	   */
	  private Date lastReload;

	  @Inject
	  protected ReloadPropertiesConfigurator(PropertiesFilesSet propertiesFilesSet,@ReloadConfiguratorPeriod int reloadPeriod) {

	    this.propertiesFilesSet = propertiesFilesSet;
	    this.reloadPeriod = reloadPeriod;

	    properties = new Properties();
	    load();

	  }

	  /**
	   * {@inheritDoc}
	   */
	  @Override
	  public String getProperty(String key) {
	    checkLoadTimer();
	    return properties.getProperty(key);
	  }

	  /**
	   * {@inheritDoc}
	   */
	  @Override
	  public String getProperty(String key, String defaultValue) {
	    checkLoadTimer();
	    return properties.getProperty(key, defaultValue);
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @throws UnsupportedOperationException
	   *           The value of a property cannot be set in properties file.
	   */
	  @Override
	  public final void setProperty(String key, String value) {
	    throw new UnsupportedOperationException("Cannot set a property value in properties file.");
	  }

	  /**
	   * {@inheritDoc}
	   */
	  @Override
	  public boolean containsKey(String key) {
	    return properties.containsKey(key);
	  }

	  /**
	   * Reloads the properties if necessary.
	   */
	  private synchronized void checkLoadTimer() {
	    if ((new Date().getTime() - lastReload.getTime()) / 1000 >= reloadPeriod) {
	      load();
	    }
	  }

	  /**
	   * Load the properties.
	   */
	  private void load() {

	    properties.clear();

	    // Read properties from the properties files.
	    for (final String file : propertiesFilesSet.getFiles()) {

	      try {

	        if (log.isDebugEnabled()) {
	          log.debug("Loading properties from file '" + file + "'.");
	        }
	        properties.load(this.getClass().getClassLoader().getResourceAsStream(file));

	      } catch (IOException e) {
	        if (log.isWarnEnabled()) {
	          log.warn("Properties loading failure with file '" + file + "'.", e);
	        }
	      }
	    }

	    lastReload = new Date();

	  }

}
