package org.sigmah.server.dao;

import org.junit.runner.RunWith;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.server.inject.ConfigurationModule;
import org.sigmah.server.inject.GuiceJUnitRunner;
import org.sigmah.server.inject.GuiceJUnitRunner.GuiceModules;
import org.sigmah.server.inject.MapperModule;
import org.sigmah.server.inject.PersistenceModule;
import org.sigmah.server.inject.SecurityModule;

/**
 * Abstract DAO test class initializing {@code Injector}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({
								ConfigurationModule.class,
								PersistenceModule.class,
								SecurityModule.class,
								MapperModule.class
})
public abstract class AbstractDaoTest extends EntityManagerProvider {

}
