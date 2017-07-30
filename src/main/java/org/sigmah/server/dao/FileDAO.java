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
	 *          The loading scope (see {@link LoadingScope} for details).
	 *          If {@code null}, the default {@link LoadingScope#LAST_VERSION} is used.
	 * @return The {@link FileVersion} list corresponding to the given {@code filesIds}.
	 */
	List<FileVersion> findVersions(Collection<Integer> filesIds, LoadingScope loadingScope);

	/**
	 * Finds the {@link FileVersion} list corresponding to all {@code filesIds} in the database.
	 * 
	 * @return The {@link FileVersion} list corresponding to all {@code filesIds}in the database.
	 */
	List<FileVersion> findAllVersions();
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
