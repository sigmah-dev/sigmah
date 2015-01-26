package org.sigmah.server.inject;

import org.sigmah.server.security.Authenticator;
import org.sigmah.server.security.SecureSessionValidator;
import org.sigmah.server.security.impl.AuthenticationSecureSessionValidator;
import org.sigmah.server.security.impl.DatabaseAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Security module.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SecurityModule extends AbstractModule {

	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SecurityModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {

		if (LOG.isInfoEnabled()) {
			LOG.info("Installing security module.");
		}

		bind(SecureSessionValidator.class).to(AuthenticationSecureSessionValidator.class).in(Singleton.class);
		bind(Authenticator.class).to(DatabaseAuthenticator.class).in(Singleton.class);
	}

}
