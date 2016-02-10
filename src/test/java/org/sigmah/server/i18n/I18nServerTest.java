package org.sigmah.server.i18n;

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


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.inject.ConfigurationModule;
import org.sigmah.server.inject.GuiceJUnitRunner;
import org.sigmah.server.inject.GuiceJUnitRunner.GuiceModules;
import org.sigmah.server.inject.I18nServerModule;
import org.sigmah.server.inject.PersistenceModule;
import org.sigmah.shared.Language;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.Properties;

/**
 * JUnit test for the {@link I18nServer} Module
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({
								PersistenceModule.class,
								ConfigurationModule.class,
								I18nServerModule.class
})
// TODO use Assume to skip test if environment is not set
@Ignore("Test requires a database environment")
public class I18nServerTest {

	@Inject
	private I18nServer i18nServer;

	@Test
	public void testParam() throws IOException {
		final Properties frenchProperties = new Properties();
		frenchProperties.load(I18nServer.class.getResourceAsStream("/org/sigmah/client/i18n/UIConstants_fr.properties"));
		
		final Properties englishProperties = new Properties();
		englishProperties.load(I18nServer.class.getResourceAsStream("/org/sigmah/client/i18n/UIConstants.properties"));

		Assert.assertEquals(frenchProperties.getProperty("navigation.unauthorized.action"), i18nServer.t(Language.FR, "navigation.unauthorized.action"));
		Assert.assertEquals(frenchProperties.getProperty("Admin_BANNER"), i18nServer.t(Language.FR, "Admin_BANNER"));
		Assert.assertEquals(englishProperties.getProperty("Admin_BANNER"), i18nServer.t(Language.EN, "Admin_BANNER"));
		Assert.assertEquals(englishProperties.getProperty("Admin_BANNER"), i18nServer.t(null, "Admin_BANNER"));

	}

}
