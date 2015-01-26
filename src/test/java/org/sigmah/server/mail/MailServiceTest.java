package org.sigmah.server.mail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.inject.ConfigurationModule;
import org.sigmah.server.inject.GuiceJUnitRunner;
import org.sigmah.server.inject.GuiceJUnitRunner.GuiceModules;
import org.sigmah.server.inject.I18nServerModule;
import org.sigmah.server.inject.PersistenceModule;
import org.sigmah.shared.Language;
import org.sigmah.shared.dto.referential.EmailKey;
import org.sigmah.shared.dto.referential.EmailKeyEnum;
import org.sigmah.shared.dto.referential.EmailType;

import com.google.inject.Inject;
import org.sigmah.server.inject.TestMailModule;

/**
 * JUnit test for the {@link MailService} Module
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({
								TestMailModule.class,
								ConfigurationModule.class,
								PersistenceModule.class,
								I18nServerModule.class
})
public class MailServiceTest {

	@Inject
	private MailService mailService;

	@Test
	public void sendEmail() {
		// Preparing the parameters for the email to be sent
		Map<EmailKey, String> parameters = new HashMap<EmailKey, String>();
		parameters.put(EmailKeyEnum.INVITING_USERNAME, "Machin");
		parameters.put(EmailKeyEnum.INVITING_EMAIL, "machin@truc.fr");
		parameters.put(EmailKeyEnum.USER_USERNAME, "truc");
		parameters.put(EmailKeyEnum.CHANGE_PASS_KEY, "hop");
		parameters.put(EmailKeyEnum.USER_LOGIN, "dumb login");
		parameters.put(EmailKeyEnum.USER_PASSWORD, "dumb p4ssw0rd");
		parameters.put(EmailKeyEnum.APPLICATION_LINK, "http://www.google.com");

		mailService.send(EmailType.INVITATION, parameters, Language.FR, "urd-sigmah@netapsys.fr");
		mailService.send(EmailType.INVITATION, parameters, null, "urd-sigmah@netapsys.fr");
	}

}
