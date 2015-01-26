package org.sigmah.server.conf;

import java.util.Date;

import org.sigmah.shared.conf.PropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties reloadable files accessor.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ReloadableProperties extends BasicProperties {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ReloadableProperties.class);

	/**
	 * Reaload period.
	 */
	@ReloadPeriod
	private int reloadPeriod;

	/**
	 * Last reload date.
	 */
	private Date lastReload;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void load() {

		try {

			if (LOG.isInfoEnabled()) {
				LOG.info("Reloading properties.");
			}

			super.load();

		} finally {
			lastReload = new Date();
		}

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
	 * {@inheritDoc}
	 */
	@Override
	public String getProperty(PropertyKey key) {
		checkLoadTimer();
		return super.getProperty(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProperty(PropertyKey key, String defaultValue) {
		checkLoadTimer();
		return super.getProperty(key, defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getBooleanProperty(PropertyKey key) {
		checkLoadTimer();
		return super.getBooleanProperty(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getIntegerProperty(PropertyKey key) {
		checkLoadTimer();
		return super.getIntegerProperty(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getIntegerProperty(PropertyKey key, Integer defaultValue) {
		checkLoadTimer();
		return super.getIntegerProperty(key, defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getLongProperty(PropertyKey key) {
		checkLoadTimer();
		return super.getLongProperty(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getLongProperty(PropertyKey key, Long defaultValue) {
		checkLoadTimer();
		return super.getLongProperty(key, defaultValue);
	}

}
