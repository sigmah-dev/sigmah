package org.sigmah.server.mail;

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
