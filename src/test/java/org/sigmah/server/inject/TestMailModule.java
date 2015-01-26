package org.sigmah.server.inject;

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
