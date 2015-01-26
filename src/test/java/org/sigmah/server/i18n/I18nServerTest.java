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
	public void testParam() {

		Assert.assertEquals("Vous n'êtes pas autorisé à exécuter cette action.", i18nServer.t(Language.FR, "navigation.unauthorized.action"));
		Assert.assertEquals("Bannière", i18nServer.t(Language.FR, "Admin_BANNER"));
		Assert.assertEquals("Banner", i18nServer.t(Language.EN, "Admin_BANNER"));
		Assert.assertEquals("Banner", i18nServer.t(null, "Admin_BANNER"));

	}

}
