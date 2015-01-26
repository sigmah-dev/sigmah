package org.sigmah.server.endpoint.file;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

public class FileModule extends AbstractModule {

    static final String REPOSITORY_NAME = "repository.name";
    static final String REPOSITORY_LOGOS = "repository.images";

    @Override
    protected void configure() {
        bind(FileManager.class).to(FileManagerImpl.class).in(Singleton.class);
        bind(LogoManager.class).to(LogoManagerImpl.class).in(Singleton.class);
        bind(FileStorageProvider.class).toProvider(FileStorageProviderProvider.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Singleton
    public static class FileStorageProviderProvider implements Provider<FileStorageProvider> {

        private Provider<FileStorageProvider> inner;

        private static final Logger log = Logger.getLogger(FileStorageProviderProvider.class);

        @Inject
        public FileStorageProviderProvider(Properties configProperties, Injector injector) {
            String providerClassName = configProperties.getProperty("repository.file_storage_provider_class");
            if (providerClassName == null) {
                inner = (Provider) injector.getProvider(LocalStorageProvider.class);
            } else {
                try {
                    Class providerClass = Class.forName(providerClassName);
                    inner = injector.getProvider(providerClass);
                } catch (Exception e) {
                    log.error("Failed to load file storage provider " + providerClassName
                            + ", using LocalStorageProvider", e);
                    inner = (Provider) injector.getProvider(LocalStorageProvider.class);
                }
            }
        }

        @Override
        public FileStorageProvider get() {
            return inner.get();
        }
    }

}
