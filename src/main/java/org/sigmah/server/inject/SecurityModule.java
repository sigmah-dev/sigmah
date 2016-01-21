package org.sigmah.server.inject;

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
