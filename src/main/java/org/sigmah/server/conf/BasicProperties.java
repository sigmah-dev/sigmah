package org.sigmah.server.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.conf.PropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties files accessor.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class BasicProperties implements Properties {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BasicProperties.class);

	/**
	 * The list of the properties file to load.
	 */
	private ArrayList<String> propertiesFiles;

	/**
	 * The set of properties.
	 */
	private java.util.Properties properties;

	public BasicProperties() {
		load();
	}

	/**
	 * Loads the properties.
	 */
	protected void load() {

		propertiesFiles = new ArrayList<String>();
		propertiesFiles.add("sigmah.properties");
		propertiesFiles.add("version.properties");

		properties = new java.util.Properties();

		// Read properties from the properties files.
		for (final String file : propertiesFiles) {

			try (final InputStream is = BasicProperties.class.getClassLoader().getResourceAsStream(file)) {

				if (LOG.isInfoEnabled()) {
					LOG.info("Loading properties from file '" + file + "'.");
				}

				properties.load(is);

				if (LOG.isTraceEnabled()) {
					LOG.trace("Loaded properties: " + this.toString());
				}

			} catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Properties loading failure with file '" + file + "'.", e);
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProperty(PropertyKey key) {
		return getProperty(key, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProperty(PropertyKey key, String defaultValue) {
		if (key == null) {
			return defaultValue;
		}

		return properties.getProperty(key.getName(), defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getBooleanProperty(PropertyKey key) {
		return ClientUtils.isTrue(getProperty(key, null));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getIntegerProperty(PropertyKey key) {
		return getIntegerProperty(key, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getIntegerProperty(PropertyKey key, Integer defaultValue) {
		return ClientUtils.asInt(getProperty(key), defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getLongProperty(PropertyKey key) {
		return getLongProperty(key, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getLongProperty(PropertyKey key, Long defaultValue) {
		return ClientUtils.asLong(getProperty(key), defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.valueOf(properties);
	}

}
