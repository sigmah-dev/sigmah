package org.sigmah.server.inject;

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
	}

}
