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

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.sigmah.server.mail.DummyMailSender;
import org.sigmah.server.mail.MailSender;
import org.sigmah.server.mail.MailService;
import org.sigmah.server.mail.ModelMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module to install the test version of the mailer service.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TestMailModule extends AbstractModule {
	
	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MailModule.class);

	@Override
	protected void configure() {
		LOG.info("Installing test mail module.");

		// Binds the sender.
		bind(MailService.class).to(ModelMailService.class).in(Singleton.class);
		bind(MailSender.class).to(DummyMailSender.class).in(Singleton.class);
	}
	
}
