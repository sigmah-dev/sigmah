package org.sigmah.server.file.util;

import org.sigmah.server.file.FileStorageProvider;

/**
 * Represents a file element.
 * 
 * @author Aurélien Ponçon
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FileElement extends RepositoryElement {

	/**
	 * The file's storage id (for the expected version). Use {@link FileStorageProvider} to open an InputStream
	 */
	private final String storageId;

	public FileElement(String id, String name, String storageId) {
		super(id, name);
		this.storageId = storageId;
	}

	public String getStorageId() {
		return storageId;
	}

}
