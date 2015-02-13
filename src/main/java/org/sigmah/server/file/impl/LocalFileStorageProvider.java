package org.sigmah.server.file.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sigmah.server.conf.Properties;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.shared.conf.PropertyKey;

import com.google.inject.Inject;

/**
 * Provides storage for the contents of uploaded files.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LocalFileStorageProvider implements FileStorageProvider {

	/**
	 * Injected application properties.
	 */
	private final Properties properties;

	@Inject
	public LocalFileStorageProvider(final Properties properties) {
		this.properties = properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputStream create(final String storageId) throws IOException {

		return Files.newOutputStream(Paths.get(getStorageRootPath(), storageId));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream open(final String storageId) throws IOException {

		return Files.newInputStream(Paths.get(getStorageRootPath(), storageId));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean delete(final String storageId) throws IOException {

		return Files.deleteIfExists(Paths.get(getStorageRootPath(), storageId));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long copy(InputStream input, String fileId, CopyOption... options) throws IOException {
		Path target = Paths.get(getStorageRootPath(), fileId);
		return Files.copy(input, target, options);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exists(String storageId) {
		return Files.exists(Paths.get(getStorageRootPath(), storageId));
	}

	/**
	 * Returns the files storage root directory path.
	 * 
	 * @return The files storage root directory path.
	 */
	private String getStorageRootPath() {
		return properties.getProperty(PropertyKey.FILE_REPOSITORY_NAME);
	}

}
