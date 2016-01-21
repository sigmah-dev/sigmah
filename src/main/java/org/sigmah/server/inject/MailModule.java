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

import org.sigmah.server.mail.MailSender;
import org.sigmah.server.mail.MailSenderImpl;
import org.sigmah.server.mail.MailService;
import org.sigmah.server.mail.ModelMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Module to install the mailer service.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class MailModule extends AbstractModule {

	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MailModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Installing mail module.");
		}

		// Binds the sender.
		bind(MailService.class).to(ModelMailService.class).in(Singleton.class);
		bind(MailSender.class).to(MailSenderImpl.class).in(Singleton.class);
	}

}
