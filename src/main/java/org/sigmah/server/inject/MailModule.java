package org.sigmah.server.inject;

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
