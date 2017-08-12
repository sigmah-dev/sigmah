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

import org.sigmah.server.conf.Properties;
import org.sigmah.server.conf.ReloadPeriod;
import org.sigmah.server.conf.ReloadableProperties;
import org.sigmah.server.file.BackupArchiveJobFactory;
import org.sigmah.server.file.BackupArchiveManager;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.file.LogoManager;
import org.sigmah.server.file.impl.BackupArchiveManagerImpl;
import org.sigmah.server.file.impl.LocalFileStorageProvider;
import org.sigmah.server.file.impl.LogoManagerImpl;
import org.sigmah.server.search.FilesSolrManager;
import org.sigmah.server.search.FilesSolrManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Configuration module.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ConfigurationModule extends AbstractModule {

	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {

		if (LOG.isInfoEnabled()) {
			LOG.info("Installing configuration module.");
		}

		bindConstant().annotatedWith(ReloadPeriod.class).to(60);
		bind(Properties.class).to(ReloadableProperties.class).in(Singleton.class);

		// Files storage.
		bind(FileStorageProvider.class).to(LocalFileStorageProvider.class).in(Singleton.class);
		bind(LogoManager.class).to(LogoManagerImpl.class).in(Singleton.class);

		// Backup Manager.
		bind(BackupArchiveManager.class).to(BackupArchiveManagerImpl.class).in(Singleton.class);
		install(new FactoryModuleBuilder().build(BackupArchiveJobFactory.class));
		
		//Solr Files Handler
		bind(FilesSolrManager.class).to(FilesSolrManagerImpl.class);
	}

}
