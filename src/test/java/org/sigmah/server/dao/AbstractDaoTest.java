package org.sigmah.server.dao;

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
