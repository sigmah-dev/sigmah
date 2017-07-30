package org.sigmah.server.dao.impl;

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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.value.File;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.domain.value.Value;
import org.sigmah.shared.dto.value.FileDTO.LoadingScope;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import org.sigmah.server.domain.util.DomainFilters;
import static org.sigmah.shared.util.ValueResultUtils.normalizeFileName;

/**
 * {@link FileDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class FileHibernateDAO extends AbstractDAO<File, Integer> implements FileDAO {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDispatch.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FileVersion> findVersions(final Collection<Integer> filesIds, LoadingScope loadingScope) {

		if (CollectionUtils.isEmpty(filesIds)) {
			throw new IllegalArgumentException("Invalid files ids collection.");
		}

		if (loadingScope == null) {
			loadingScope = LoadingScope.LAST_VERSION;
		}

		// NOTE : StringBuilder has been removed here since all the strings used
		// here are constants.
		final String request;

		switch (loadingScope) {
		case ALL_VERSIONS:
			// Retrieves all versions of each file.
			request = "SELECT " + "  fv " + "FROM " + "  File f INNER JOIN f.versions fv " + "WHERE "
					+ "  f.id IN (:filesIds)";
			break;

		case LAST_VERSION:
			// Retrieves only the last version of each file.
			request = "SELECT " + "  fv " + "FROM " + "  File f INNER JOIN f.versions fv " + "WHERE "
					+ "  f.id IN (:filesIds) " + "  AND fv.versionNumber IN ("
					+ "    SELECT max(fv2.versionNumber) FROM FileVersion fv2 WHERE fv2.parentFile = f" + "  )";
			break;

		case LAST_VERSION_FROM_NOT_DELETED_FILES:
			// Retrieves only the last version of each file if the file has not
			// been deleted.
			request = "SELECT " + "  fv " + "FROM " + "  File f INNER JOIN f.versions fv " + "WHERE "
					+ "  f.id IN (:filesIds) " + "  AND f.dateDeleted IS NULL " + "  AND fv.versionNumber IN ("
					+ "    SELECT max(fv2.versionNumber) FROM FileVersion fv2 WHERE fv2.parentFile = f" + "  )";
			break;

		default:
			throw new IllegalArgumentException("Invalid file versions loading mode.");
		}

		final TypedQuery<FileVersion> query = em().createQuery(request, FileVersion.class);
		query.setParameter("filesIds", filesIds);

		return query.getResultList();
	}

	@Override
	public List<FileVersion> findAllVersions() {
		final String request;
		 //select last versions of all files which are not deleted
		 request = "SELECT "
		 + " fv "
		 + "FROM "
		 + " File f INNER JOIN f.versions fv "
		 + "WHERE "
		 + " f.dateDeleted IS NULL "
		 + " AND fv.versionNumber IN ("
		 + " SELECT max(fv2.versionNumber) FROM FileVersion fv2 WHERE"
		 + " fv2.parentFile = f"
		 + " )";
		//System.out.println("GUBI " + request);
		List<FileVersion> res = null;
		try {
			DomainFilters.disableUserFilter(em());
			//hope this works to get all the files
			System.out.println("This should not be null also:" + em());
			final TypedQuery<FileVersion> query = em().createQuery(request, FileVersion.class);
			res = query.getResultList();
		} catch (RuntimeException e) {
			System.out.println("Here's an error!" + em());
			e.printStackTrace();
		}
		//System.out.println("GUBI " + res.toString());
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileVersion getVersion(final Integer versionId) {

		final TypedQuery<FileVersion> query = em().createQuery("SELECT fv FROM FileVersion fv WHERE fv.id = :id",
				FileVersion.class);
		query.setParameter("id", versionId);

		return query.getSingleResult();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileVersion getLastVersion(Integer fileId) {

		final TypedQuery<FileVersion> query = em().createQuery(
				"SELECT fv FROM FileVersion fv WHERE fv.parentFile.id = :fileId ORDER BY fv.versionNumber DESC",
				FileVersion.class);

		query.setParameter("fileId", fileId);
		query.setMaxResults(1);

		return query.getSingleResult();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer saveOrUpdate(final Map<String, String> properties, final String physicalName, final int size) {

		// Uploaded file's id.
		Integer id = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_ID));

		// Author id.
		final int authorId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_AUTHOR), 0);

		try {

			if (id == null) {
				// New file (first version).
				id = saveNewFile(properties, physicalName, size, authorId);

			} else {
				// New version.
				id = saveNewVersion(properties, physicalName, size, id, authorId);
			}

		} catch (IOException e) {
			final String name = properties.get(FileUploadUtils.DOCUMENT_NAME);
			throw new IllegalStateException(
					"Error while trying to save the file '" + name + "' (id #" + id + ") for author #" + authorId + ".",
					e);
		}

		return id;
	}

	/**
	 * Saves a new file.
	 * 
	 * @param properties
	 *            The properties map of the uploaded file (see
	 *            {@link FileUploadUtils}).
	 * @param physicalName
	 *            The uploaded file content.
	 * @param size
	 *            Size of the uploaded file.
	 * @param authorId
	 *            The author id.
	 * @return The id of the just saved file.
	 * @throws IOException
	 */
	@Transactional
	protected Integer saveNewFile(Map<String, String> properties, String physicalName, int size, int authorId)
			throws IOException {

		final EntityManager em = em();

		LOGGER.debug("[saveNewFile] New file.");

		// --------------------------------------------------------------------
		// STEP 1 : saves the file.
		// --------------------------------------------------------------------
		LOGGER.debug("[saveNewFile] Saves the new file.");
		final File file = new File();

		// Gets the details of the name of the file.
		final String fullName = ValueResultUtils.normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
		final int index = fullName.indexOf('.');

		final String name = index > 0 ? fullName.substring(0, index) : fullName;
		final String extension = index > 0 && index < fullName.length() ? fullName.substring(index + 1) : null;

		file.setName(name);

		// Creates and adds the new version.
		file.addVersion(createVersion(1, name, extension, authorId, physicalName, size));

		em.persist(file);

		// --------------------------------------------------------------------
		// STEP 2 : gets the current value for this list of files.
		// --------------------------------------------------------------------

		// Element id.
		final int elementId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT), 0);

		// Project id.
		final int projectId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_PROJECT), 0);

		// Retrieving the current value
		final TypedQuery<Value> query = em.createQuery(
				"SELECT v FROM Value v WHERE v.containerId = :projectId and v.element.id = :elementId", Value.class);
		query.setParameter("projectId", projectId);
		query.setParameter("elementId", elementId);

		Value currentValue = null;

		try {
			currentValue = query.getSingleResult();
		} catch (NoResultException nre) {
			// No current value
		}

		// --------------------------------------------------------------------
		// STEP 3 : creates or updates the value with the new file id.
		// --------------------------------------------------------------------

		// The value already exists, must update it.
		if (currentValue != null) {
			currentValue.setLastModificationAction('U');

			// Sets the value (adds a new file id).
			currentValue.setValue(
					currentValue.getValue() + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + String.valueOf(file.getId()));
		}
		// The value for this list of files doesn't exist already, must
		// create it.
		else {
			currentValue = new Value();

			// Creation of the value
			currentValue.setLastModificationAction('C');

			// Parent element
			final FlexibleElement element = em.find(FlexibleElement.class, elementId);
			currentValue.setElement(element);

			// Container
			currentValue.setContainerId(projectId);

			// Sets the value (one file id).
			currentValue.setValue(String.valueOf(file.getId()));
		}

		// Modifier
		final User user = em.find(User.class, authorId);
		currentValue.setLastModificationUser(user);

		// Last update date
		currentValue.setLastModificationDate(new Date());

		// Saves or updates the new value.
		em.merge(currentValue);

		return file.getId();
	}

	/**
	 * Saves a new file.
	 * 
	 * @param properties
	 *            The properties map of the uploaded file (see
	 *            {@link FileUploadUtils}).
	 * @param physicalName
	 *            The uploaded file content.
	 * @param size
	 *            Size of the uploaded file.
	 * @param id
	 *            The file which gets a new version.
	 * @param authorId
	 *            The author id.
	 * @return The file id (must be the same as the parameter).
	 * @throws IOException
	 */
	@Transactional
	protected Integer saveNewVersion(Map<String, String> properties, String physicalName, int size, Integer id,
			int authorId) throws IOException {

		final EntityManager em = em();

		LOGGER.debug("[save] New file version.");

		// Gets the details of the name of the file.
		final String fullName = normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
		final int index = fullName.indexOf('.');

		final String name = index > 0 ? fullName.substring(0, index) : fullName;
		final String extension = index > 0 && index < fullName.length() ? fullName.substring(index + 1) : null;

		// Creates and adds the new version.
		final File file = em.find(File.class, Integer.valueOf(id));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[save] Found file: " + file.getName() + ".");
		}

		Integer versionNumber;

		DomainFilters.disableDeletedFilter(em);

		final Query query = em.createQuery(
				"SELECT max(fv.versionNumber)+1 AS newVersionNumber FROM FileVersion AS fv WHERE parentFile=:parentFile");
		query.setParameter("parentFile", file);
		versionNumber = (Integer) query.getSingleResult();
		if (versionNumber == null) {
			versionNumber = 0;
		}

		final FileVersion version = createVersion(versionNumber, name, extension, authorId, physicalName, size);
		version.setComments(properties.get(FileUploadUtils.DOCUMENT_COMMENTS));
		file.addVersion(version);

		em.persist(file);

		return file.getId();
	}

	/**
	 * Creates a file version with the given number and author.
	 * 
	 * @param versionNumber
	 *            The version number.
	 * @param name
	 *            The version name.
	 * @param extension
	 *            The version extension.
	 * @param authorId
	 *            The author id.
	 * @param content
	 *            The version content.
	 * @return The version just created.
	 * @throws IOException
	 */
	private static FileVersion createVersion(int versionNumber, String name, String extension, int authorId,
			String physicalName, int size) throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[createVersion] Creates a new file version # + " + versionNumber + ".");
		}

		final FileVersion version = new FileVersion();

		// Sets attributes.
		version.setVersionNumber(versionNumber);
		version.setName(name);
		version.setExtension(extension);
		version.setAddedDate(new Date());
		version.setSize((long) size);
		final User user = new User();
		user.setId(authorId);
		version.setAuthor(user);

		// Saves content.
		version.setPath(physicalName);

		return version;
	}

}
