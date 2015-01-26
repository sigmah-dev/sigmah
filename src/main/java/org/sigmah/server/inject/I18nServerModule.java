package org.sigmah.server.inject;

import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.i18n.I18nServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Module to install the I18nServer service.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class I18nServerModule extends AbstractModule {

	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(I18nServerModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Installing I18nServer module.");
		}

		// Binds the service.
		bind(I18nServer.class).to(I18nServerImpl.class).in(Singleton.class);
	}

}
