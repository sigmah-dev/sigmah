package org.sigmah.server.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.value.File;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.shared.dto.value.FileDTO.LoadingScope;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.value.File} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface FileDAO extends DAO<File, Integer> {

	/**
	 * Finds the {@link FileVersion} list corresponding to the given {@code filesIds}.
	 * 
	 * @param filesIds
	 *          The {@link File} id(s) collection.
	 * @param loadingScope
	 *          The loading scope (see {@link LoadingScope} for details).<br/>
	 *          If {@code null}, the default {@link LoadingScope#LAST_VERSION} is used.
	 * @return The {@link FileVersion} list corresponding to the given {@code filesIds}.
	 */
	List<FileVersion> findVersions(Collection<Integer> filesIds, LoadingScope loadingScope);

	/**
	 * Retrieves the {@link FileVersion} corresponding to the given {@code versionId}.
	 * 
	 * @param versionId
	 *          The {@link FileVersion} id to retrieve.
	 * @return The {@link FileVersion} corresponding to the given {@code versionId}.
	 */
	FileVersion getVersion(Integer versionId);

	/**
	 * Retrieves the {@link FileVersion} corresponding to the given {@code fileId} last version.
	 * 
	 * @param fileId
	 *          The {@link File} id which last version is retrieved.
	 * @return The {@link FileVersion} corresponding to the given {@code fileId} last version.
	 */
	FileVersion getLastVersion(Integer fileId);

	/**
	 * Creates a file or creates a new version based on the given properties.
	 * 
	 * @param properties
	 *          Properties of the file.
	 * @param physicalName
	 *          Physical identifier.
	 * @param size
	 *          Size of the file.
	 * @return The id of the just saved file.
	 */
	Integer saveOrUpdate(Map<String, String> properties, String physicalName, int size);
}
