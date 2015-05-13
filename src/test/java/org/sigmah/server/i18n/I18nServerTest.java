package org.sigmah.server.i18n;

import org.junit.Assert;
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
